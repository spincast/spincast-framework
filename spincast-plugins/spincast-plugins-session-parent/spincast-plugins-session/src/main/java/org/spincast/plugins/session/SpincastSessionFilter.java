package org.spincast.plugins.session;

import org.spincast.core.exchange.RequestContext;

/**
 * You should add this filter with the
 * "skipResourcesRequests()" options so it
 * is ignored except for main routes.
 */
public interface SpincastSessionFilter {

    public void before(RequestContext<?> context);

    public void after(RequestContext<?> context);

}
