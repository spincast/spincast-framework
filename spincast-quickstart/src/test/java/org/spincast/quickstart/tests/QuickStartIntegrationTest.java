package org.spincast.quickstart.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestHttpResponse;

/**
 * Integration tests for the application.
 */
public class QuickStartIntegrationTest extends AppIntegrationTestBase {

    @Test
    public void testGreeting() throws Exception {

        SpincastTestHttpResponse response = get("/greet/Stromgol");

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("Hello Stromgol!", response.getContent());
    }

}
