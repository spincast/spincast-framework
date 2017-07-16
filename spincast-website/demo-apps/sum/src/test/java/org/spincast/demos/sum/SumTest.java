package org.spincast.demos.sum;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.json.JsonManager;
import org.spincast.core.json.JsonObject;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.testing.AppBasedDefaultContextTypesTestingBase;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.AppTestingConfigs;
import org.spincast.testing.core.utils.SpincastConfigTestingDefault;

import com.google.inject.Inject;

public class SumTest extends AppBasedDefaultContextTypesTestingBase {

    @Override
    protected void startApp() {
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
