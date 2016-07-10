package org.spincast.plugins.routing;

import java.util.Set;

import org.spincast.core.routing.RoutingType;

import com.google.common.collect.Sets;

/**
 * Default configuration for the Spincast Router.
 */
public class SpincastRouterConfigDefault implements ISpincastRouterConfig {

    @Override
    public Set<RoutingType> getFilterDefaultRoutingTypes() {

        //==========================================
        // By default we apply the filters on all
        // route types.
        //==========================================
        return Sets.newHashSet(RoutingType.values());
    }

    @Override
    public int getCorsFilterPosition() {
        return -100;
    }

    @Override
    public int getRedirectFilterPosition() {
        return -1000;
    }

}
