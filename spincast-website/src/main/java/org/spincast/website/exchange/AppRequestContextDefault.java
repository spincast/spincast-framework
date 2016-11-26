package org.spincast.website.exchange;

import org.spincast.core.exchange.RequestContextBase;
import org.spincast.core.exchange.RequestContextBaseDeps;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * Custom request context class
 */
public class AppRequestContextDefault extends RequestContextBase<AppRequestContext> implements AppRequestContext {

    @AssistedInject
    public AppRequestContextDefault(@Assisted Object exchange, RequestContextBaseDeps<AppRequestContext> requestContextBaseDeps) {
        super(exchange, requestContextBaseDeps);
    }

    // Nothing more required for now.
}
