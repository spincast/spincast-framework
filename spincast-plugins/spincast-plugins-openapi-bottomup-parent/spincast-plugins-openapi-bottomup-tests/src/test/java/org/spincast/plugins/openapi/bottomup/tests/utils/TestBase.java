package org.spincast.plugins.openapi.bottomup.tests.utils;

import java.util.List;

import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.guice.SpincastPlugin;
import org.spincast.core.json.JsonManager;
import org.spincast.core.routing.Router;
import org.spincast.core.websocket.DefaultWebsocketContext;
import org.spincast.plugins.openapi.bottomup.SpincastOpenApiBottomUpPlugin;
import org.spincast.plugins.openapi.bottomup.SpincastOpenApiManager;
import org.spincast.testing.defaults.NoAppTestingBase;

import com.google.inject.Inject;

public abstract class TestBase extends NoAppTestingBase {

    @Override
    protected List<SpincastPlugin> getExtraPlugins() {
        List<SpincastPlugin> extraPlugins = super.getExtraPlugins();
        extraPlugins.add(new SpincastOpenApiBottomUpPlugin());
        return extraPlugins;
    }

    @Override
    public void beforeTest() {
        super.beforeTest();
        getRouter().removeAllRoutes(true);
        getSpincastOpenApiManager().resetAll();
    }

    @Inject
    protected SpincastOpenApiManager spincastOpenApiManager;

    @Inject
    protected Router<DefaultRequestContext, DefaultWebsocketContext> router;

    @Inject
    protected TestController testController;

    @Inject
    protected JsonManager jsonManager;

    protected SpincastOpenApiManager getSpincastOpenApiManager() {
        return this.spincastOpenApiManager;
    }

    protected Router<DefaultRequestContext, DefaultWebsocketContext> getRouter() {
        return this.router;
    }

    protected TestController getTestController() {
        return this.testController;
    }

    protected JsonManager getJsonManager() {
        return this.jsonManager;
    }



}
