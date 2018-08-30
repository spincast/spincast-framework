package org.spincast.tests.appbasedtesting.app;

import org.spincast.core.exchange.RequestContext;

public interface AppRequestContext extends RequestContext<AppRequestContext> {

    public String getCustomMethodOutput();
}
