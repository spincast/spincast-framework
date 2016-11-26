package org.spincast.plugins.undertow;

import org.spincast.core.routing.StaticResourceCorsConfig;

import io.undertow.server.HttpHandler;

public interface CorsHandlerFactory {

    public CorsHandler create(HttpHandler nextHandler,
                              StaticResourceCorsConfig corsConfig);
}
