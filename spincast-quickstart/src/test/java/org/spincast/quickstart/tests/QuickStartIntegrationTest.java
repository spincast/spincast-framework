package org.spincast.quickstart.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;

/**
 * Integration tests for the application.
 */
public class QuickStartIntegrationTest extends AppIntegrationTestBase {

    @Test
    public void testGreeting() throws Exception {

        HttpResponse response = GET("/greet/Stromgol").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("Hello Stromgol!", response.getContentAsString());
    }

}
