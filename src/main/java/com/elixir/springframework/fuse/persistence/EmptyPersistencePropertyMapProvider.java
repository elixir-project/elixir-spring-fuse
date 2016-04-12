package com.elixir.springframework.fuse.persistence;

import java.util.Collections;
import java.util.Map;

/**
 * Created by elixir on 3/7/16.
 */
public class EmptyPersistencePropertyMapProvider implements PersistencePropertyMapProvider {

    @Override
    public Map<String, ?> propertyMap(PersistenceUnit persistenceUnit) {
        return Collections.emptyMap();
    }
}
