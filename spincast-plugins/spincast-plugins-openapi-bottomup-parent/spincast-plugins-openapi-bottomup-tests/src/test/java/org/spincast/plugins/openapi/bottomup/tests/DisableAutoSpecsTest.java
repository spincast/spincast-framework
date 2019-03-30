package org.spincast.plugins.openapi.bottomup.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.plugins.openapi.bottomup.config.SpincastOpenApiBottomUpPluginConfig;
import org.spincast.plugins.openapi.bottomup.tests.utils.TestBase;

import com.google.inject.Module;
import com.google.inject.util.Modules;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

public class DisableAutoSpecsTest extends TestBase {

    @Override
    protected Module getExtraOverridingModule() {
        return Modules.override(super.getExtraOverridingModule()).with(new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(SpincastOpenApiBottomUpPluginConfig.class).toInstance(new SpincastOpenApiBottomUpPluginConfig() {

                    @Override
                    public String[] getDefaultConsumesContentTypes() {
                        return new String[]{"application/default1"};
                    }

                    @Override
                    public String[] getDefaultProducesContentTypes() {
                        return new String[]{"application/default2"};
                    }

                    @Override
                    public boolean isDisableAutoSpecs() {
                        //==========================================
                        // True!
                        //==========================================
                        return true;
                    }
                });
            }
        });
    }

    @Test
    public void baseOpenAPI() {
        OpenAPI openApiBase = new OpenAPI();
        Info info = new Info().title("A global Title")
                              .description("A global description")
                              .termsOfService("https://example.com")
                              .contact(new Contact()
                                                    .email("stromgol@example.com"))
                              .license(new License().name("Apache 2.0")
                                                    .url("http://www.apache.org/licenses/LICENSE-2.0.html"));

        openApiBase.info(info);

        Paths paths = new Paths();
        PathItem pathItem = new PathItem();
        io.swagger.v3.oas.models.Operation operation = new io.swagger.v3.oas.models.Operation();
        operation.setDescription("titi description");
        pathItem.setDelete(operation);
        paths.addPathItem("/titi", pathItem);
        openApiBase.setPaths(paths);

        getSpincastOpenApiManager().setOpenApiBase(openApiBase);

        getRouter().GET("/tutu").handle(getTestController()::sayHello);

        OpenAPI openApi = getSpincastOpenApiManager().getOpenApi();
        assertNotNull(openApi);
        assertNotNull(openApi.getInfo().getContact());
        assertNotNull(openApi.getInfo().getLicense());
        assertNotNull(openApi.getInfo().getDescription());
        assertNotNull(openApi.getInfo().getTitle());

        //==========================================
        // Should not be documented!
        //==========================================
        PathItem pathItem2 = openApi.getPaths().get("/tutu");
        assertNull(pathItem2);

        io.swagger.v3.oas.models.Operation delete = openApi.getPaths().get("/titi").getDelete();
        assertNotNull(delete);
        assertEquals("titi description", delete.getDescription());
    }


}
