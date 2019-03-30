package org.spincast.plugins.openapi.bottomup.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.plugins.openapi.bottomup.Specs;
import org.spincast.plugins.openapi.bottomup.SpecsObject;
import org.spincast.plugins.openapi.bottomup.config.SpincastOpenApiBottomUpPluginConfig;
import org.spincast.plugins.openapi.bottomup.tests.utils.TestBase;

import com.google.inject.Module;
import com.google.inject.util.Modules;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.responses.ApiResponses;

public class NoConsumesAndProducesNullTest extends TestBase {

    @Override
    protected Module getExtraOverridingModule() {
        return Modules.override(super.getExtraOverridingModule()).with(new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(SpincastOpenApiBottomUpPluginConfig.class).toInstance(new SpincastOpenApiBottomUpPluginConfig() {

                    @Override
                    public String[] getDefaultConsumesContentTypes() {
                        //==========================================
                        // No @Consumes!
                        //==========================================
                        return null;
                    }

                    @Override
                    public String[] getDefaultProducesContentTypes() {
                        //==========================================
                        // No @Produces!
                        //==========================================
                        return null;
                    }

                    @Override
                    public boolean isDisableAutoSpecs() {
                        return false;
                    }
                });
            }
        });
    }

    @Test
    public void noProducesAndConsumes() {

        getRouter().GET("/tutu")
                   .specs(new @Specs(@Operation(summary = "My summary",
                                                description = "My description",
                                                requestBody = @RequestBody(content = {@Content()},
                                                                           required = true)

                   )) SpecsObject() {})
                   .handle(getTestController()::sayHello);

        OpenAPI openApi = getSpincastOpenApiManager().getOpenApi();
        assertNotNull(openApi);

        PathItem pathItem = openApi.getPaths().get("/tutu");
        assertNotNull(pathItem);

        io.swagger.v3.oas.models.Operation getOperation = pathItem.getGet();
        assertNotNull(getOperation);

        io.swagger.v3.oas.models.parameters.RequestBody requestBody = getOperation.getRequestBody();
        assertNotNull(requestBody);

        io.swagger.v3.oas.models.media.Content requestContent = requestBody.getContent();
        assertNotNull(requestContent);
        assertEquals(1, requestContent.size());
        assertEquals("*/*", requestContent.keySet().iterator().next());


        ApiResponses responses = getOperation.getResponses();
        assertNotNull(responses);

        io.swagger.v3.oas.models.responses.ApiResponse defaultResponse = responses.getDefault();
        assertNotNull(defaultResponse);

        io.swagger.v3.oas.models.media.Content responseContent = defaultResponse.getContent();
        assertNotNull(responseContent);
        assertEquals(1, responseContent.size());
        assertEquals("*/*", responseContent.keySet().iterator().next());
    }

}
