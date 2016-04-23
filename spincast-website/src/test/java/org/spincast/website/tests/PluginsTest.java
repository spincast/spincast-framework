package org.spincast.website.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestHttpResponse;

public class PluginsTest extends AppIntegrationTestBase {

    @Test
    public void pluginValid() throws Exception {

        SpincastTestHttpResponse response = get("/plugins/spincast-request");

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());
    }

    @Test
    public void pluginInvalid() throws Exception {

        SpincastTestHttpResponse response = get("/plugins/nope");

        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());
    }

    @Test
    public void pluginNameSanitization() throws Exception {

        SpincastTestHttpResponse response = get("/plugins/.");

        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());
    }

}
