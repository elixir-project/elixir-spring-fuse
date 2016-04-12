package com.elixir.springframework.fuse.config;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ContextLoader;

/**
 * Created by elixir on 2/25/16.
 */
//@PropertySource("file://${" + AppConstant.APP_HOME_KEY + "}")
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@ConfigurationProperties("application")
public class AppConfig {

    private String appName;

    public String appHome;

    public String logMaxFileSize;

    public Integer logMaxHistory;

    public boolean logAdditive;

    public boolean debug;


    public String appName() {
        return appName;
    }

    public String appHome() {
        return appHome;
    }

    public boolean debug() {
        return debug;
    }

    public boolean logAdditive() {
        return logAdditive;
    }

    public Integer logMaxHistory() {
        return logMaxHistory;
    }

    public String logMaxFileSize() {
        return logMaxFileSize;
    }

    public static AppConfig instance() {
        // BeanFactory.FACTORY_BEAN_PREFIX
       // ApplicationContextUtils.ge
        return ContextLoader.getCurrentWebApplicationContext().getBean(AppConfig.class);
        //return BeanProvider.getContextualReference(AppConfig.class);
    }
}
