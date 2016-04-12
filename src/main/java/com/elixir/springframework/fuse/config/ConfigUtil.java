package com.elixir.springframework.fuse.config;

import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by elixir on 2/25/16.
 */
public class ConfigUtil {
    public static Properties loadConfig(File configFile) throws IOException {
        synchronized (configFile) {
            if (configFile.exists()) {
                Properties props = new Properties();
                props.load(new FileInputStream(configFile));
                LoggerFactory.getLogger(ConfigUtil.class).warn("Config file loaded: " + configFile.getAbsolutePath());
                return props;
            }
        }
        LoggerFactory.getLogger(ConfigUtil.class).warn("config file not found: " + configFile.getAbsolutePath());
        return null;
    }

    /*public static Properties loadConfig(String filename) throws IOException {
        System.out.println("filename="+filename);
        AppConfig config = AppConfig.instance();
        if (config != null) {
            File configFile = new File(new File(config.getAppHome(), AppConstant.APP_CONFIG_DIR), filename);
            return loadConfig(configFile);
        }
        return null;
    }*/
}
