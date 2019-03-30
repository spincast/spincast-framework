package org.spincast.plugins.openapi.bottomup.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;

import org.junit.Test;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.json.JsonObject;
import org.spincast.core.routing.HttpMethod;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.websocket.DefaultWebsocketContext;
import org.spincast.plugins.openapi.bottomup.Specs;
import org.spincast.plugins.openapi.bottomup.SpecsObject;
import org.spincast.plugins.openapi.bottomup.config.SpincastOpenApiBottomUpPluginConfig;
import org.spincast.plugins.openapi.bottomup.tests.utils.Nope;
import org.spincast.plugins.openapi.bottomup.tests.utils.TestBase;
import org.spincast.plugins.openapi.bottomup.tests.utils.TestWebsocketController;
import org.yaml.snakeyaml.Yaml;

import com.google.inject.Module;
import com.google.inject.util.Modules;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.responses.ApiResponses;

public class OpenApiTest extends TestBase {

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
                        return false;
                    }
                });
            }
        });
    }

    @Test
    public void defaultSpecs() {

        getRouter().POST("/tutu/").handle(getTestController()::sayHello);

        OpenAPI openApi = getSpincastOpenApiManager().getOpenApi();
        PathItem pathItem = openApi.getPaths().get("/tutu");
        assertNotNull(pathItem);

        io.swagger.v3.oas.models.Operation post = pathItem.getPost();
        assertNotNull(post);

        assertEquals("application/default2",
                     post.getResponses().values().iterator().next().getContent().keySet().iterator().next());
    }

    @Test
    public void defaultSpecsNullSpecs() {

        getRouter().POST("/tutu/").specs(null).handle(getTestController()::sayHello);

        OpenAPI openApi = getSpincastOpenApiManager().getOpenApi();
        PathItem pathItem = openApi.getPaths().get("/tutu");
        assertNotNull(pathItem);

        io.swagger.v3.oas.models.Operation post = pathItem.getPost();
        assertNotNull(post);

        assertEquals("application/default2",
                     post.getResponses().values().iterator().next().getContent().keySet().iterator().next());
    }

    @Test
    public void dynamicParameters() {

        getRouter().POST("/tutu/${param1}/${param2}/${param3:\\d+}/${param4:<A>}/${param5:<A+>}/${param6:<N>}/${param7:<N+>}/${param8:<AN>}/${param9:<AN+>}")
                   .handle(getTestController()::sayHello);

        OpenAPI openApi = getSpincastOpenApiManager().getOpenApi();
        PathItem pathItem =
                openApi.getPaths().get("/tutu/{param1}/{param2}/{param3}/{param4}/{param5}/{param6}/{param7}/{param8}/{param9}");
        assertNotNull(pathItem);

        io.swagger.v3.oas.models.Operation post = pathItem.getPost();
        assertNotNull(post);

        List<io.swagger.v3.oas.models.parameters.Parameter> parameters = post.getParameters();
        assertNotNull(parameters);
        assertEquals(9, parameters.size());

        io.swagger.v3.oas.models.parameters.Parameter param = parameters.get(0);
        assertEquals("param1", param.getName());
        assertEquals(null, param.getSchema().getDescription());
        assertEquals(null, param.getSchema().getFormat());

        param = parameters.get(1);
        assertEquals("param2", param.getName());
        assertEquals(null, param.getSchema().getDescription());
        assertEquals(null, param.getSchema().getFormat());

        param = parameters.get(2);
        assertEquals("param3", param.getName());
        assertEquals(null, param.getSchema().getDescription());
        assertEquals("\\d+", param.getSchema().getFormat());

        param = parameters.get(3);
        assertEquals("param4", param.getName());
        assertEquals("Alpha characters", param.getSchema().getDescription());
        assertEquals("(?i)[a-z]+", param.getSchema().getFormat());

        param = parameters.get(4);
        assertEquals("param5", param.getName());
        assertEquals("Alpha characters, \"_\" and \"-\"", param.getSchema().getDescription());
        assertEquals("(?i)[-_a-z]+", param.getSchema().getFormat());

        param = parameters.get(5);
        assertEquals("param6", param.getName());
        assertEquals("Numeric characters", param.getSchema().getDescription());
        assertEquals("[0-9]+", param.getSchema().getFormat());

        param = parameters.get(6);
        assertEquals("param7", param.getName());
        assertEquals("Numeric characters, \"_\" and \"-\"", param.getSchema().getDescription());
        assertEquals("[-_0-9]+", param.getSchema().getFormat());

        param = parameters.get(7);
        assertEquals("param8", param.getName());
        assertEquals("Alphanumeric characters", param.getSchema().getDescription());
        assertEquals("(?i)[a-z0-9]+", param.getSchema().getFormat());

        param = parameters.get(8);
        assertEquals("param9", param.getName());
        assertEquals("Alphanumeric characters, \"_\" and \"-\"", param.getSchema().getDescription());
        assertEquals("(?i)[-_a-z0-9]+", param.getSchema().getFormat());
    }

    @Test
    public void dynamicParameterOverriden() {

        // @formatter:off
        getRouter().POST("/tutu/${param1:<AN+>}")
                   .specs(new @Specs(@Operation(
                        parameters = {
                              @Parameter(name = "param1",
                                         in = ParameterIn.QUERY,
                                         schema = @Schema(description = "My description",
                                                          format = "some format")),
                              @Parameter(name = "param2",
                                         in = ParameterIn.PATH,
                                         schema = @Schema(description = "My description2",
                                                          format = "some format2"))
                        })) SpecsObject() {})
                   .handle(getTestController()::sayHello);
        // @formatter:on

        OpenAPI openApi = getSpincastOpenApiManager().getOpenApi();
        PathItem pathItem =
                openApi.getPaths().get("/tutu/{param1}");
        assertNotNull(pathItem);

        io.swagger.v3.oas.models.Operation post = pathItem.getPost();
        assertNotNull(post);

        List<io.swagger.v3.oas.models.parameters.Parameter> parameters = post.getParameters();
        assertNotNull(parameters);
        assertEquals(2, parameters.size());

        io.swagger.v3.oas.models.parameters.Parameter param = parameters.get(0);
        assertEquals("param1", param.getName());
        assertEquals(ParameterIn.QUERY.toString(), param.getIn());
        assertEquals("My description", param.getSchema().getDescription());
        assertEquals("some format", param.getSchema().getFormat());

        param = parameters.get(1);
        assertEquals("param2", param.getName());
        assertEquals(ParameterIn.PATH.toString(), param.getIn());
        assertEquals("My description2", param.getSchema().getDescription());
        assertEquals("some format2", param.getSchema().getFormat());
    }

    @Test
    public void dynamicParameterNewAlias() {

        getRouter().addRouteParamPatternAlias("NOPE", "user|users|usr");

        getRouter().POST("/tutu/${param1:<NOPE>}")
                   .handle(getTestController()::sayHello);

        OpenAPI openApi = getSpincastOpenApiManager().getOpenApi();
        PathItem pathItem =
                openApi.getPaths().get("/tutu/{param1}");
        assertNotNull(pathItem);

        io.swagger.v3.oas.models.Operation post = pathItem.getPost();
        assertNotNull(post);

        List<io.swagger.v3.oas.models.parameters.Parameter> parameters = post.getParameters();
        assertNotNull(parameters);
        assertEquals(1, parameters.size());

        io.swagger.v3.oas.models.parameters.Parameter param = parameters.get(0);
        assertEquals("param1", param.getName());
        assertEquals("<NOPE>", param.getSchema().getDescription());
        assertEquals("user|users|usr", param.getSchema().getFormat());
    }

    @Test
    public void hiddenFromSpecsOnRoute() {

        getRouter().POST("/${param1:<AN>}")
                   .specsIgnore()
                   .handle(getTestController()::sayHello);

        getRouter().POST("/toto/${param1:<AN>}")
                   .handle(getTestController()::sayHello);

        OpenAPI openApi = getSpincastOpenApiManager().getOpenApi();

        assertEquals(1, openApi.getPaths().size());

        PathItem pathItem =
                openApi.getPaths().get("/{param1}");
        assertNull(pathItem);

        pathItem = openApi.getPaths().get("/toto/{param1}");
        assertNotNull(pathItem);
    }

    @Test
    public void hiddenFromSpecsUsingSpincastOpenApiManagerById() {

        getSpincastOpenApiManager().ignoreRoutesByIds("my route1 id");

        getRouter().POST("/${param1:<AN>}")
                   .id("my route1 id")
                   .handle(getTestController()::sayHello);

        getRouter().POST("/toto/${param1:<AN>}")
                   .handle(getTestController()::sayHello);

        OpenAPI openApi = getSpincastOpenApiManager().getOpenApi();

        assertEquals(1, openApi.getPaths().size());

        PathItem pathItem =
                openApi.getPaths().get("/{param1}");
        assertNull(pathItem);

        pathItem = openApi.getPaths().get("/toto/{param1}");
        assertNotNull(pathItem);
    }

    @Test
    public void hiddenFromSpecsUsingSpincastOpenApiManagerByHttpMethodAndPath() {

        getSpincastOpenApiManager().ignoreRouteUsingHttpMethodAndPath(HttpMethod.POST, "/${param1:<AN>}");

        getRouter().POST("/${param1:<AN>}")
                   .handle(getTestController()::sayHello);

        getRouter().POST("/toto/${param1:<AN>}")
                   .handle(getTestController()::sayHello);

        OpenAPI openApi = getSpincastOpenApiManager().getOpenApi();

        assertEquals(1, openApi.getPaths().size());

        PathItem pathItem =
                openApi.getPaths().get("/{param1}");
        assertNull(pathItem);

        pathItem = openApi.getPaths().get("/toto/{param1}");
        assertNotNull(pathItem);
    }

    @Test
    public void hiddenFromSpecsUsingSpincastOpenApiManagerByHttpMethodAndPath3() {

        //==========================================
        // All HTTP methods
        //==========================================
        getSpincastOpenApiManager().ignoreRouteUsingHttpMethodAndPath(null, "/${param1:<AN>}");

        getRouter().methods(HttpMethod.POST, HttpMethod.DELETE).path("/${param1:<AN>}")
                   .handle(getTestController()::sayHello);

        getRouter().POST("/toto/${param1:<AN>}")
                   .handle(getTestController()::sayHello);

        OpenAPI openApi = getSpincastOpenApiManager().getOpenApi();

        assertEquals(1, openApi.getPaths().size());

        PathItem pathItem = openApi.getPaths().get("/toto/{param1}");
        assertNotNull(pathItem);

        pathItem = openApi.getPaths().get("/{param1}");
        assertNull(pathItem);
    }

    @Test
    public void hiddenFromSpecsUsingSpincastOpenApiManagerByHttpMethodAndPath2() {

        getSpincastOpenApiManager().ignoreRouteUsingHttpMethodAndPath(HttpMethod.POST, "/${param1:<AN>}");

        getRouter().methods(HttpMethod.POST, HttpMethod.DELETE).path("/${param1:<AN>}")
                   .handle(getTestController()::sayHello);

        getRouter().POST("/toto/${param1:<AN>}")
                   .handle(getTestController()::sayHello);

        OpenAPI openApi = getSpincastOpenApiManager().getOpenApi();

        assertEquals(2, openApi.getPaths().size());

        PathItem pathItem = openApi.getPaths().get("/toto/{param1}");
        assertNotNull(pathItem);

        pathItem = openApi.getPaths().get("/{param1}");
        assertNotNull(pathItem);
        assertNull(pathItem.getPost());
        assertNotNull(pathItem.getDelete());
    }

    @Test
    public void defaultProducesAndConsumes() {

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
        assertEquals("application/default1", requestContent.keySet().iterator().next());


        ApiResponses responses = getOperation.getResponses();
        assertNotNull(responses);

        io.swagger.v3.oas.models.responses.ApiResponse defaultResponse = responses.getDefault();
        assertNotNull(defaultResponse);

        io.swagger.v3.oas.models.media.Content responseContent = defaultResponse.getContent();
        assertNotNull(responseContent);
        assertEquals(1, responseContent.size());
        assertEquals("application/default2", responseContent.keySet().iterator().next());
    }

    @Test
    public void specifiedProducesAndConsumes() {

        getRouter().GET("/tutu")
                   .specs(new @Specs(produces = @Produces({"application/111", "application/222"}),
                                     consumes = @Consumes({"application/333", "application/444"}),
                                     value = @Operation(summary = "My summary",
                                                        description = "My description",
                                                        requestBody = @RequestBody(content = {@Content()},
                                                                                   required = true)

                                     )) SpecsObject() {})
                   .handle(getTestController()::sayHello);

        String openApiAsJson = getSpincastOpenApiManager().getOpenApiAsJson(true);
        assertNotNull(openApiAsJson);

        OpenAPI openApi = getSpincastOpenApiManager().getOpenApi();
        assertNotNull(openApi);

        io.swagger.v3.oas.models.media.Content requestContent =
                openApi.getPaths().get("/tutu").getGet().getRequestBody().getContent();
        assertNotNull(requestContent);
        assertEquals(2, requestContent.size());
        assertTrue(requestContent.containsKey("application/333"));
        assertTrue(requestContent.containsKey("application/444"));

        io.swagger.v3.oas.models.media.Content responseContent =
                openApi.getPaths().get("/tutu").getGet().getResponses().getDefault().getContent();
        assertNotNull(responseContent);
        assertEquals(2, responseContent.size());
        assertTrue(responseContent.containsKey("application/111"));
        assertTrue(responseContent.containsKey("application/222"));
    }

    @Test
    public void consumesFromTheRoute() {

        getRouter().GET("/tutu")
                   .accept(ContentTypeDefaults.BINARY)
                   .specs(new @Specs(@Operation(summary = "My summary",
                                                description = "My description",
                                                requestBody = @RequestBody(content = {@Content()},
                                                                           required = true)

                   )) SpecsObject() {})
                   .handle(getTestController()::sayHello);

        String openApiAsJson = getSpincastOpenApiManager().getOpenApiAsJson(true);
        assertNotNull(openApiAsJson);

        OpenAPI openApi = getSpincastOpenApiManager().getOpenApi();
        assertNotNull(openApi);

        io.swagger.v3.oas.models.media.Content requestContent =
                openApi.getPaths().get("/tutu").getGet().getRequestBody().getContent();
        assertNotNull(requestContent);
        assertEquals(1, requestContent.size());
        assertTrue(requestContent.containsKey("application/octet-stream"));

        io.swagger.v3.oas.models.media.Content responseContent =
                openApi.getPaths().get("/tutu").getGet().getResponses().getDefault().getContent();
        assertNotNull(responseContent);
        assertEquals(1, responseContent.size());
        assertTrue(responseContent.containsKey("application/default2"));
    }

    @Test
    public void consumesFromTheRouteOverriden() {

        getRouter().GET("/tutu")
                   .accept(ContentTypeDefaults.BINARY)
                   .specs(new @Specs(consumes = @Consumes({"application/333", "application/444"}),
                                     value = @Operation(summary = "My summary",
                                                        description = "My description",
                                                        requestBody = @RequestBody(content = {@Content()},
                                                                                   required = true)

                                     )) SpecsObject() {})
                   .handle(getTestController()::sayHello);

        String openApiAsJson = getSpincastOpenApiManager().getOpenApiAsJson(true);
        assertNotNull(openApiAsJson);

        OpenAPI openApi = getSpincastOpenApiManager().getOpenApi();
        assertNotNull(openApi);

        io.swagger.v3.oas.models.media.Content requestContent =
                openApi.getPaths().get("/tutu").getGet().getRequestBody().getContent();
        assertNotNull(requestContent);
        assertEquals(2, requestContent.size());
        assertTrue(requestContent.containsKey("application/333"));
        assertTrue(requestContent.containsKey("application/444"));
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

        io.swagger.v3.oas.models.Operation get = openApi.getPaths().get("/tutu").getGet();
        assertNotNull(get);

        io.swagger.v3.oas.models.Operation delete = openApi.getPaths().get("/titi").getDelete();
        assertNotNull(delete);
        assertEquals("titi description", delete.getDescription());
    }

    @Test
    public void getOpenApi() {

        getRouter().GET("/tutu")
                   .specs(
                          new @Specs(@Operation(operationId = "myOperationId",
                                                summary = "my summary",
                                                description = "my description",
                                                responses = @ApiResponse(
                                                                         responseCode = "200",
                                                                         description = "voila!"))) SpecsObject() {})
                   .handle(getTestController()::sayHello);

        OpenAPI openAPI = getSpincastOpenApiManager().getOpenApi();
        assertNotNull(openAPI);
    }

    @Test
    public void getOpenApiAsJson() {

        getRouter().methods(HttpMethod.POST, HttpMethod.PUT).path("/toto/titi")
                   .specs(
                          new @Specs(@Operation(operationId = "myOperationId111",
                                                summary = "my summary222",
                                                description = "my description333",
                                                responses = @ApiResponse(
                                                                         responseCode = "400",
                                                                         description = "jujujujuj"))) SpecsObject() {})
                   .handle(getTestController()::sayHello);

        getRouter().GET("/coco").handle(getTestController()::sayHello);

        String openApiAsJson = getSpincastOpenApiManager().getOpenApiAsJson(true);
        assertNotNull(openApiAsJson);

        JsonObject obj = getJsonManager().fromString(openApiAsJson);
        assertNotNull(obj);
    }

    @Test
    public void getOpenApiAsYaml() {
        getRouter().methods(HttpMethod.POST, HttpMethod.PUT).path("/toto/titi")
                   .specs(
                          new @Specs(@Operation(operationId = "myOperationId111",
                                                summary = "my summary222",
                                                description = "my description333",
                                                responses = @ApiResponse(
                                                                         responseCode = "400",
                                                                         description = "jujujujuj"))) SpecsObject() {})
                   .handle(getTestController()::sayHello);

        getRouter().GET("/coco").handle(getTestController()::sayHello);

        String openApiAsYaml = getSpincastOpenApiManager().getOpenApiAsYaml();
        assertNotNull(openApiAsYaml);

        Yaml yaml = new Yaml();
        Object obj = yaml.load(openApiAsYaml);
        assertNotNull(obj);
    }

    @Test
    public void specsAsYamlString() {

        getRouter().GET("/tutu")
                   .specs("get:\n" +
                          "  operationId: myOperationId\n" +
                          "  responses:\n" +
                          "    default:\n" +
                          "      description: default response\n" +
                          "      content:\n" +
                          "        application/xml: {}\n")
                   .handle(getTestController()::sayHello);

        OpenAPI openAPI = getSpincastOpenApiManager().getOpenApi();
        assertNotNull(openAPI);

        io.swagger.v3.oas.models.Operation get = openAPI.getPaths().get("/tutu").getGet();
        assertNotNull(get);

        assertEquals("application/xml",
                     get.getResponses().values().iterator().next().getContent().keySet().iterator().next());
    }

    @Test
    public void specsAsYamlStringMoreThanOneHttpMethod() {

        getRouter().GET("/tutu")
                   .specs("get:\n" +
                          "  operationId: myOperationId\n" +
                          "  responses:\n" +
                          "    default:\n" +
                          "      description: default response\n" +
                          "      content:\n" +
                          "        application/xml: {}\n" +
                          "post:\n" +
                          "  operationId: myOperationId2\n" +
                          "  responses:\n" +
                          "    default:\n" +
                          "      description: default response\n" +
                          "      content:\n" +
                          "        application/json: {}\n")
                   .handle(getTestController()::sayHello);

        OpenAPI openAPI = getSpincastOpenApiManager().getOpenApi();
        assertNotNull(openAPI);

        io.swagger.v3.oas.models.Operation get = openAPI.getPaths().get("/tutu").getGet();
        assertNotNull(get);

        assertEquals("application/xml",
                     get.getResponses().values().iterator().next().getContent().keySet().iterator().next());

        io.swagger.v3.oas.models.Operation post = openAPI.getPaths().get("/tutu").getPost();
        assertNotNull(post);

        assertEquals("application/json",
                     post.getResponses().values().iterator().next().getContent().keySet().iterator().next());
    }

    @Test
    public void specsAsYamlStringWrongIndentation() {

        getRouter().GET("/tutu")
                   .specs("get:\n" +
                          "operationId: myOperationId\n" +
                          "  responses:\n" +
                          "    default:\n" +
                          "      description: default response\n" +
                          "      content:\n" +
                          "        application/xml: {}\n")
                   .handle(getTestController()::sayHello);

        try {
            getSpincastOpenApiManager().getOpenApi();
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void specsAsYamlStringError() {

        getRouter().GET("/tutu")
                   .specs("get\n" + // ":" missing!
                          "  operationId: myOperationId\n" +
                          "  responses:\n" +
                          "    default:\n" +
                          "      description: default response\n" +
                          "      content:\n" +
                          "        application/xml: {}\n")
                   .handle(getTestController()::sayHello);

        try {
            getSpincastOpenApiManager().getOpenApi();
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void specsAsYamlStringButHidden() {

        getRouter().GET("/tutu")
                   .specsIgnore()
                   .specs("get:\n" +
                          "  operationId: myOperationId\n" +
                          "  responses:\n" +
                          "    default:\n" +
                          "      description: default response\n" +
                          "      content:\n" +
                          "        application/xml: {}\n")
                   .handle(getTestController()::sayHello);

        getRouter().GET("/other").handle(getTestController()::sayHello);

        OpenAPI openAPI = getSpincastOpenApiManager().getOpenApi();
        assertNotNull(openAPI);

        PathItem pathItem = openAPI.getPaths().get("/tutu");
        assertNull(pathItem);

        pathItem = openAPI.getPaths().get("/other");
        assertNotNull(pathItem);
    }

    @Test
    public void specsAsYamlStringButHiddenById() {

        getSpincastOpenApiManager().ignoreRoutesByIds("my route1 id");

        getRouter().GET("/tutu")
                   .id("my route1 id")
                   .specs("get:\n" +
                          "  operationId: myOperationId\n" +
                          "  responses:\n" +
                          "    default:\n" +
                          "      description: default response\n" +
                          "      content:\n" +
                          "        application/xml: {}\n" +
                          "post:\n" +
                          "  operationId: myOperationId2\n" +
                          "  responses:\n" +
                          "    default:\n" +
                          "      description: default response\n" +
                          "      content:\n" +
                          "        application/xml: {}\n")
                   .handle(getTestController()::sayHello);

        getRouter().GET("/other").handle(getTestController()::sayHello);

        OpenAPI openAPI = getSpincastOpenApiManager().getOpenApi();
        assertNotNull(openAPI);

        PathItem pathItem = openAPI.getPaths().get("/tutu");
        assertNull(pathItem);

        pathItem = openAPI.getPaths().get("/other");
        assertNotNull(pathItem);
    }

    @Test
    public void specsAsYamlStringButHiddenByHttpMethodAndPath() {

        getSpincastOpenApiManager().ignoreRouteUsingHttpMethodAndPath(HttpMethod.GET, "/tutu");

        getRouter().GET("/tutu")
                   .id("my route1 id")
                   .specs("get:\n" +
                          "  operationId: myOperationId\n" +
                          "  responses:\n" +
                          "    default:\n" +
                          "      description: default response\n" +
                          "      content:\n" +
                          "        application/xml: {}\n" +
                          "post:\n" +
                          "  operationId: myOperationId2\n" +
                          "  responses:\n" +
                          "    default:\n" +
                          "      description: default response\n" +
                          "      content:\n" +
                          "        application/xml: {}\n")
                   .handle(getTestController()::sayHello);

        getRouter().GET("/other").handle(getTestController()::sayHello);

        OpenAPI openAPI = getSpincastOpenApiManager().getOpenApi();
        assertNotNull(openAPI);

        io.swagger.v3.oas.models.Operation get = openAPI.getPaths().get("/tutu").getGet();
        assertNull(get);

        io.swagger.v3.oas.models.Operation post = openAPI.getPaths().get("/tutu").getPost();
        assertNotNull(post);

        PathItem pathItem = openAPI.getPaths().get("/other");
        assertNotNull(pathItem);
    }

    @Test
    public void websocketRouteIgnored() {

        getRouter().websocket("/titi").handle(new TestWebsocketController<DefaultRequestContext, DefaultWebsocketContext>());
        getRouter().POST("/tutu").handle(getTestController()::sayHello);

        OpenAPI openApi = getSpincastOpenApiManager().getOpenApi();
        io.swagger.v3.oas.models.Operation post = openApi.getPaths().get("/tutu").getPost();
        assertNotNull(post);

        PathItem pathItem = openApi.getPaths().get("/titi");
        assertNull(pathItem);
    }

    @Test
    public void unknownAnnotation() {

        getRouter().GET("/tutu")
                   .specs(new @Nope("nope") SpecsObject() {})
                   .handle(getTestController()::sayHello);

        OpenAPI openApi = getSpincastOpenApiManager().getOpenApi();
        assertNotNull(openApi);
        assertNull(openApi.getPaths());
    }

    @Test
    public void invalidSpecsObject() {

        getRouter().GET("/tutu")
                   .specs(new Date())
                   .handle(getTestController()::sayHello);

        OpenAPI openApi = getSpincastOpenApiManager().getOpenApi();
        assertNotNull(openApi);
        assertNull(openApi.getPaths());
    }

    @Test
    public void allHttpMethods() {

        getRouter().ALL("/tutu/").handle(getTestController()::sayHello);

        OpenAPI openApi = getSpincastOpenApiManager().getOpenApi();
        PathItem pathItem = openApi.getPaths().get("/tutu");
        assertNotNull(pathItem);

        assertNotNull(pathItem.getPost());
        assertNotNull(pathItem.getGet());
        assertNotNull(pathItem.getDelete());
        assertNotNull(pathItem.getPut());
        assertNotNull(pathItem.getPatch());
        assertNotNull(pathItem.getHead());
        assertNotNull(pathItem.getOptions());

        // Unsupported (no JAX-RS annotation for TRACE)
        assertNull(pathItem.getTrace());
    }

    @Test
    public void ignoringByIds() {

        getRouter().POST("/titi").id("titi").handle(getTestController()::sayHello);
        getRouter().POST("/tutu").id("tutu").handle(getTestController()::sayHello);
        getRouter().POST("/toto").id("toto").handle(getTestController()::sayHello);

        getSpincastOpenApiManager().ignoreRoutesByIds("titi", "toto");

        OpenAPI openApi = getSpincastOpenApiManager().getOpenApi();
        PathItem pathItem = openApi.getPaths().get("/tutu");
        assertNotNull(pathItem);

        pathItem = openApi.getPaths().get("/titi");
        assertNull(pathItem);

        pathItem = openApi.getPaths().get("/toto");
        assertNull(pathItem);
    }

    @Test
    public void ignoringByHTTPMethodAndPath() {

        getRouter().POST("/titi").handle(getTestController()::sayHello);
        getRouter().POST("/tutu").handle(getTestController()::sayHello);
        getRouter().POST("/toto").handle(getTestController()::sayHello);

        getSpincastOpenApiManager().ignoreRouteUsingHttpMethodAndPath(HttpMethod.POST, "/titi");

        // invalid method
        getSpincastOpenApiManager().ignoreRouteUsingHttpMethodAndPath(HttpMethod.GET, "/tutu");

        OpenAPI openApi = getSpincastOpenApiManager().getOpenApi();
        PathItem pathItem = openApi.getPaths().get("/tutu");
        assertNotNull(pathItem);

        pathItem = openApi.getPaths().get("/toto");
        assertNotNull(pathItem);
    }

    @Test
    public void ignoringByAllHTTPMethod() {

        getRouter().GET("/titi").handle(getTestController()::sayHello);
        getRouter().POST("/titi").handle(getTestController()::sayHello);
        getRouter().POST("/tutu").handle(getTestController()::sayHello);
        getRouter().POST("/toto").handle(getTestController()::sayHello);

        getSpincastOpenApiManager().ignoreRouteUsingHttpMethodAndPath(null, "/titi");

        OpenAPI openApi = getSpincastOpenApiManager().getOpenApi();
        PathItem pathItem = openApi.getPaths().get("/tutu");
        assertNotNull(pathItem);

        pathItem = openApi.getPaths().get("/toto");
        assertNotNull(pathItem);
    }


}
