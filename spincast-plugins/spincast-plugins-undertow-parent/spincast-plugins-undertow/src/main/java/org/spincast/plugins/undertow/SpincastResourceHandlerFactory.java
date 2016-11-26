package org.spincast.plugins.undertow;

import org.spincast.core.routing.StaticResource;

import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.resource.ResourceManager;

/**
 * Factory to create SpincastResourceHandler
 */
public interface SpincastResourceHandlerFactory {

    public SpincastResourceHandler create(ResourceManager resourceManager,
                                          StaticResource<?> staticResource);

    public SpincastResourceHandler create(ResourceManager resourceManager,
                                          StaticResource<?> staticResource,
                                          HttpHandler nextHandler);

}
