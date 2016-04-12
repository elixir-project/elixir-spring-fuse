package com.elixir.springframework.fuse.context;


import com.elixir.springframework.fuse.persistence.PersistenceUnit;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.autoconfigure.web.DispatcherServletAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AliasFor;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.lang.annotation.*;

/**
 * Created by elixir on 2/23/16.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@EnableWebMvc
@SpringBootApplication(exclude = {
        DispatcherServletAutoConfiguration.class,
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        TransactionAutoConfiguration.class})
public @interface Module {
    @AliasFor(
            annotation = Configuration.class,
            attribute = "value"
    ) String name();

    String parent() default "";

    String[] urlPatterns() default {};

    int loadOnStartup() default 1;

    boolean autoRegister() default true;

    PersistenceUnit[] persistence() default {};

    //authenticationProvider
}
