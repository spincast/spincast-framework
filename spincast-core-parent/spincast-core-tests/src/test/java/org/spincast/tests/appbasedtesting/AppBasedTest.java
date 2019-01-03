package org.spincast.tests.appbasedtesting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.json.JsonManager;
import org.spincast.core.json.JsonObject;
import org.spincast.core.routing.Handler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.websocket.DefaultWebsocketContext;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.AppBasedTestingBase;
import org.spincast.testing.core.AppTestingConfigs;
import org.spincast.testing.core.utils.SpincastConfigTestingDefault;
import org.spincast.tests.appbasedtesting.app.App;
import org.spincast.tests.appbasedtesting.app.AppConfigs;
import org.spincast.tests.appbasedtesting.app.AppRequestContext;

import com.google.inject.Inject;

public class AppBasedTest extends AppBasedTestingBase<AppRequestContext, DefaultWebsocketContext> {

    @Override
    protected void callAppMainMethod() {

        //==========================================
        // We simply have to call the "main()" method
        // here...
        // The server will be automatically started
        // by the "init" method in the App class which
        // will be called once the Guice context is
        // created!
        //==========================================
        App.main(null);
    }

    @Inject
    protected AppConfigs appConfigs;

    protected AppConfigs getAppConfigs() {
        return this.appConfigs;
    }

    @Inject
    protected JsonManager jsonManager;

    protected JsonManager getJsonManager() {
        return this.jsonManager;
    }

    //==========================================
    // We tell the testing library what to use
    // for the configurations.
    //
    // An interesting thing to note is that this
    // allows us to specify what implementation to
    // use for the "SpincastConfig" binding and
    // which one to use for the "AppConfigs"
    // binding... And those two implementations
    // can be different! So we can both 
    // extends the "AppConfigs" to tweak it, and
    // still also extend or directly use
    // "SpincastConfigTestingDefault"...
    //==========================================
    @Override
    protected AppTestingConfigs getAppTestingConfigs() {

        return new AppTestingConfigs() {

            @Override
            public boolean isBindAppClass() {
                //==========================================
                // True, so the app is initialized regularly
                // and the server is started.
                //==========================================
                return true;
            }

            @Override
            public Class<? extends SpincastConfig> getSpincastConfigTestingImplementationClass() {

                //==========================================
                // We use the default testing config provided
                // by Spincast for the "SpincastConfig" binding.
                // This makes sure, for example, that the server
                // will start on a random and free port.
                //==========================================
                return SpincastConfigTestingDefault.class;
            }

            @Override
            public Class<?> getAppConfigTestingImplementationClass() {

                //==========================================
                // Custom testing application configs
                //==========================================
                return TestingAppConfig.class;
            }

            @Override
            public Class<?> getAppConfigInterface() {

                //==========================================
                // The configurations interface uses by the real 
                // application is required : we'll change its
                // binding so it uses the "getAppConfigTestingImplementationClass()"
                // testing implementation class!
                //==========================================
                return AppConfigs.class;
            }
        };
    }

    @Test
    public void testingAppConfigsUsed() throws Exception {
        assertEquals("testing", getAppConfigs().getCustomConfig());
    }

    @Test
    public void testinSpincastConfigsUsed() throws Exception {
        assertNotNull(getAppConfigs().getHttpsServerPort());
        assertNotNull(getSpincastConfig().getHttpsServerPort());
        assertEquals(getAppConfigs().getHttpsServerPort(), getSpincastConfig().getHttpsServerPort());
    }

    @Test
    public void appRoute() throws Exception {

        //==========================================
        // The "/" route is defined by the App
        // itself
        //==========================================
        HttpResponse response = GET("/").setCookies(getPreviousResponseCookies()).send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        saveResponseCookies(response);
        assertEquals(ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset(), response.getContentType());

        JsonObject obj = response.getContentAsJsonObject();
        assertNotNull(obj);

        JsonObject expected = getJsonManager().create();
        expected.set("k1", "v1");
        expected.set("k2", "v2");
        assertTrue(obj.isEquivalentTo(expected));
    }

    @Test
    public void addNewRoute() throws Exception {

        //==========================================
        // Adds a route to the router
        //==========================================
        getRouter().GET("/testing123").handle(new Handler<AppRequestContext>() {

            @Override
            public void handle(AppRequestContext context) {
                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/testing123").setCookies(getPreviousResponseCookies()).send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        saveResponseCookies(response);
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());

        String content = response.getContentAsString();
        assertNotNull(content);
        assertEquals("ok", content);
    }



}
