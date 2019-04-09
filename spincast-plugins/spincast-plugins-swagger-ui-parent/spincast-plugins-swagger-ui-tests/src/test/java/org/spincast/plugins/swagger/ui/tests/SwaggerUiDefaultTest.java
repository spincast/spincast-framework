package org.spincast.plugins.swagger.ui.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.spincast.core.guice.SpincastPlugin;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.plugins.openapi.bottomup.SpincastOpenApiBottomUpPlugin;
import org.spincast.plugins.swagger.ui.SpincastSwaggerUiPlugin;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.defaults.NoAppStartHttpServerTestingBase;

public class SwaggerUiDefaultTest extends NoAppStartHttpServerTestingBase {

    @Override
    protected List<SpincastPlugin> getExtraPlugins() {
        List<SpincastPlugin> extraPlugins = super.getExtraPlugins();
        extraPlugins.add(new SpincastSwaggerUiPlugin());
        extraPlugins.add(new SpincastOpenApiBottomUpPlugin());
        return extraPlugins;
    }

    @Override
    protected void clearRoutes() {
        // Do not clear route as it would clear
        // the "/swagger-ui" route added by the
        // "SpincastSwaggerUiPlugin" plugin!
    }

    @Test
    public void defaultConfigs() throws Exception {

        getRouter().GET("/titi123").handle((context) -> context.response().sendPlainText("titi123!"));

        HttpResponse response = GET("/swagger-ui").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());

        String content = response.getContentAsString();
        assertTrue(content.contains("url: \"https://petstore.swagger.io/v2/swagger.json\""));
    }

}
