package org.spincast.plugins.routing;

import java.util.Set;

import org.spincast.core.routing.RoutingType;

import com.google.inject.ImplementedBy;

/**
 * Configurations for the Spincast Router.
 * 
 * We use "@ImplementedBy" to specify the default configurations
 * to use if none is specified in a Guice module.
 */
@ImplementedBy(SpincastRouterConfigDefault.class)
public interface ISpincastRouterConfig {

    /**
     * The routing types to apply a filter to when none is
     * explicitly specified.
     */
    public Set<RoutingType> getFilterDefaultRoutingTypes();

    /**
     * The default position  for a <code>cors</code> "before" filter.
     * Must be &lt; 0 ! 
     */
    public int getCorsFilterPosition();

}
