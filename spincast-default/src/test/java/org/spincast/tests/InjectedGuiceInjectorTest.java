package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.spincast.core.config.ISpincastConfig;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.routing.IHandler;
import org.spincast.defaults.tests.DefaultIntegrationTestingBase;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestHttpResponse;

public class InjectedGuiceInjectorTest extends DefaultIntegrationTestingBase {

    @Test
    public void test() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                ISpincastConfig spincastConfig = context.guice().getInstance(ISpincastConfig.class);
                assertNotNull(spincastConfig);

            }
        });

        SpincastTestHttpResponse response = get("/one");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

}
