package org.spincast.plugins.openapi.bottomup.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.apache.http.HttpStatus;
import org.junit.Test;
import org.spincast.core.guice.SpincastPlugin;
import org.spincast.core.json.JsonObject;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.plugins.openapi.bottomup.SpincastOpenApiBottomUpPlugin;
import org.spincast.plugins.openapi.bottomup.SpincastOpenApiManager;
import org.spincast.plugins.openapi.bottomup.tests.utils.TestController;
import org.spincast.testing.defaults.NoAppStartHttpServerTestingBase;

import com.google.inject.Inject;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;

public class ServingSpecsTest extends NoAppStartHttpServerTestingBase {

    @Override
    protected List<SpincastPlugin> getExtraPlugins() {
        List<SpincastPlugin> extraPlugins = super.getExtraPlugins();
        extraPlugins.add(new SpincastOpenApiBottomUpPlugin());
        return extraPlugins;
    }

    @Inject
    protected TestController testController;


    @Inject
    protected SpincastOpenApiManager spincastOpenApiManager;

    @Test
    public void servingSpecs() {

        getRouter().GET("/tutu").handle(this.testController::sayHello);
        getRouter().GET("/specs.json").specsIgnore().handle(this.testController::specsJson);
        getRouter().GET("/specs.yaml").specsIgnore().handle(this.testController::specsYaml);

        HttpResponse response = GET("/specs.json").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());

        JsonObject specs = response.getContentAsJsonObject();
        assertNotNull(specs);
        JsonObject content = specs.getJsonObject("paths['/tutu'].get.responses.default.content['application/json']");
        assertNotNull(content);

        response = GET("/specs.yaml").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());

        String yamlSpecs = response.getContentAsString();
        assertNotNull(yamlSpecs);

        SwaggerParseResult readContents = new OpenAPIV3Parser().readContents(yamlSpecs);
        OpenAPI openAPI = readContents.getOpenAPI();
        assertNotNull(openAPI);
        assertNotNull(openAPI.getPaths().get("/tutu").getGet());
    }

    @Test
    public void servingSpecsAsDynamicResource() {

        int[] countJsonCall = new int[]{0};
        int[] countYamlCall = new int[]{0};

        getRouter().GET("/tutu").handle(this.testController::sayHello);
        getRouter().file("/specifications.json").pathRelative("/generated/specifications.json").handle((context) -> {
            countJsonCall[0]++;
            context.response().sendJson(this.spincastOpenApiManager.getOpenApiAsJson());
        });
        getRouter().file("/specifications.yaml").pathRelative("/generated/specifications.yaml").handle((context) -> {
            countYamlCall[0]++;
            context.response().sendCharacters(this.spincastOpenApiManager.getOpenApiAsYaml(), "text/yaml");
        });

        HttpResponse response = GET("/specifications.json").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());

        JsonObject specs = response.getContentAsJsonObject();
        assertNotNull(specs);
        JsonObject content = specs.getJsonObject("paths['/tutu'].get.responses.default.content['application/json']");
        assertNotNull(content);

        response = GET("/specifications.json").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(1, countJsonCall[0]);

        response = GET("/specifications.yaml").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());

        String yamlSpecs = response.getContentAsString();
        assertNotNull(yamlSpecs);

        SwaggerParseResult readContents = new OpenAPIV3Parser().readContents(yamlSpecs);
        OpenAPI openAPI = readContents.getOpenAPI();
        assertNotNull(openAPI);
        assertNotNull(openAPI.getPaths().get("/tutu").getGet());

        response = GET("/specifications.yaml").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(1, countJsonCall[0]);

    }

}
