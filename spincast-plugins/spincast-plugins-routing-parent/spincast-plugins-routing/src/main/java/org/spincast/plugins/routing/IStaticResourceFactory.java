package org.spincast.plugins.routing;

import org.spincast.core.exchange.IRequestContext;
import org.spincast.core.routing.IHandler;
import org.spincast.core.routing.IStaticResource;
import org.spincast.core.routing.IStaticResourceCorsConfig;
import org.spincast.core.routing.StaticResourceType;

import com.google.inject.assistedinject.Assisted;

public interface IStaticResourceFactory<R extends IRequestContext<?>> {

    public IStaticResource<R> create(@Assisted StaticResourceType staticResourceType,
                                     @Assisted("url") String url,
                                     @Assisted("path") String path,
                                     @Assisted IHandler<R> generator,
                                     @Assisted IStaticResourceCorsConfig corsConfig);
}
