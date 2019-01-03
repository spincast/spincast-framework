package org.spincast.website.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.website.AppConfig;

import com.google.inject.Inject;

public class PluginsTest extends WebsiteIntegrationTestBase {

    @Inject
    protected AppConfig appConfig;

    protected AppConfig getAppConfig() {
        return this.appConfig;
    }

    @Test
    public void pluginValid() throws Exception {

        HttpResponse response = GET("/plugins/spincast-request", false, false).send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());
    }

    @Test
    public void pluginInvalid() throws Exception {

        HttpResponse response = GET("/plugins/nope", false, false).send();

        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());
    }

    @Test
    public void pluginNameSanitization() throws Exception {

        HttpResponse response = GET("/plugins/.", false, false).send();

        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());
    }

}
