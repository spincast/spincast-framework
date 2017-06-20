package org.spincast.website.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;

public class AdminProtectionTest extends WebsiteIntegrationTestBase {

    @Test
    public void noCredentials() throws Exception {

        HttpResponse response = GET("/admin").send();
        assertEquals(HttpStatus.SC_UNAUTHORIZED, response.getStatus());
    }

}
