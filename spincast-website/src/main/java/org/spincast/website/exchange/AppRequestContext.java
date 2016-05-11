package org.spincast.website.exchange;

import org.spincast.core.exchange.RequestContextBase;
import org.spincast.core.exchange.RequestContextBaseDeps;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * Custom request context class
 */
public class AppRequestContext extends RequestContextBase<IAppRequestContext> implements IAppRequestContext {

    @AssistedInject
    public AppRequestContext(@Assisted Object exchange, RequestContextBaseDeps<IAppRequestContext> requestContextBaseDeps) {
        super(exchange, requestContextBaseDeps);
    }

    // Nothing more required for now.
}
