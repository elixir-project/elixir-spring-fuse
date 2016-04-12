package com.elixir.springframework.fuse.config;

/**
 * Created by elixir on 2/25/16.
 */

//@Configuration
public class ConfigProducer {

    /*private Properties configProperties;

    //@Bean
    @Scope(WebApplicationContext.SCOPE_APPLICATION)
    public AppConfig appConfig() {
        AppConfig config = ConfigCache.get(AppConfig.class.getName());
        if (config == null) {
            config = initConfig(AppConstant.APP_DEFAULT_NAME);
        }
        return config;
    }

    public AppConfig initConfig(String appName) {
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
        try {
            configProperties = ConfigUtil.loadConfig(configFile);
        } catch (IOException ex) {
            LoggerFactory.getLogger(ConfigProducer.class).error(ex.getMessage(), ex);
        }
        configProperties = configProperties != null ? configProperties : new Properties();
        configProperties.put(AppConstant.APP_NAME_KEY, appName);
        configProperties.put(AppConstant.APP_HOME_KEY, appHome);

        System.out.println("configProperties=" + configProperties);
        ConfigFactory.setProperty(AppConstant.APP_CONFIG_FILE, configFile.getAbsolutePath());
        ConfigCache.remove(AppConfig.class.getName());
        return ConfigCache.getOrCreate(AppConfig.class.getName(), AppConfig.class, configProperties);
    }

    public <T extends Config> T buildConfig(String envKey, Class<T> clazz) {
        if (configProperties == null) {
            appConfig();
        }
        Properties props = new Properties();
        if (configProperties != null) {
            props.putAll(configProperties);
        }
        if (envKey != null) {
            props.put(AppConstant.APP_ENV_KEY, envKey);
            return ConfigFactory.create(clazz, props);
        }
        return ConfigFactory.create(clazz, props);
    }*/
}
