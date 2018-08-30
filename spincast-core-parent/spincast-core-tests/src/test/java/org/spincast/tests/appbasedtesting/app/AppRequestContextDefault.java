package org.spincast.tests.appbasedtesting.app;

import org.spincast.core.exchange.RequestContextBase;
import org.spincast.core.exchange.RequestContextBaseDeps;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * When you extend this class, don't forget to annotate
 * the constructor with {@literal @}
 */
public class AppRequestContextDefault extends RequestContextBase<AppRequestContext> implements AppRequestContext {

    @AssistedInject
    public AppRequestContextDefault(@Assisted Object exchange,
                                    RequestContextBaseDeps<AppRequestContext> requestContextBaseDeps) {
        super(exchange, requestContextBaseDeps);
    }

    @Override
    public String getCustomMethodOutput() {
        return "Stromgol!";
    }
}
