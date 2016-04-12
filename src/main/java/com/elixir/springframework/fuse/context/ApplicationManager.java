package com.elixir.springframework.fuse.context;

import java.util.Map;
import java.util.Properties;

/**
 * Created by elixir on 3/2/16.
 */
public interface ApplicationManager {

    public Map<String, Module> moduleConfiguration();
    public Properties configProperties();
    public void onStartup(Object context);
}
