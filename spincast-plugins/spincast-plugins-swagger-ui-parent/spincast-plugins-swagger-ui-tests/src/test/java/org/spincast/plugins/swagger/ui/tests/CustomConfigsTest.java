package org.spincast.plugins.swagger.ui.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.guice.SpincastPlugin;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.plugins.openapi.bottomup.SpincastOpenApiBottomUpPlugin;
import org.spincast.plugins.swagger.ui.SpincastSwaggerUiPlugin;
import org.spincast.plugins.swagger.ui.config.SpincastSwaggerUiConfig;
import org.spincast.plugins.swagger.ui.config.SpincastSwaggerUiConfigDefault;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.defaults.NoAppStartHttpServerTestingBase;

import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.util.Modules;

public class CustomConfigsTest extends NoAppStartHttpServerTestingBase {


    public static class TestSwaggerUiConfigs extends SpincastSwaggerUiConfigDefault {

        @Override
        public String getSwaggerUiPath() {
            return "/specs/ui";
        }

        @Override
        public String getOpenApiSpecificationsUrl() {
            return "/specifications";
        }
    }

    @Override
    protected List<SpincastPlugin> getExtraPlugins() {
        List<SpincastPlugin> extraPlugins = super.getExtraPlugins();
        extraPlugins.add(new SpincastSwaggerUiPlugin());
        extraPlugins.add(new SpincastOpenApiBottomUpPlugin());
        return extraPlugins;
    }

    @Override
    protected Module getExtraOverridingModule() {
        return Modules.override(super.getExtraOverridingModule()).with(new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(SpincastSwaggerUiConfig.class).to(TestSwaggerUiConfigs.class).in(Scopes.SINGLETON);
            }
        });
    }

    @Override
    protected void clearRoutes() {
        // Do not clear route as it would clear
        // the "/swagger-ui" route added by the
        // "SpincastSwaggerUiPlugin" plugin!
    }

    @Test
    public void customConfigs() throws Exception {

        getRouter().GET("/titi123").handle((context) -> context.response().sendPlainText("titi123!"));

        HttpResponse response = GET("/swagger-ui").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/specs/ui").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());

        String content = response.getContentAsString();
        assertFalse(content.contains("url: \"https://petstore.swagger.io/v2/swagger.json\""));
        assertTrue(content.contains("url: \"/specifications\""));
    }

}
