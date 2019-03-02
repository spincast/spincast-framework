package org.spincast.core.routing;

import javax.annotation.Nullable;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.routing.hotlinking.HotlinkingManager;

import com.google.inject.assistedinject.Assisted;

public interface StaticResourceFactory<R extends RequestContext<?>> {

    public StaticResource<R> create(@Assisted("isSpicastOrPluginAddedResource") boolean isSpicastOrPluginAddedResource,
                                    @Assisted StaticResourceType staticResourceType,
                                    @Assisted("url") String url,
                                    @Assisted("path") String path,
                                    @Assisted Handler<R> generator,
                                    @Assisted StaticResourceCorsConfig corsConfig,
                                    @Assisted StaticResourceCacheConfig cacheConfig,
                                    @Assisted("ignoreQueryString") boolean ignoreQueryString,
                                    @Assisted("hotlinkingProtected") boolean hotlinkingProtected,
                                    @Assisted("hotlinkingManager") @Nullable HotlinkingManager hotlinkingManager);
}
