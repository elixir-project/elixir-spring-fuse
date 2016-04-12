package com.elixir.springframework.fuse.persistence;

import java.util.Map;

/**
 * Created by elixir on 3/7/16.
 */
public interface PersistencePropertyMapProvider {
    public Map<String, ?> propertyMap(PersistenceUnit persistenceUnit);
}
