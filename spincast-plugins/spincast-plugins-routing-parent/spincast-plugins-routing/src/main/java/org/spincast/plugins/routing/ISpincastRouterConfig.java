package org.spincast.plugins.routing;

import java.util.Set;

import org.spincast.core.routing.RoutingType;

/**
 * Configurations for the Spincast Router.
 * 
 */
public interface ISpincastRouterConfig {

    /**
     * The routing types to use when none is
     * explicitally specified.
     */
    public Set<RoutingType> getFilterDefaultRoutingTypes();

    /**
     * The position  for a cors "before" filter.
     * MUST be < 0 ! 
     */
    public int getCorsFilterPosition();

}
