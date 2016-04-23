package org.spincast.defaults.guice;

import java.util.Set;

import org.spincast.core.routing.RoutingType;
import org.spincast.plugins.routing.ISpincastRouterConfig;

import com.google.common.collect.Sets;

public class DefaultSpincastRouterConfig implements ISpincastRouterConfig {

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

}
