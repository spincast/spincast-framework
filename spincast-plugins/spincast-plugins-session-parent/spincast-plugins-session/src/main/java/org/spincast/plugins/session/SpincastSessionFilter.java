package org.spincast.plugins.session;

import org.spincast.core.exchange.RequestContext;

/**
 * You should add this filter with the
 * "skipResourcesRequests()" options so it
 * is ignored except for main routes.
 */
public interface SpincastSessionFilter {

    /**
     * The route id of the <em>before</em> filter,
     * if added automatically.
     */
    public static final String ROUTE_ID_BEFORE_FILTER = "sessionBeforeFilter";

    /**
     * The route id of the <em>after</em> filter,
     * if added automatically.
     */
    public static final String ROUTE_ID_AFTER_FILTER = "sessionAfterFilter";

    public void before(RequestContext<?> context);

    public void after(RequestContext<?> context);

}
