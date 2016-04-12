package com.elixir.springframework.fuse.persistence;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Properties;

/**
 * Created by oogunjimi on 2/17/2016.
 */

@Configuration
@EnableTransactionManagement
public class PersistenceConfiguration  {
   // public static final String PERSISTENCE_UNIT_NAME = "hawk-persistence";
    //public static final String DATASOURCE_PREFIX = "datasource";




    //@Bean //@Scope(BeanDefinition.SCOPE_PROTOTYPE)
    /*public DataSource dataSource2(ServletContext servletContext) {
        System.out.println("injected2===" + servletContext);
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/hawk?zeroDateTimeBehavior=convertToNull&amp;useSSL=true");
        dataSource.setUsername("root");
        dataSource.setPassword("elixir");
        return dataSource;
    }*/

    //@Bean @Lazy(true)@Scope(BeanDefinition.SCOPE_PROTOTYPE)
    /*public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        //ConfigurationPropertiesBindingPostProcessor
        return transactionManager;
    }*/

    //@Bean
   /* public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }*/

    //@Persistence("")
    // @PersistenceUnit
//@PersistenceProperty()
    Properties additionalProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
        properties.setProperty("javax.persistence.jdbc.url"
                , "jdbc:mysql://localhost:3306/hawk?zeroDateTimeBehavior=convertToNull&amp;useSSL=true");
        properties.setProperty("javax.persistence.jdbc.user", "root");

        properties.setProperty("javax.persistence.jdbc.password", "elixir");
        properties.setProperty("hibernate.connection.provider_class"
                , "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");
        properties.setProperty("hibernate.hikari.dataSourceClassName"
                , "com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        properties.setProperty("hibernate.hikari.dataSource.user", "root");
        properties.setProperty("hibernate.hikari.dataSource.password", "elixir");
        properties.setProperty("hibernate.hikari.dataSource.databaseName", "hawk");
        properties.setProperty("hibernate.hikari.dataSource.cachePrepStmts", "true");
        properties.setProperty("hibernate.hikari.dataSource.prepStmtCacheSize", "250");
        properties.setProperty("hibernate.hikari.dataSource.prepStmtCacheSqlLimit", "2048");
        return properties;
    }

    /*private Map<String, PersistenceUnitBuilder> createPersistenceUnitBuilder(Module module,Map<String, PersistenceUnitBuilder> parentPersistenceUnitMap){
        if(module==null)
            return null;
        Map<String, PersistenceUnitBuilder> persistenceUnitMap=new HashMap<>();
        Arrays.stream(module.persistence()).filter(persistenceUnit -> persistenceUnit != null).forEach(persistenceUnit -> {
            PersistenceUnitBuilder persistenceUnitBuilder=null;
            if(parentPersistenceUnitMap!=null){
                PersistenceUnitBuilder puBuilder = parentPersistenceUnitMap.get(persistenceUnit.unitName());
                if(puBuilder!=null){
                    if(!persistenceUnit.inherited())
                        throw new IllegalArgumentException("PersistenceUnit: "+persistenceUnit.unitName()+" in module: "+module.name()+" does not inherit from parent");
                    else{
                        persistenceUnitBuilder=puBuilder;
                        puBuilder.addChild(persistenceUnit);
                    }
                }
            }
            persistenceUnitMap.put(persistenceUnit.unitName(),persistenceUnitBuilder==null?new PersistenceUnitBuilder(persistenceUnit):persistenceUnitBuilder);
        });
        return persistenceUnitMap;
    }*/
    /*public LocalContainerEntityManagerFactoryBean customerntityManagerFactory(ApplicationManager applicationManager, ConfigurableApplicationContext applicationContext) {
        Map<Module,Map<String, PersistenceUnitBuilder>> modulePersistenceUnitMap=new HashMap<>();
        //parse persitence unit
        Module coreModule = applicationManager.moduleConfiguration().get(Resource.MODULE_NAME);
        final Map<String, PersistenceUnitBuilder> corePersistenceUnitBuilder=createPersistenceUnitBuilder(coreModule, null);;
        if(coreModule!=null) {
            modulePersistenceUnitMap.put(coreModule,corePersistenceUnitBuilder);
        }
        applicationManager.moduleConfiguration().entrySet().stream().filter(entry->coreModule!=entry.getValue()).forEach(entry->{
            Map<String, PersistenceUnitBuilder> persistenceUnitBuilder = createPersistenceUnitBuilder(entry.getValue(), corePersistenceUnitBuilder);
            modulePersistenceUnitMap.put(entry.getValue(),persistenceUnitBuilder);
        });
    //build PU EMF
        modulePersistenceUnitMap.entrySet().stream().filter(entry->entry.getValue()!=null).forEach(entry->{
            entry.getValue().forEach((unitName,puBuilder)->{
                if(puBuilder!=null?(puBuilder.hasChildren() && entry.getValue()==coreModule) ||  !puBuilder.hasChildren():false){
                    //DefaultListableBeanFactory g; g.registerBeanDefinition();
                    ConfigurableListableBeanFactory l;
                   // applicationContext.getBeanFactory().re;
                    BeanDefinitionBuilder v;
                    //puBuilder.getObject();
                }
            });

        });

        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        //beanDefinition.setBeanClass(MyBeanClass.class);
        beanDefinition.setLazyInit(false);
        beanDefinition.setAbstract(false);
        beanDefinition.setAutowireCandidate(true);
        beanDefinition.setScope("session");
        //LocalContainerEntityManagerFactoryBean j;j.destroy();
        return null;
    }*/

    public void createEntityManagerFactoryBean(PersistenceUnit pu){
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(LocalContainerEntityManagerFactoryBean.class);
        beanDefinitionBuilder
        .setLazyInit(false)
        .setAbstract(false)
        .setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE)
        .setScope(BeanDefinition.SCOPE_SINGLETON)
                .addPropertyValue("",pu.unitName());
    }
        //@Bean
    /*public LocalContainerEntityManagerFactoryBean customerEntityManagerFactory(ApplicationManager applicationManager, PersistenceUnit persistenceUnit) {
        Set<String> packages = new HashSet<String>();
        applicationManager.modules().stream().forEach(moduleClass -> {
            Module module = moduleClass.getAnnotation(Module.class);
            Arrays.stream(module.persistence()).filter(pu -> pu != null).forEach(pu -> {
                if (persistenceUnit.unitName().equals(pu.unitName())) {
                    EntityScan entityScan = pu.entityScan();
                    if (entityScan != null) {
                        if (entityScan.basePackageClasses() != null) {
                            for (Class<?> clazz : entityScan.basePackageClasses()) {
                                packages.add(ClassUtils.getPackageName(clazz));
                            }
                        }
                        if (entityScan.basePackages() != null) {
                            for (String packageName : entityScan.basePackages()) {
                                packages.add(packageName);
                            }
                        }
                        if (entityScan.value() != null) {
                            for (String packageName : entityScan.value()) {
                                packages.add(packageName);
                            }
                        }
                        if (packages.isEmpty()) {
                            packages.add(ClassUtils.getPackageName(moduleClass));
                        }
                    }
                }
            });
        });
        ClassUtils.hasConstructor();EntityManagerFactoryBuilder h;
//316610624   6512    /  dalani Banky!678   630 771 288
        final Properties props = additionalProperties();
        Map<String, ?> propertyMap=null;
        PersistenceProperty[] properties = persistenceUnit.properties();
        Arrays.stream(properties).filter(prop -> prop != null && prop.name() != null).forEach(prop -> {
            props.put(prop.name(), prop.value());
        });
        Class<? extends PersistencePropertyMapProvider> propertyMapProvider = persistenceUnit.propertyMapProvider();
        try {
             propertyMap = propertyMapProvider.newInstance().propertyMap(persistenceUnit);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setPersistenceUnitName(persistenceUnit.unitName());
        em.setPersistenceProvider(new HibernatePersistenceProvider());
        em.setPackagesToScan(packages.toArray(new String[packages.size()]));
        em.setJpaProperties(props);
        if(propertyMap!=null)
            em.setJpaPropertyMap(propertyMap);
        return em;
    }*/

    /*private DataSource dataSource(Properties props) {
        String provider = props.getProperty(DATASOURCE_PREFIX + ".provider");
        //provider = provider == null ? props.getProperty(DATASOURCE_PREFIX + ".provider") : provider;
        if (provider != null) {
            Class<? extends DataSource> targetClass = null;
            try {
                targetClass = (Class<? extends DataSource>) Class.forName(provider);
                if (targetClass.isInstance(DataSource.class))
                    processInitialization(targetClass, props, null);
            } catch (ClassNotFoundException | ClassCastException e) {
                e.printStackTrace();
            }
        }
        return null;
    }*/

   /* public Object processInitialization(Object bean, String beanName)
            throws BeansException {
        // ConfigurationProperties annotation;
        Map attrMap = new HashMap<String, Object>();
        attrMap.put("value", "datasource");
        //ConfigurationProperties annotation = AnnotationUtils.synthesizeAnnotation(ConfigurationProperties.class);

        ConfigurationPropertiesLiterial annotation = new ConfigurationPropertiesLiterial();
        annotation.setPrefix();//annotation
        if (annotation != null) {
            postProcessBeforeInitialization(bean, beanName, annotation);
        }
        annotation = this.beans.findFactoryAnnotation(beanName,
                ConfigurationProperties.class);
        if (annotation != null) {
            postProcessBeforeInitialization(bean, beanName, annotation);
        }
        return bean;
    }*/


   /* private <T> T processInitialization(Class<T> targetClazz, Properties properties,
                                        ConfigurationProperties annotation) {
        //Object bean
        PropertiesConfigurationFactory<T> factory = new PropertiesConfigurationFactory<T>(targetClazz);
        factory.setProperties(properties);
        // If no explicit conversion service is provided we add one so that (at least)
        // comma-separated arrays of convertibles can be bound automatically
        if (annotation != null) {
            factory.setIgnoreInvalidFields(annotation.ignoreInvalidFields());
            factory.setIgnoreUnknownFields(annotation.ignoreUnknownFields());
            factory.setExceptionIfInvalid(annotation.exceptionIfInvalid());
            factory.setIgnoreNestedProperties(annotation.ignoreNestedProperties());
            String targetName = (StringUtils.hasLength(annotation.value())
                    ? annotation.value() : annotation.prefix());
            if (StringUtils.hasLength(targetName)) {
                factory.setTargetName(targetName);
            }
        }
        try {
            //factory.bindPropertiesToTarget();
            return factory.getObject();
        } catch (Exception ex) {
            String targetClass = ClassUtils.getShortName(targetClazz);
            throw new BeanCreationException(targetClazz.getName(), "Could not bind properties to "
                    + targetClass + " (" + annotation.toString() + ")", ex);
        }
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {


    }*/
}
