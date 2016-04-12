package com.elixir.springframework.fuse.persistence;


import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.PersistenceProperty;
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;
import java.lang.annotation.*;


/**
 * Created by elixir on 3/7/16.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface PersistenceUnit {
    public static final String DEFAULT_PERSISTENCE_PROVIDER = "org.hibernate.jpa.HibernatePersistenceProvider";
    public static final String PERSISTENCE_PROVIDER_KEY = "persistence.provider";
    public static final String DATASOURCE_KEY = "persistence.datasource";

    String unitName();

    PersistenceUnitTransactionType transactionType() default PersistenceUnitTransactionType.RESOURCE_LOCAL;

    Class<? extends PersistenceProvider> persistenceProvider() default PersistenceProvider.class;

    Class<? extends DataSource> dataSource() default DataSource.class;

    SharedCacheMode sharedCacheMode() default SharedCacheMode.ENABLE_SELECTIVE;

    ValidationMode validationMode() default ValidationMode.AUTO;

    PersistenceProperty[] properties() default {@PersistenceProperty(name = PERSISTENCE_PROVIDER_KEY, value = DEFAULT_PERSISTENCE_PROVIDER)};

    Class<? extends PersistencePropertyMapProvider> propertyMapProvider() default DefaultPersistencePropertyMapProvider.class;

    Class<? extends PlatformTransactionManager> transactionManager() default PlatformTransactionManager.class;

    boolean transactional() default true;

    boolean inherited() default false;

    EntityScan entityScan() default @EntityScan();

}
