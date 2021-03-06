package org.spincast.plugins.routing;

import javax.annotation.Nullable;

import org.spincast.core.routing.StaticResourceCacheConfig;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class StaticResourceCacheConfigDefault implements StaticResourceCacheConfig {

    private final int cacheSeconds;
    private final boolean isCachePrivate;
    private final Integer cacheSecondsCdn;

    @AssistedInject
    public StaticResourceCacheConfigDefault(@Assisted("cacheSeconds") int cacheSeconds,
                                            @Assisted boolean isCachePrivate,
                                            @Assisted("cacheSecondsCdn") @Nullable Integer cacheSecondsCdn) {
        this.cacheSeconds = cacheSeconds;
        this.isCachePrivate = isCachePrivate;
        this.cacheSecondsCdn = cacheSecondsCdn;
    }

    @Override
    public int getCacheSeconds() {
        return this.cacheSeconds;
    }

    @Override
    public boolean isCachePrivate() {
        return this.isCachePrivate;
    }

    @Override
    public Integer getCacheSecondsCdn() {
        return this.cacheSecondsCdn;
    }
}
