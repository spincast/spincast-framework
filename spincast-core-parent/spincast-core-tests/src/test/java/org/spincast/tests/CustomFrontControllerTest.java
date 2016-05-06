package org.spincast.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.spincast.defaults.tests.DefaultIntegrationTestingBase;
import org.spincast.defaults.tests.DefaultTestingModule;
import org.spincast.plugins.httpclient.IHttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.tests.varia.CustomFrontController;

import com.google.inject.Key;
import com.google.inject.Module;

public class CustomFrontControllerTest extends DefaultIntegrationTestingBase {

    /**
     * Custom module
     */
    @Override
    public Module getTestingModule() {
        return new DefaultTestingModule() {

            @Override
            protected Key<?> getFrontControllerKey() {
                return Key.get(CustomFrontController.class);
            }
        };
    }

    @Test
    public void customExchangeValidation() throws Exception {

        IHttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());

    }

}