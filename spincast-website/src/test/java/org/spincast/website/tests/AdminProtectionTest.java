package org.spincast.website.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.website.AppConfigDefault;

public class AdminProtectionTest extends WebsiteIntegrationTestBase {

    @Override
    protected String getAppConfigPropertiesFileContent() {

        String content = super.getAppConfigPropertiesFileContent();

        content += AppConfigDefault.APP_PROPERTIES_KEY_ADMIN_CREDENTIAL_KEYS + "=test.cred1, test.cred2" + "\n";
        content += "test.cred1=user1, pass1" + "\n";
        content += "test.cred2=user2,pass2" + "\n";

        return content;
    }

    @Test
    public void noCredentials() throws Exception {

        HttpResponse response = GET("/admin").send();

        assertEquals(HttpStatus.SC_UNAUTHORIZED, response.getStatus());

    }

}
