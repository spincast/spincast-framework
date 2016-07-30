package org.spincast.plugins.undertow;

import org.spincast.core.routing.IStaticResource;

import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.resource.ResourceManager;

/**
 * Factory to create SpincastResourceHandler
 */
public interface ISpincastResourceHandlerFactory {

    public ISpincastResourceHandler create(ResourceManager resourceManager,
                                           IStaticResource<?> staticResource);

    public ISpincastResourceHandler create(ResourceManager resourceManager,
                                           IStaticResource<?> staticResource,
                                           HttpHandler nextHandler);

}
