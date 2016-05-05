package org.spincast.defaults.tests;

import java.util.Set;

import org.spincast.core.routing.RoutingType;
import org.spincast.plugins.routing.ISpincastRouterConfig;

import com.google.common.collect.Sets;

public class SpincastTestSpincastRoutingPluginConfig implements ISpincastRouterConfig {

    @Override
    public Set<RoutingType> getFilterDefaultRoutingTypes() {
        return Sets.newHashSet(RoutingType.values());
    }

    @Override
    public int getCorsFilterPosition() {
        return -100;
    }

}
