package org.spincast.quickstart.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.spincast.core.json.IJsonManager;
import org.spincast.core.json.IJsonObject;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.plugins.httpclient.IHttpResponse;
import org.spincast.quickstart.App;
import org.spincast.quickstart.exchange.IAppRequestContext;
import org.spincast.quickstart.exchange.IAppWebsocketContext;
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
public class QuickStartSumTest extends SpincastIntegrationTestBase<IAppRequestContext, IAppWebsocketContext> {

    @Override
    protected Injector createInjector() {

        Module overridingModule = getDefaultOverridingModule(IAppRequestContext.class,
                                                             IAppWebsocketContext.class);
        return App.createApp(null, overridingModule);
    }

    @Inject
    private IJsonManager jsonManager;

    @Test
    public void validRequest() throws Exception {

        IHttpResponse response = POST("/sum").addEntityFormDataValue("first", "42")
                                             .addEntityFormDataValue("second", "1024")
                                             .addJsonAcceptHeader()
                                             .send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset(),
                     response.getContentType());

        String content = response.getContentAsString();
        assertNotNull(content);

        IJsonObject resultObj = this.jsonManager.create(content);
        assertNotNull(resultObj);

        assertEquals(new Integer(1066), resultObj.getInteger("result"));
        assertNull(resultObj.get("error", null));
    }

    @Test
    public void paramMissing() throws Exception {

        // The "second" parameter is missing...
        IHttpResponse response = POST("/sum").addEntityFormDataValue("first", "42")
                                             .addJsonAcceptHeader()
                                             .send();

        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatus());
        assertEquals(ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset(), response.getContentType());

        String content = response.getContentAsString();
        assertNotNull(content);

        IJsonObject resultObj = this.jsonManager.create(content);
        assertNotNull(resultObj);

        assertNull(resultObj.get("result", null));
        assertEquals("The 'second' post parameter is required.", resultObj.getString("error"));
    }

    @Test
    public void resultOverflows() throws Exception {

        // This will overflow the Integer max value...
        IHttpResponse response = POST("/sum").addEntityFormDataValue("first", String.valueOf(Integer.MAX_VALUE))
                                             .addEntityFormDataValue("second", "1")
                                             .addJsonAcceptHeader()
                                             .send();

        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatus());
        assertEquals(ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset(), response.getContentType());

        String content = response.getContentAsString();
        assertNotNull(content);

        IJsonObject resultObj = this.jsonManager.create(content);
        assertNotNull(resultObj);

        assertNull(resultObj.get("result", null));
        assertEquals("The sum overflows the maximum integer value, " + Integer.MAX_VALUE, resultObj.getString("error"));
    }

}
