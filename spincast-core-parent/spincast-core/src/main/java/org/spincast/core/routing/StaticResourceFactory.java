package org.spincast.core.routing;

import org.spincast.core.exchange.RequestContext;

import com.google.inject.assistedinject.Assisted;

public interface StaticResourceFactory<R extends RequestContext<?>> {

    public StaticResource<R> create(@Assisted StaticResourceType staticResourceType,
                                     @Assisted("url") String url,
                                     @Assisted("path") String path,
                                     @Assisted Handler<R> generator,
                                     @Assisted StaticResourceCorsConfig corsConfig,
                                     @Assisted StaticResourceCacheConfig cacheConfig);
}
