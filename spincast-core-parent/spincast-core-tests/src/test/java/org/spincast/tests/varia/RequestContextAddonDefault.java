package org.spincast.tests.varia;

import static org.junit.Assert.assertNotNull;

import org.spincast.core.config.SpincastConfig;
import org.spincast.core.exchange.RequestContext;

import com.google.inject.Inject;

public class RequestContextAddonDefault implements RequestContextAddon {

    private final RequestContext<?> requestContext;

    @Inject
    public RequestContextAddonDefault(RequestContext<?> requestContext,
                                      SpincastConfig spincastConfig) {
        this.requestContext = requestContext;
        assertNotNull(spincastConfig);
    }

    @Override
    public void addonMethod1() {
        this.requestContext.response().sendPlainText("addonMethod1");
    }

    @Override
    public String addonMethod2() {
        return "addonMethod2";
    }
}
