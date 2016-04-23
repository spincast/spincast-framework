package org.spincast.core.exchange;

/**
 * Assisted factory to create request context instances.
 */
public interface IRequestContextFactory<R extends IRequestContext<?>> {

    public R createRequestContext(Object exchange);

}
