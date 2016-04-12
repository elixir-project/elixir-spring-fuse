package com.elixir.springframework.fuse.persistence;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by elixir on 3/7/16.
 */
public class DefaultPersistencePropertyMapProvider implements PersistencePropertyMapProvider {
    @Override
    public Map<String, ?> propertyMap(PersistenceUnit persistenceUnit) {
        try {//@SuppressWarnings({"unchecked","rawtypes"})
            return new HashMap<String, String>((Map)new PersistenceConfiguration().additionalProperties());
            //return persistenceUnit != null ? new HashMap<String, String>((Map)ConfigUtil.loadConfig(persistenceUnit.unitName())) : Collections.emptyMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
