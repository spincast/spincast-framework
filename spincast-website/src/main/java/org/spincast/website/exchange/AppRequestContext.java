package org.spincast.website.exchange;

import org.spincast.core.exchange.RequestContextBase;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * Custom request context class
 */
public class AppRequestContext extends RequestContextBase<IAppRequestContext> implements IAppRequestContext {

    @AssistedInject
    public AppRequestContext(@Assisted Object exchange) {
        super(exchange);
    }

    // Nothing more required for now.
}
