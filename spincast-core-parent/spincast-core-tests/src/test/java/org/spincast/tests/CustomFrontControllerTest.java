package org.spincast.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.spincast.core.controllers.FrontController;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.defaults.testing.IntegrationTestNoAppDefaultContextsBase;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.tests.varia.CustomFrontController;

import com.google.inject.Module;
import com.google.inject.Scopes;

public class CustomFrontControllerTest extends IntegrationTestNoAppDefaultContextsBase {

    @Override
    protected Module getGuiceTweakerOverridingModule() {
        return new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(FrontController.class).to(CustomFrontController.class).in(Scopes.SINGLETON);
            }
        };
    }

    @Test
    public void customExchangeValidation() throws Exception {

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());

    }
}
