package org.spincast.plugins.openapi.bottomup.tests.utils;

import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.plugins.openapi.bottomup.SpincastOpenApiManager;

import com.google.inject.Inject;

public class TestController {

    private final SpincastOpenApiManager spincastOpenApiManager;

    @Inject
    public TestController(SpincastOpenApiManager spincastOpenApiManager) {
        this.spincastOpenApiManager = spincastOpenApiManager;
    }

    protected SpincastOpenApiManager getSpincastOpenApiManager() {
        return this.spincastOpenApiManager;
    }

    public void sayHello(DefaultRequestContext context) {
        context.response().sendPlainText("Hello!");
    }

    public void specsJson(DefaultRequestContext context) {
        context.response().sendJson(getSpincastOpenApiManager().getOpenApiAsJson());
    }

    public void specsYaml(DefaultRequestContext context) {
        context.response().sendCharacters(getSpincastOpenApiManager().getOpenApiAsYaml(), "text/yaml");
    }

}
