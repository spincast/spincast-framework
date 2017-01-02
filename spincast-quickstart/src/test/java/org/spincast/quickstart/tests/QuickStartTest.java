package org.spincast.quickstart.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;

public class QuickStartTest extends AppTestBase {

    @Test
    public void testIndex() throws Exception {

        HttpResponse response = GET("/").send();
        assertNotNull(response);
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());
        assertTrue(response.getContentAsString().contains("home_page"));
    }

    @Test
    public void test404() throws Exception {

        HttpResponse response = GET("/nope").send();
        assertNotNull(response);
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());
        assertTrue(response.getContentAsString().contains("notFound_page"));
    }

    @Test
    public void testException() throws Exception {

        HttpResponse response = GET("/exception-example").send();
        assertNotNull(response);
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());
        assertTrue(response.getContentAsString().contains("exception_page"));
    }

}
