package org.spincast.testing.defaults.tests.utils;

import org.spincast.core.exchange.RequestContext;

/**
 * Custom Request Context type
 */
public interface RequestContextTesting extends RequestContext<RequestContextTesting> {

    public String test();
}
