package org.spincast.plugins.undertow;

import com.google.inject.assistedinject.Assisted;

import io.undertow.server.HttpHandler;

public interface SkipResourceOnQueryStringHandlerFactory {

    public SkipResourceOnQueryStringHandler create(@Assisted("runHandler") HttpHandler runHandler,
                                                   @Assisted("skipHandler") HttpHandler skipHandler);
}
