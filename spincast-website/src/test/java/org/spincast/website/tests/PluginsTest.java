package org.spincast.website.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.plugins.httpclient.IHttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;

public class PluginsTest extends WebsiteIntegrationTestBase {

    @Test
    public void pluginValid() throws Exception {

        IHttpResponse response = GET("/plugins/spincast-request").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());
    }

    @Test
    public void pluginInvalid() throws Exception {

        IHttpResponse response = GET("/plugins/nope").send();

        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());
    }

    @Test
    public void pluginNameSanitization() throws Exception {

        IHttpResponse response = GET("/plugins/.").send();

        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());
    }

}
