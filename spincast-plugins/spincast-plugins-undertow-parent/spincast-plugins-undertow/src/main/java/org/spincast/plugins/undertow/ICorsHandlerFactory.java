package org.spincast.plugins.undertow;

import org.spincast.core.routing.IStaticResourceCorsConfig;

import io.undertow.server.HttpHandler;

public interface ICorsHandlerFactory {

    public ICorsHandler create(HttpHandler nextHandler,
                               IStaticResourceCorsConfig corsConfig);
}
