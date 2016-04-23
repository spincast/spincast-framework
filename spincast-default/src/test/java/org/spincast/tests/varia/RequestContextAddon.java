package org.spincast.tests.varia;

import static org.junit.Assert.assertNotNull;

import org.spincast.core.config.ISpincastConfig;
import org.spincast.core.exchange.IRequestContext;

import com.google.inject.Inject;

public class RequestContextAddon implements IRequestContextAddon {

    private final IRequestContext<?> requestContext;

    @Inject
    public RequestContextAddon(IRequestContext<?> requestContext,
                               ISpincastConfig spincastConfig) {
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
