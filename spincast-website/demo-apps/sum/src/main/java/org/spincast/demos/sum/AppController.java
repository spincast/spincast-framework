package org.spincast.demos.sum;

import org.spincast.core.exchange.DefaultRequestContext;

public interface AppController {

    /**
     * Sum Route Handler
     */
    public void sumRoute(DefaultRequestContext context);
}
