package org.spincast.plugins.pebble.utils;

import java.util.function.Function;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.mitchellbosecke.pebble.cache.PebbleCache;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class SpincastCaffeineTemplateCache implements PebbleCache<Object, PebbleTemplate> {

    private final Cache<Object, PebbleTemplate> templateCache;

    public SpincastCaffeineTemplateCache(long maximumSize) {
        if (maximumSize < 0) {
            maximumSize = 0;
        }
        this.templateCache = Caffeine.newBuilder()
                                     .maximumSize(maximumSize)
                                     .build();
    }

    @Override
    public PebbleTemplate computeIfAbsent(Object key, Function<? super Object, ? extends PebbleTemplate> mappingFunction) {
        return this.templateCache.get(key, mappingFunction);
    }

    @Override
    public void invalidateAll() {
        this.templateCache.invalidateAll();
    }

}
