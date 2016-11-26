package org.spincast.core.exchange;

/**
 * Assisted factory to create request context instances.
 */
public interface RequestContextFactory<R extends RequestContext<?>> {

    public R createRequestContext(Object exchange);

}
