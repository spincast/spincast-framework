package org.spincast.core.routing;

import org.spincast.core.exchange.IRequestContext;

import com.google.inject.assistedinject.Assisted;

public interface IStaticResourceFactory<R extends IRequestContext<?>> {

    public IStaticResource<R> create(@Assisted StaticResourceType staticResourceType,
                                     @Assisted("url") String url,
                                     @Assisted("path") String path,
                                     @Assisted IHandler<R> generator,
                                     @Assisted IStaticResourceCorsConfig corsConfig,
                                     @Assisted IStaticResourceCacheConfig cacheConfig);
}
