package com.elixir.springframework.fuse.context;


import com.elixir.springframework.fuse.config.AppConstant;
import com.elixir.springframework.fuse.config.ConfigUtil;
import com.elixir.springframework.fuse.persistence.PersistenceUnitBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by elixir on 3/2/16.
 */
public class DefaultApplicationManager implements ApplicationManager, ApplicationListener<ContextRefreshedEvent>, ApplicationContextInitializer {
    private Map<String, Module> moduleConfiguration;
    private Properties configProperties;
    private Map<Module, Map<String, PersistenceUnitBuilder>> persistenceUnitConfiguration;
    private Logger logger;
    private final String appPackageScan;
    private final String appModuleName;
    private final String defaultAppName;

    public DefaultApplicationManager(Class<?> appClass) {
        String appPackage=appClass.getPackage().getName();
        Module module = appClass.getAnnotation(Module.class);
        this.appPackageScan=appPackage.substring(0, appPackage.lastIndexOf("."));
        this.appModuleName=module!=null?module.name():"application";
        this.defaultAppName = appClass.getSimpleName();
    }

    public DefaultApplicationManager(String appScanPackage,String appModuleName,String defaultAppName) {
        this.appPackageScan = appScanPackage;
        this.appModuleName=appModuleName!=null?appModuleName:"application";
        this.defaultAppName=defaultAppName;
    }

    @Override
    public Map<String, Module> moduleConfiguration() {
        if (moduleConfiguration == null)
            moduleConfiguration = new HashMap<>();
        return moduleConfiguration;
    }

    @Override
    public Properties configProperties() {
        if (configProperties == null)
            configProperties = new Properties();
        return configProperties;
    }

    public Map<Module, Map<String, PersistenceUnitBuilder>> persistenceUnitConfiguration() {
        if (persistenceUnitConfiguration == null)
            persistenceUnitConfiguration = new HashMap<>();
        return persistenceUnitConfiguration;
    }

    private Logger getLogger() {
        if (logger == null)
            logger = LoggerFactory.getLogger(this.getClass());
        return logger;
    }

    /*private Map<Module, ConfigurableApplicationContext> moduleContextConfiguration() {
        if (moduleContextConfiguration == null)
            moduleContextConfiguration = new HashMap<>();
        return moduleContextConfiguration;
    }*/
    @Override //throw exception on failure here
    public void onStartup(Object context) {
        String appName = defaultAppName;
        if (context instanceof ServletContext) {
            ServletContext servletContext = (ServletContext) context;
            appName = servletContext.getContextPath().replaceAll("/", "");
        }
        this.moduleConfiguration = initModuleConfiguration(context);
        this.configProperties = initConfigProperties(appName);
    }

    /**
     * SpringBoot context initializer: assign ROOT context to app module
     * if autoRegister is disabled (a dispatcher servlet is not assigned)
     */
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        if (this.moduleConfiguration != null) {
            Module appModule = this.moduleConfiguration.get(appModuleName);
            if (appModule!=null && !appModule.autoRegister()) {
                applicationContext.setId(appModule.name());
            }
            //preload PU
            if (persistenceUnitConfiguration().isEmpty()) {
                this.moduleConfiguration.values().stream().forEach(module -> {
                    initPersistenceUnitConfiguration(module);
                });
            }
        }
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        bindPersistenceToContext((ConfigurableApplicationContext) event.getApplicationContext());
    }

    private Map<String, Module> initModuleConfiguration(Object context) {
        ServletContext servletContext = null;
        if (context instanceof ServletContext)
            servletContext = (ServletContext) context;
        // Class<?> clazz = AnnotationUtils.findAnnotationDeclaringClass(Configuration.class, null);
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Module.class));

        Map<String, Module> moduleMap = new HashMap<>();
        Map<String, ConfigurableApplicationContext> contextMap = new HashMap<>();
        Set<BeanDefinition> beanDefinitions = appPackageScan == null ? Collections.emptySet() : scanner.findCandidateComponents(appPackageScan);
        for (BeanDefinition bd : beanDefinitions) {
            try {
                Class<?> moduleClass = Class.forName(bd.getBeanClassName());
                Module module = moduleClass.getAnnotation(Module.class);
                if (module != null) {
                    if (moduleMap.entrySet().stream().anyMatch(entry -> entry.getKey().equalsIgnoreCase(module.name())))
                        throw new IllegalStateException("Duplicate module name : " + module.name());

                    if (module.autoRegister()) {
                        AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
                        applicationContext.register(moduleClass);
                        applicationContext.setId(module.name());
                        System.out.println("MODULE=" + module.name());
                        //applicationContext.getBeanFactory()
                        //applicationContext.setAllowBeanDefinitionOverriding(true);
                        //applicationContext.addApplicationListener(new AppListener());
                        if (servletContext != null) {
                            DispatcherServlet dispatcherServlet = new DispatcherServlet(applicationContext);
                            ServletRegistration.Dynamic servlet = servletContext.addServlet(module.name(), dispatcherServlet);
                            servlet.setLoadOnStartup(module.loadOnStartup());
                            servlet.addMapping(module.urlPatterns().length > 0 ? module.urlPatterns() : new String[]{"/" + module.name() + "/*"});
                        }
                        contextMap.put(module.name(), applicationContext);
                    }
                    moduleMap.put(module.name(), module);
                }
            } catch (ClassNotFoundException ex) {
                LoggerFactory.getLogger(this.getClass()).error(ex.getMessage(), ex);
            }
        }
        moduleMap.values().stream().forEach(module -> {
            if (module.parent() != null && !module.parent().isEmpty()) {
                if (!module.name().equalsIgnoreCase(module.parent())) {
                    ConfigurableApplicationContext parentContext = contextMap.get(module.parent());
                    if (parentContext != null) {
                        ConfigurableApplicationContext appContext = contextMap.get(module.name());
                        if (appContext != null) appContext.setParent(parentContext);
                    } else
                        getLogger().warn("Parent Context '{}' not found for child '{}' - defaults to global context", module.parent(), module.name());
                } else
                    getLogger().warn("Parent Context '{}' same as child '{}' - defaults to global context", module.parent(), module.name());
            }
        });
        return moduleMap;
    }

    private Properties initConfigProperties(String appName) {
        String appHome = System.getProperty(appName + ".home");
        if ((appHome == null || appHome.length() == 0) && AppConstant.SYSTEM_HOME_DIR_KEY != null) {
            for (String homeDirKey : AppConstant.SYSTEM_HOME_DIR_KEY.split(",")) {
                appHome = System.getProperty(homeDirKey);
                if (appHome != null) {
                    appHome = appHome + File.separator + appName;
                    break;
                }
            }
        }
        File configFile = new File(new File(appHome, AppConstant.APP_CONFIG_DIR), appName + ".conf");
        Properties configProperties = null;
        try {
            configProperties = ConfigUtil.loadConfig(configFile);
        } catch (IOException ex) {
            LoggerFactory.getLogger(this.getClass()).error(ex.getMessage(), ex);
        }
        System.out.println(configFile.getAbsolutePath());
        configProperties = configProperties != null ? configProperties : new Properties();
        configProperties.put(AppConstant.APP_NAME_KEY, appName);
        configProperties.put(AppConstant.APP_HOME_KEY, appHome);
        configProperties.put(AppConstant.APP_CONFIG_FILE, configFile.getAbsolutePath());
        return configProperties;
    }

    private Map<String, PersistenceUnitBuilder> initPersistenceUnitConfiguration(Module module) {
        Map<String, PersistenceUnitBuilder> modulePersistenceUnitMap = persistenceUnitConfiguration().get(module);
        if (modulePersistenceUnitMap == null) {
            Module appModule = appModuleName != module.name() ? moduleConfiguration().get(appModuleName) : null;
            modulePersistenceUnitMap = createPersistenceUnitConfiguration(module, !module.equals(appModule) && appModule != null ? initPersistenceUnitConfiguration(appModule) : null);
            persistenceUnitConfiguration().put(module, modulePersistenceUnitMap);
        }
        return modulePersistenceUnitMap;
    }

    private Map<String, PersistenceUnitBuilder> createPersistenceUnitConfiguration(Module module, Map<String, PersistenceUnitBuilder> parentPersistenceUnitMap) {
        if (module == null)
            return null;
        Map<String, PersistenceUnitBuilder> persistenceUnitMap = new HashMap<>();
        Arrays.stream(module.persistence()).filter(persistenceUnit -> persistenceUnit != null).forEach(persistenceUnit -> {
            PersistenceUnitBuilder persistenceUnitBuilder = null;
            if (parentPersistenceUnitMap != null) {
                PersistenceUnitBuilder puBuilder = parentPersistenceUnitMap.get(persistenceUnit.unitName());
                if (puBuilder != null) {
                    if (!persistenceUnit.inherited())
                        throw new IllegalArgumentException("PersistenceUnit: " + persistenceUnit.unitName() + " in module: " + module.name() + " does not inherit from parent");
                    else {
                        persistenceUnitBuilder = puBuilder;
                        puBuilder.addChild(persistenceUnit);
                    }
                }
            }
            persistenceUnitMap.put(persistenceUnit.unitName(), persistenceUnitBuilder == null ? new PersistenceUnitBuilder(persistenceUnit) : persistenceUnitBuilder);
        });
        return persistenceUnitMap;
    }

    public void bindPersistenceToContext(ConfigurableApplicationContext applicationContext) {
        BeanDefinitionRegistry beanDefinitionRegistry = (applicationContext != null && applicationContext.getBeanFactory() instanceof BeanDefinitionRegistry)
                ? (BeanDefinitionRegistry) applicationContext.getBeanFactory() : null;
        if (beanDefinitionRegistry == null) {
            LoggerFactory.getLogger(this.getClass()).warn("BeanDefinitionRegistry cannot be found! Persistence unit setup skipped for context:" + applicationContext);
            return;
        }
        Module module = moduleConfiguration().get(applicationContext.getId());

        final Map<String, PersistenceUnitBuilder> persistenceUnitConfiguration = module != null ? initPersistenceUnitConfiguration(module) : null;
        if (persistenceUnitConfiguration != null) {
            persistenceUnitConfiguration.forEach((unitName, puBuilder) -> {
                if (puBuilder != null ? (puBuilder.hasChildren() && appModuleName.equals(module.name())) || !puBuilder.hasChildren() : false) {
                    PersistenceUnitBuilder.Builder builder = puBuilder.createBuilder();
                    beanDefinitionRegistry.registerBeanDefinition(builder.getPersistenceUnitName(), builder.buildEntityManagerFactoryBeanDefinition());
                    beanDefinitionRegistry.registerBeanDefinition(builder.getPersistenceUnitName() + "_transactionManager", builder.buildTransactionManagerBeanDefinition());
                    // beanDefinition.getPropertyValues().addPropertyValue(builder.getPersistenceUnitName(),new RuntimeBeanReference(beanName));
                }
            });
        }

    }


    /*public void bindPersistenceToContext(ApplicationManager applicationManager, ConfigurableApplicationContext applicationContext) {
        BeanDefinitionRegistry beanDefinitionRegistry = (applicationContext != null && applicationContext.getBeanFactory() instanceof BeanDefinitionRegistry)
                ? (BeanDefinitionRegistry) applicationContext.getBeanFactory() : null;
        if (beanDefinitionRegistry == null) {
            LoggerFactory.getLogger(this.getClass()).warn("BeanDefinitionRegistry canot be found! Persistence unit setup skipped for context:" + applicationContext);
            return;
        }
        Map<Module, Map<String, PersistenceUnitBuilder>> modulePersistenceUnitMap = new HashMap<>();
        //parse persitence unit
        Module appModule = applicationManager.moduleConfiguration().get(appModuleName);
        final Map<String, PersistenceUnitBuilder> corePersistenceUnitBuilder = createPersistenceUnitBuilder(appModule, null);
        if (appModule != null) {
            modulePersistenceUnitMap.put(appModule, corePersistenceUnitBuilder);
        }
        applicationManager.moduleConfiguration().entrySet().stream().filter(entry -> appModule != entry.getValue()).forEach(entry -> {
            Map<String, PersistenceUnitBuilder> persistenceUnitBuilder = createPersistenceUnitBuilder(entry.getValue(), corePersistenceUnitBuilder);
            modulePersistenceUnitMap.put(entry.getValue(), persistenceUnitBuilder);
        });
        //build PU EMF
        modulePersistenceUnitMap.entrySet().stream().filter(entry -> entry.getValue() != null).forEach(entry -> {
            entry.getValue().forEach((unitName, puBuilder) -> {
                if (puBuilder != null ? (puBuilder.hasChildren() && entry.getValue() == appModule) || !puBuilder.hasChildren() : false) {

                    *//*if(beanDefinitionRegistry.containsBeanDefinition())
                        beanDefinitionRegistry.removeBeanDefinition();*//*
                    PersistenceUnitBuilder.Builder builder = puBuilder.createBuilder();
                    beanDefinitionRegistry.registerBeanDefinition(builder.getPersistenceUnitName(), puBuilder.buildBeanDefinition());
                    //puBuilder.buildBeanDefinition();
                }
            });

        });
    }*/
}
