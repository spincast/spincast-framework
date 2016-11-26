package org.spincast.quickstart.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.spincast.core.json.JsonManager;
import org.spincast.core.json.JsonObject;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.quickstart.App;
import org.spincast.quickstart.exchange.AppRequestContext;
import org.spincast.quickstart.exchange.AppWebsocketContext;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.SpincastIntegrationTestBase;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * This test class only contains tests for the "/sum" route, because
 * we use it as an example in the Spincast documentation as an example as how
 * to create a test. 
 * 
 * It could extend <code>AppIntegrationTestBase</code> to be simpler, but we wanted to 
 * show how to extend a Spincast base class directly.
 */
public class QuickStartSumTest extends SpincastIntegrationTestBase<AppRequestContext, AppWebsocketContext> {

    @Override
    protected Injector createInjector() {

        Module overridingModule = getDefaultOverridingModule(AppRequestContext.class,
                                                             AppWebsocketContext.class);
        return App.createApp(null, overridingModule);
    }

    @Inject
    private JsonManager jsonManager;

    @Test
    public void validRequest() throws Exception {

        HttpResponse response = POST("/sum").addEntityFormDataValue("first", "42")
                                             .addEntityFormDataValue("second", "1024")
                                             .addJsonAcceptHeader()
                                             .send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset(),
                     response.getContentType());

        String content = response.getContentAsString();
        assertNotNull(content);

        JsonObject resultObj = this.jsonManager.fromString(content);
        assertNotNull(resultObj);

        assertEquals(new Integer(1066), resultObj.getInteger("result"));
        assertNull(resultObj.getString("error", null));
    }

    @Test
    public void paramMissing() throws Exception {

        // The "second" parameter is missing...
        HttpResponse response = POST("/sum").addEntityFormDataValue("first", "42")
                                             .addJsonAcceptHeader()
                                             .send();

        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatus());
        assertEquals(ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset(), response.getContentType());

        String content = response.getContentAsString();
        assertNotNull(content);

        JsonObject resultObj = this.jsonManager.fromString(content);
        assertNotNull(resultObj);

        assertNull(resultObj.getString("result", null));
        assertEquals("The 'second' post parameter is required.", resultObj.getString("error"));
    }

    @Test
    public void resultOverflows() throws Exception {

        // This will overflow the Integer max value...
        HttpResponse response = POST("/sum").addEntityFormDataValue("first", String.valueOf(Integer.MAX_VALUE))
                                             .addEntityFormDataValue("second", "1")
                                             .addJsonAcceptHeader()
                                             .send();

        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatus());
        assertEquals(ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset(), response.getContentType());

        String content = response.getContentAsString();
        assertNotNull(content);

        JsonObject resultObj = this.jsonManager.fromString(content);
        assertNotNull(resultObj);

        assertNull(resultObj.getString("result", null));
        assertEquals("The sum overflows the maximum integer value, " + Integer.MAX_VALUE, resultObj.getString("error"));
    }

}
