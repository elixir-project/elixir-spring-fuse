package com.elixir.springframework.fuse.persistence;


import com.elixir.springframework.fuse.config.ConfigurationPropertiesLiteral;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.boot.autoconfigure.security.AuthenticationManagerConfiguration;
import org.springframework.boot.bind.PropertiesConfigurationFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceProperty;
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Created by elixir on 3/8/16.
 */
public class PersistenceUnitBuilder {
    private final LinkedList<PersistenceUnit> children = new LinkedList<>();
    private PersistenceUnit parent;
    private Logger logger;

    public PersistenceUnitBuilder() {
    }

    public PersistenceUnitBuilder(PersistenceUnit persistenceUnit) {
        this.parent = persistenceUnit;
    }

    public PersistenceUnit getParent() {
        return parent;
    }

    public void addChild(PersistenceUnit persistenceUnit) {
        children.push(persistenceUnit);
    }

    public boolean containsChild(PersistenceUnit persistenceUnit) {
        return children.stream().anyMatch(pu -> pu.equals(persistenceUnit));
    }

    public boolean hasChildren() {
        return children.size() > 0;
    }
    /*private LocalContainerEntityManagerFactoryBean entityManagerFactoryBean;

    public LocalContainerEntityManagerFactoryBean getObject() {
        if (entityManagerFactoryBean == null)
            build();
        return entityManagerFactoryBean;
    }

    public LocalContainerEntityManagerFactoryBean build() {
        Builder builder = new Builder();
        builder.sanitize(parent);
        children.stream().forEachOrdered(persistenceUnit -> builder.sanitize(persistenceUnit));
        entityManagerFactoryBean = builder.build();
        return entityManagerFactoryBean;
    }*/

    /*public AbstractEntityManagerFactoryBean buildEntityManagerFactoryBean() {
        return createBuilder().buildEntityManagerFactoryBean();
    }

    public BeanDefinition buildBeanDefinition() {
        return createBuilder().buildBeanDefinition();
    }
*/
    public Builder createBuilder() {
        Builder builder = new Builder();
        builder.resolve(parent);
        children.stream().forEachOrdered(persistenceUnit -> builder.resolve(persistenceUnit));
        return builder;
    }

    private Logger getLogger() {
        if (logger == null)
            logger = LoggerFactory.getLogger(this.getClass());
        return logger;
    }

    public final class Builder {

        Class<? extends PlatformTransactionManager> transactionManagerClass;
        private String persistenceUnitName;
        private SharedCacheMode sharedCacheMode;
        private ValidationMode validationMode;
        private Map<String, ?> propertyMap;
        private Properties properties;
        private Set<String> packagesToScan;
        private PersistenceUnitTransactionType persistenceUnitTransactionType;
        private DataSource dataSource;
        private PersistenceUnitManager persistenceUnitManager;
        private JpaVendorAdapter jpaVendorAdapter;
        private Class<? extends PersistenceProvider> persistenceProvider;
        private AbstractEntityManagerFactoryBean _entityManagerFactoryBean;

        public String getPersistenceUnitName() {
            return persistenceUnitName;
        }

        public SharedCacheMode getSharedCacheMode() {
            return sharedCacheMode;
        }

        public ValidationMode getValidationMode() {
            return validationMode;
        }

        public Map<String, ?> getPropertyMap() {
            return propertyMap;
        }

        public Properties getProperties() {
            return properties;
        }

        public Set<String> getPackagesToScan() {
            return packagesToScan;
        }

        public PersistenceUnitTransactionType getPersistenceUnitTransactionType() {
            return persistenceUnitTransactionType;
        }

        public DataSource getDataSource() {
            return dataSource;
        }

        public PersistenceUnitManager getPersistenceUnitManager() {
            return persistenceUnitManager;
        }

        public JpaVendorAdapter getJpaVendorAdapter() {
            return jpaVendorAdapter;
        }

        public Class<? extends PersistenceProvider> getPersistenceProvider() {
            return persistenceProvider;
        }

        public Class<? extends PlatformTransactionManager> getTransactionManagerClass() {
            return transactionManagerClass;
        }

        private void resolve(PersistenceUnit persistenceUnit) {
            if (persistenceUnit == null)
                return;
            if (this.persistenceUnitName == null)
                this.persistenceUnitName = persistenceUnit.unitName();
            if (this.sharedCacheMode == null)
                this.sharedCacheMode = persistenceUnit.sharedCacheMode();
            if (this.validationMode == null)
                this.validationMode = persistenceUnit.validationMode();

            EntityScan entityScan = persistenceUnit.entityScan();
            if (entityScan != null) {
                this.packagesToScan = new HashSet<>();
                if (entityScan.basePackageClasses() != null) {
                    Arrays.stream(entityScan.basePackageClasses()).filter(clazz -> clazz != null)
                            .forEach(clazz -> this.packagesToScan.add(ClassUtils.getPackageName(clazz)));
                }
                if (entityScan.basePackages() != null) {
                    Arrays.stream(entityScan.basePackages()).filter(packageName -> packageName != null)
                            .forEach(packageName -> this.packagesToScan.add(packageName));
                }
                if (entityScan.value() != null) {
                    Arrays.stream(entityScan.value()).filter(packageName -> packageName != null)
                            .forEach(packageName -> this.packagesToScan.add(packageName));
                }

            }
            LinkedList providers = new LinkedList();
            providers.push(persistenceUnit.persistenceProvider());
            //load properties
            PersistenceProperty[] properties = persistenceUnit.properties();
            Arrays.stream(properties).filter(props -> props != null && props.name() != null)
                    .forEach(props -> (this.properties = this.properties == null ? new Properties() : this.properties)
                            .put(props.name(), props.value())
                    );
            providers.push(this.properties != null ? this.properties.getProperty(PersistenceUnit.PERSISTENCE_PROVIDER_KEY) : null);
            //load propertyMap
            Class<? extends PersistencePropertyMapProvider> propertyMapProvider = persistenceUnit.propertyMapProvider();
            try {
                if (isConcreteClass(propertyMapProvider)) {
                    this.propertyMap = propertyMapProvider.newInstance().propertyMap(persistenceUnit);
                    providers.push(this.propertyMap != null ? this.propertyMap.get(PersistenceUnit.PERSISTENCE_PROVIDER_KEY) : null);
                } else if (!PersistencePropertyMapProvider.class.equals(propertyMapProvider))
                    getLogger().warn("{} property map provider is not assignable from PersistencePropertyMapProvider or is not a concrete class", propertyMapProvider);
            } catch (InstantiationException | IllegalAccessException e) {
                getLogger().error(e.getMessage(), e);
            }
            //resolve provider
            providers.stream().filter(provider -> {
                        if (provider != null) {
                            try {
                                Class<?> providerClass = provider instanceof Class<?> ? (Class<?>) provider :
                                        provider instanceof String ? Class.forName((String) provider) : null;
                                if (isConcreteClass(providerClass) && PersistenceProvider.class.isAssignableFrom(providerClass)) {
                                    //todo:ClassUtils.hasConstructor(providerClass)
                                    this.persistenceProvider = (Class<? extends PersistenceProvider>) providerClass;
                                    return true;
                                } else
                                    getLogger().warn("{} = {} property is not assignable from PersistenceProvider or is not a concrete class", PersistenceUnit.PERSISTENCE_PROVIDER_KEY, providerClass);
                            } catch (ClassNotFoundException e) {
                                getLogger().error(e.getMessage(), e);
                            }
                        }
                        return false;
                    }
            ).findFirst();
            //resolve transaction Manager
            transactionManagerClass = persistenceUnit.transactionManager();
            if (!isConcreteClass(transactionManagerClass) || !ClassUtils.hasConstructor(transactionManagerClass)) {
                transactionManagerClass = this.persistenceUnitTransactionType == PersistenceUnitTransactionType.JTA
                        ? JtaTransactionManager.class : JpaTransactionManager.class;
            }
            //resolve datasource
            providers.clear();
            providers.push(persistenceUnit.dataSource());
            if (this.properties != null)
                providers.push(this.properties.getProperty(PersistenceUnit.DATASOURCE_KEY));
            if (this.propertyMap != null)
                providers.push(this.propertyMap.get(PersistenceUnit.DATASOURCE_KEY));
            Class<? extends DataSource> dataSourceClass = null;
            providers.stream().filter(provider -> {
                        if (provider != null) {
                            try {
                                Class<?> providerClass = provider instanceof Class<?> ? (Class<?>) provider :
                                        provider instanceof String ? Class.forName((String) provider) : null;
                                if (isConcreteClass(providerClass) && DataSource.class.isAssignableFrom(providerClass)) {
                                    //todo:ClassUtils.hasConstructor(providerClass)
                                    dataSource = instantiateDataSource((Class<? extends DataSource>) providerClass);
                                    return dataSource != null;
                                } else if(providerClass!=DataSource.class)
                                    getLogger().warn("{} = {} property is not assignable from DataSource or is not a concrete class", PersistenceUnit.DATASOURCE_KEY, providerClass);
                            } catch (ClassNotFoundException e) {
                                getLogger().error(e.getMessage(), e);
                            }
                        }
                        return false;
                    }
            ).findFirst();
        }

        private DataSource instantiateDataSource(Class<? extends DataSource> targetClass) {
            Properties props = new Properties();
            if (this.properties != null) props.putAll(this.properties);
            if (this.propertyMap != null) props.putAll(this.propertyMap);

            ConfigurationPropertiesLiteral annotation = new ConfigurationPropertiesLiteral();
            annotation.setPrefix(PersistenceUnit.DATASOURCE_KEY);
            try {
                return bindPropertiesToClass(targetClass, props, annotation);
            } catch (Exception e) {
                getLogger().error("cannot instantiate DataSource class : " + targetClass, e);
            }
            return null;
        }

        private <T> T bindPropertiesToClass(Class<T> targetClazz, Properties properties, ConfigurationProperties annotation) {
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

        public AbstractEntityManagerFactoryBean buildEntityManagerFactoryBean() {
            LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
            if (this.persistenceUnitManager != null) {
                entityManagerFactoryBean.setPersistenceUnitManager(this.persistenceUnitManager);
            }
            if (this.persistenceProvider != null) {
                entityManagerFactoryBean.setPersistenceProviderClass(this.persistenceProvider);
            }
            if (this.persistenceUnitName != null) {
                entityManagerFactoryBean.setPersistenceUnitName(this.persistenceUnitName);
            }
            if (this.jpaVendorAdapter != null) {
                entityManagerFactoryBean.setJpaVendorAdapter(this.jpaVendorAdapter);
            }
            if (this.dataSource != null) {
                if (this.persistenceUnitTransactionType == PersistenceUnitTransactionType.JTA) {
                    entityManagerFactoryBean.setJtaDataSource(this.dataSource);
                } else {
                    entityManagerFactoryBean.setDataSource(this.dataSource);
                }
            }
            if (this.packagesToScan != null) {
                entityManagerFactoryBean.setPackagesToScan(this.packagesToScan.toArray(new String[this.packagesToScan.size()]));
            }
            if (this.properties != null) {
                entityManagerFactoryBean.setJpaProperties(this.properties);
            }
            if (this.propertyMap != null) {
                entityManagerFactoryBean.setJpaPropertyMap(this.propertyMap);
            }
            this._entityManagerFactoryBean = entityManagerFactoryBean;
            return _entityManagerFactoryBean;
        }

        public PlatformTransactionManager buildTransactionManager() {
            if (transactionManagerClass == null || this._entityManagerFactoryBean == null)
                return null;
            PlatformTransactionManager transactionManager = BeanUtils.instantiate(transactionManagerClass);
            if (transactionManager instanceof JpaTransactionManager) {
                EntityManagerFactory nativeEntityManagerFactory = _entityManagerFactoryBean.getNativeEntityManagerFactory();
                if (nativeEntityManagerFactory == null) {
                    _entityManagerFactoryBean.afterPropertiesSet();
                    nativeEntityManagerFactory = _entityManagerFactoryBean.getNativeEntityManagerFactory();
                }
                ((JpaTransactionManager) transactionManager).setEntityManagerFactory(nativeEntityManagerFactory);
            } else if (transactionManager instanceof JtaTransactionManager) {
                AuthenticationManagerConfiguration h;
            }
            return transactionManager;
        }

        public BeanDefinition buildEntityManagerFactoryBeanDefinition() {
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
                    .genericBeanDefinition(LocalContainerEntityManagerFactoryBean.class)
                    .setLazyInit(false)
                    .setAbstract(false)
                    .setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE)
                    .setScope(BeanDefinition.SCOPE_SINGLETON);

            if (this.persistenceUnitManager != null) {
                beanDefinitionBuilder.addPropertyValue("persistenceUnitManager", this.persistenceUnitManager);
            }
            if (this.persistenceProvider != null) {System.out.println("kill  "+this.persistenceProvider);
                beanDefinitionBuilder.addPropertyValue("persistenceProviderClass", this.persistenceProvider);
            }
            if (this.persistenceUnitName != null) {
                beanDefinitionBuilder.addPropertyValue("persistenceUnitName", this.persistenceUnitName);
            }
            if (this.jpaVendorAdapter != null) {
                beanDefinitionBuilder.addPropertyValue("jpaVendorAdapter", this.jpaVendorAdapter);
            }
            if (this.dataSource != null) {
                if (this.persistenceUnitTransactionType == PersistenceUnitTransactionType.JTA) {
                    beanDefinitionBuilder.addPropertyValue("jtaDataSource", this.dataSource);
                } else {
                    beanDefinitionBuilder.addPropertyValue("dataSource", this.dataSource);
                }
            }
            if (this.packagesToScan != null) {
                System.out.println("this.packagesToScan=" + this.packagesToScan.size());
                beanDefinitionBuilder.addPropertyValue("packagesToScan", this.packagesToScan.toArray(new String[this.packagesToScan.size()]));
            }
            if (this.properties != null) {
                beanDefinitionBuilder.addPropertyValue("jpaProperties", this.properties);
            }
            if (this.propertyMap != null) {
                beanDefinitionBuilder.addPropertyValue("jpaPropertyMap", this.propertyMap);
            }
            return beanDefinitionBuilder.getBeanDefinition();
        }

        public BeanDefinition buildTransactionManagerBeanDefinition() {
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
                    .genericBeanDefinition(transactionManagerClass)
                    .setLazyInit(false)
                    .setAbstract(false)
                    .setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE)
                    .setScope(BeanDefinition.SCOPE_SINGLETON);
            if (JpaTransactionManager.class.isAssignableFrom(transactionManagerClass)) {
                beanDefinitionBuilder.addPropertyReference("entityManagerFactory", this.persistenceUnitName);
            } else if (JtaTransactionManager.class.isAssignableFrom(transactionManagerClass)) {

            }
            return beanDefinitionBuilder.getBeanDefinition();
        }

        private boolean isConcreteClass(Class clazz) {
            return clazz != null && !clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers());
        }
    }
}
