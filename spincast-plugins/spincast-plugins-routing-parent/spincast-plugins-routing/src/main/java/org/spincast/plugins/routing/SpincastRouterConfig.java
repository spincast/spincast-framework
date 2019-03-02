package org.spincast.plugins.routing;

import java.util.Set;

import org.spincast.core.routing.RoutingType;

import com.google.inject.ImplementedBy;

/**
 * Configurations for the Spincast Router/routing.
 * <p>
 * We use "@ImplementedBy" to specify the default configurations
 * to use if none is specified in a Guice module.
 * </p>
 */
@ImplementedBy(SpincastRouterConfigDefault.class)
public interface SpincastRouterConfig {

    /**
     * The routing types to apply a filter (none "0" position) 
     * when none is explicitly specified.
     */
    public Set<RoutingType> getFilterDefaultRoutingTypes();

    /**
     * The default position for a <code>cors</code> "before" filter.
     * Must be &lt; 0 ! 
     */
    public int getCorsFilterPosition();

    /**
     * The default position for a <code>redirect</code> "before" filter.
     * Must be &lt; 0 and should probably be one of the very, very
     * first filter to run! 
     */
    public int getRedirectFilterPosition();

}
