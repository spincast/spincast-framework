package org.spincast.plugins.pebble.utils;

import java.util.function.Function;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.mitchellbosecke.pebble.cache.CacheKey;
import com.mitchellbosecke.pebble.cache.PebbleCache;

public class SpincastCaffeineTagCache implements PebbleCache<CacheKey, Object> {

    private final Cache<CacheKey, Object> tagCache;

    public SpincastCaffeineTagCache(long maximumSize) {
        if (maximumSize < 0) {
            maximumSize = 0;
        }
        this.tagCache = Caffeine.newBuilder()
                                .maximumSize(maximumSize)
                                .build();
    }

    @Override
    public Object computeIfAbsent(CacheKey key, Function<? super CacheKey, ? extends Object> mappingFunction) {
        return this.tagCache.get(key, mappingFunction);
    }

    @Override
    public void invalidateAll() {
        this.tagCache.invalidateAll();
    }

}
