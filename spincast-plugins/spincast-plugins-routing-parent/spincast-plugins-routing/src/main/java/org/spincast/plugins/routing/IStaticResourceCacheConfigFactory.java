package org.spincast.plugins.routing;

import org.spincast.core.routing.IStaticResourceCacheConfig;

import com.google.inject.assistedinject.Assisted;

/**
 * Factory to create cache config for static resources.
 */
public interface IStaticResourceCacheConfigFactory {

    public IStaticResourceCacheConfig create(@Assisted("cacheSeconds") int cacheSeconds,
                                             boolean isCachePrivate,
                                             @Assisted("cacheSecondsCdn") Integer cacheSecondsCdn);
}
