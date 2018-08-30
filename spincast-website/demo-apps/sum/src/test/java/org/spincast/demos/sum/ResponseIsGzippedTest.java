package org.spincast.demos.sum;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.guice.GuiceTweaker;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.testing.AppBasedDefaultContextTypesTestingBase;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.AppTestingConfigs;
import org.spincast.testing.core.utils.SpincastConfigTestingDefault;

import com.google.inject.Module;
import com.google.inject.Scopes;

public class ResponseIsGzippedTest extends AppBasedDefaultContextTypesTestingBase {

    @Override
    protected void callAppMainMethod() {
        App.main(null);
    }

    @Override
    protected AppTestingConfigs getAppTestingConfigs() {
        return new AppTestingConfigs() {

            @Override
            public boolean isBindAppClass() {
                return true;
            }

            @Override
            public Class<? extends SpincastConfig> getSpincastConfigTestingImplementationClass() {
                return SpincastConfigTestingDefault.class;
            }

            @Override
            public Class<?> getAppConfigTestingImplementationClass() {
                return null;
            }

            @Override
            public Class<?> getAppConfigInterface() {
                return null;
            }
        };
    }

    /**
     * AppController mock : simply returns "42" on the "/sum"
     * endpoint.
     */
    public static class AppControllerTesting extends AppControllerDefault {

        @Override
        public void sumRoute(DefaultRequestContext context) {
            context.response().sendPlainText("42");
        }
    }

    /**
     * We add an extra overriding module to change the
     * "AppController" binding so our "AppControllerTesting" mock
     * implementation is used... Under the hood, this is done
     * by the {@link GuiceTweaker Guice Tweaker}.
     */
    @Override
    protected Module getExtraOverridingModule() {
        return new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(AppController.class).to(AppControllerTesting.class).in(Scopes.SINGLETON);
            }
        };
    }

    @Test
    public void isGzipped() throws Exception {

        HttpResponse response = POST("/sum").addFormBodyFieldValue("first", "Stromgol")
                                            .addJsonAcceptHeader()
                                            .send();

        assertTrue(response.isGzipped());

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(),
                     response.getContentType());
        assertEquals("42", response.getContentAsString());
    }


}
