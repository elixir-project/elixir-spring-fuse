package com.elixir.springframework.fuse;

import com.elixir.springframework.fuse.context.ApplicationManager;
import com.elixir.springframework.fuse.context.DefaultApplicationManager;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;


import javax.servlet.ServletContext;
import javax.servlet.ServletException;


/**
 * Created by oogunjimi on 2/17/2016.
 */
//@Module(name = "core", autoRegister = false,persistence = {@PersistenceUnit(unitName = PersistenceConfiguration.PERSISTENCE_UNIT_NAME)})
//@SpringBootApplication(exclude = {DispatcherServletAutoConfiguration.class, HibernateJpaAutoConfiguration.class,
 //       DataSourceAutoConfiguration.class,DataSourceTransactionManagerAutoConfiguration.class,XADataSourceAutoConfiguration.class, TransactionAutoConfiguration.class})
public abstract class AppModuleConfiguration extends SpringBootServletInitializer {
    private ApplicationManager applicationManager = null;

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        System.out.println("cool as ice");
        //Map<String,Module> moduleConfiguration = autoModuleConfiguration(servletContext);
        getApplicationManager().onStartup(servletContext);
        servletContext.setAttribute(ApplicationManager.class.getName(), getApplicationManager());
        super.onStartup(servletContext);
        System.out.println("==============cool");
    }

    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        builder.properties(getApplicationManager().configProperties());
        if (getApplicationManager() instanceof ApplicationContextInitializer)
            builder.initializers((ApplicationContextInitializer) getApplicationManager());
        if (getApplicationManager() instanceof ApplicationListener)
            builder.listeners((ApplicationListener) getApplicationManager());
        return builder;
    }

    protected ApplicationManager getApplicationManager() {
        if (applicationManager == null)
            applicationManager = new DefaultApplicationManager(this.getClass());
        return applicationManager;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer p = new PropertySourcesPlaceholderConfigurer();
        p.setIgnoreUnresolvablePlaceholders(true);
        p.setNullValue(null);
        //p.set
        return p;
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    public ApplicationManager applicationManager(ServletContext servletContext) {
        return (ApplicationManager) servletContext.getAttribute(ApplicationManager.class.getName());
    }

}
