package org.spincast.plugins.openapi.bottomup;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.json.JsonManager;
import org.spincast.core.routing.DefaultRouteParamAliasesBinder;
import org.spincast.core.routing.HttpMethod;
import org.spincast.core.routing.Route;
import org.spincast.core.routing.Router;
import org.spincast.core.routing.RoutingType;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.websocket.WebsocketContext;
import org.spincast.plugins.openapi.bottomup.config.SpincastOpenApiBottomUpPluginConfig;
import org.spincast.plugins.openapi.bottomup.utils.SwaggerAnnotationsCreator;
import org.spincast.shaded.org.apache.commons.codec.digest.DigestUtils;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Sets;
import com.google.inject.Inject;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.jaxrs2.integration.JaxrsOpenApiContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.integration.api.OpenAPIConfiguration;
import io.swagger.v3.oas.integration.api.OpenApiScanner;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.implementation.StubMethod;

public class SpincastOpenApiManagerDefault<R extends RequestContext<?>, W extends WebsocketContext<?>>
                                          implements SpincastOpenApiManager {

    protected final static Logger logger = LoggerFactory.getLogger(SpincastOpenApiManagerDefault.class);

    private final Router<R, W> router;
    private final JsonManager jsonManager;
    private final SpincastOpenApiBottomUpPluginConfig spincastOpenApiBottomUpPluginConfig;
    private final SwaggerAnnotationsCreator annotationsCreator;
    private final DefaultRouteParamAliasesBinder<R, W> defaultRouteParamAliasesBinder;
    private OpenAPI baseOpenApiInfo;
    private OpenAPI openApi;
    private String openApiAsJson;
    private String openApiAsJsonPretty;
    private String openApiAsYaml;
    private String openApiAsYamlPretty;
    private Set<String> routeIdsToHide;
    private Set<String> routeHttpMethodAndPathToHide;

    @Inject
    public SpincastOpenApiManagerDefault(Router<R, W> router,
                                         JsonManager jsonManager,
                                         SpincastOpenApiBottomUpPluginConfig spincastOpenApiBottomUpPluginConfig,
                                         SwaggerAnnotationsCreator annotationsCreator,
                                         DefaultRouteParamAliasesBinder<R, W> defaultRouteParamAliasesBinder) {
        this.router = router;
        this.jsonManager = jsonManager;
        this.spincastOpenApiBottomUpPluginConfig = spincastOpenApiBottomUpPluginConfig;
        this.annotationsCreator = annotationsCreator;
        this.defaultRouteParamAliasesBinder = defaultRouteParamAliasesBinder;
    }

    protected Router<R, W> getRouter() {
        return this.router;
    }

    protected JsonManager getJsonManager() {
        return this.jsonManager;
    }

    protected SpincastOpenApiBottomUpPluginConfig getSpincastOpenApiBottomUpPluginConfig() {
        return this.spincastOpenApiBottomUpPluginConfig;
    }

    protected SwaggerAnnotationsCreator getAnnotationsCreator() {
        return this.annotationsCreator;
    }

    protected DefaultRouteParamAliasesBinder<R, W> getDefaultRouteParamAliasesBinder() {
        return this.defaultRouteParamAliasesBinder;
    }

    public Set<String> getRouteIdsToHide() {
        if (this.routeIdsToHide == null) {
            this.routeIdsToHide = new HashSet<String>();
        }
        return this.routeIdsToHide;
    }

    public Set<String> getRouteHttpMethodAndPathToHide() {
        if (this.routeHttpMethodAndPathToHide == null) {
            this.routeHttpMethodAndPathToHide = new HashSet<String>();
        }
        return this.routeHttpMethodAndPathToHide;
    }

    /**
     * Delete cache so the Open API object
     * is computed from scratch.
     */
    @Override
    public void clearCache() {
        this.openApi = null;
        this.openApiAsJson = null;
        this.openApiAsJsonPretty = null;
        this.openApiAsYaml = null;
        this.openApiAsYamlPretty = null;
    }

    @Override
    public void resetAll() {
        this.baseOpenApiInfo = new OpenAPI();
        clearCache();
        getRouteIdsToHide().clear();
        getRouteHttpMethodAndPathToHide().clear();
    }

    @Override
    public void setOpenApiBase(OpenAPI baseOpenApiInfo) {
        this.baseOpenApiInfo = baseOpenApiInfo;
    }

    public OpenAPI getBaseOpenApiInfo() {
        return this.baseOpenApiInfo;
    }

    /**
     * @param force If <code>true</code>, cache
     * will be bypassed on updated.
     */
    @Override
    @Parameters
    public OpenAPI getOpenApi() {

        if (this.openApi == null) {
            try {

                OpenAPI baseInfos = getBaseOpenApiInfo();
                if (baseInfos == null) {
                    baseInfos = new OpenAPI();
                }

                //==========================================
                // Create JAX-RS like classes with the proper annotations.
                //==========================================
                Set<Class<?>> classes = generateJaxRsLikeClasses();
                OpenApiScanner scanner = createScanner(classes);

                //==========================================
                // Add the Paths specified as YAML Strings
                // on the routes.
                //==========================================
                addYamlStringSpecifiedPaths(baseInfos);

                SwaggerConfiguration swaggerConfiguration = new SwaggerConfiguration().openAPI(baseInfos);

                JaxrsOpenApiContext<JaxrsOpenApiContext<?>> context = new JaxrsOpenApiContext<JaxrsOpenApiContext<?>>();
                context.setOpenApiConfiguration(swaggerConfiguration);
                context.setOpenApiScanner(scanner);
                context.init();

                this.openApi = context.read();

            } catch (Exception ex) {
                throw SpincastStatics.runtimize(ex);
            }
        }

        return this.openApi;
    }

    protected OpenApiScanner createScanner(Set<Class<?>> classes) {

        OpenApiScanner scanner = new OpenApiScanner() {

            @Override
            public void setConfiguration(OpenAPIConfiguration openApiConfiguration) {
                // nothing required
            }

            @Override
            public Map<String, Object> resources() {
                return new HashMap<String, Object>();
            }

            @Override
            public Set<Class<?>> classes() {
                return classes;
            }
        };
        return scanner;
    }

    /**
     * This method will dynamically create and annotate
     * classes to simulate a JAX-RS application. This way,
     * we can reuse the logic from swagger-jaxrs2 to generate
     * the {@link OpenAPI} object.
     */
    protected Set<Class<?>> generateJaxRsLikeClasses() {

        //==========================================
        // Is the automatic specs generation disabled?
        //==========================================
        if (getSpincastOpenApiBottomUpPluginConfig().isDisableAutoSpecs()) {
            logger.info("Auto specs generation disabled from the configs.");
            return new HashSet<Class<?>>();
        }

        try {

            List<Route<R>> mainRoutes = getRouter().getMainRoutes();
            if (mainRoutes == null || mainRoutes.size() == 0) {
                return Sets.newHashSet();
            }

            Builder<Object> classBuilder =
                    new ByteBuddy().subclass(Object.class)
                                   .annotateType(AnnotationDescription.Builder.ofType(Path.class)
                                                                              .define("value", "")
                                                                              .build());

            Set<String> operationIdsUsed = new HashSet<String>();

            //==========================================
            // For each main route...
            //==========================================
            for (Route<R> route : mainRoutes) {

                if (route.getRoutingTypes() != null && !route.getRoutingTypes().contains(RoutingType.FOUND)) {
                    logger.info("Route without routing type RoutingType.FOUND. Ignoring: " + route);
                    continue;
                }

                //==========================================
                // Resources route
                //==========================================
                if (route.isStaticResourceRoute()) {
                    logger.info("Static resources route. Ignoring: " + route);
                    continue;
                }

                //==========================================
                // Websocket routes are not supported for now.
                // @see https://github.com/OAI/OpenAPI-Specification/issues/523
                //==========================================
                if (route.isWebsocketRoute()) {
                    logger.info("Websocket routes not supported. Ignoring: " + route);
                    continue;
                }

                //==========================================
                // Hidden route?
                // From inline annotation or from id.
                //==========================================
                if (route.isSpecsIgnore() || isToHideFromId(route)) {
                    logger.info("Route hidden from specs, won't be added:" + route);
                    continue;
                }

                //==========================================
                // For each HTTP method...
                //==========================================
                if (route.getHttpMethods() == null || route.getHttpMethods().size() == 0) {
                    throw new RuntimeException("Not supposed. At least one HTTP method required!");
                }

                for (HttpMethod httpMethod : route.getHttpMethods()) {

                    //==========================================
                    // Is supported HTTP method?
                    //==========================================
                    if (!isSupportedHttpMethod(httpMethod)) {
                        logger.warn("Unmanaged HTTP method \"" + httpMethod + "\" will be ignored for route : " + route);
                        continue;
                    }

                    //==========================================
                    // Hidden route from HTTP method and path.
                    //==========================================
                    if (isToHideFromHttpMethodAndPath(route, httpMethod)) {
                        logger.info("Route hidden from specs, won't be added: [" + httpMethod.name() + "] " +
                                    route);
                        continue;
                    }

                    //==========================================
                    // Duplicate route
                    //==========================================
                    String operationId = createOperationId(route, httpMethod);
                    if (operationIdsUsed.contains(operationId)) {
                        logger.warn("Duplicate route, ignoring: " + route);
                        continue;
                    }
                    operationIdsUsed.add(operationId);

                    Object specs = route.getSpecs();

                    //==========================================
                    // Specs specified as a YAML string. It will
                    // be processed elsewhere.
                    //==========================================
                    if (specs != null && specs instanceof String) {
                        continue;
                    }

                    List<Annotation> handlerMethodAnnotations = new ArrayList<Annotation>();
                    Specs specsAnnotation = getSpecsAnnotation(route.getSpecs());

                    if (specs != null && specsAnnotation == null) {
                        logger.warn("The " + SpincastOpenApiBottomUpPlugin.class.getSimpleName() + " plugin currently only " +
                                    "understands a YAML string or an anonymous class annotated with @Specs " +
                                    "as the first parameter to the '.specs(...)' method when building a route." +
                                    "Neither was found on this route so we ignore it: " + route);
                        continue;
                    }

                    //==========================================
                    // @GET, @POST,... annotation
                    //==========================================
                    addHttpMethodAnnotation(handlerMethodAnnotations, httpMethod);

                    //==========================================
                    // @Path annotation
                    //==========================================
                    addPathAnnotationToHandlerMethod(handlerMethodAnnotations, route.getPath());

                    //==========================================
                    // @Consumes annotation
                    //==========================================
                    addConsumesAnnotationToHandlerMethod(handlerMethodAnnotations, specsAnnotation, route);

                    //==========================================
                    // @Produces annotation
                    //==========================================
                    addProducesAnnotationToHandlerMethod(handlerMethodAnnotations, specsAnnotation);

                    //==========================================
                    // @Operation annotation
                    //==========================================
                    addOperationAnnotationToHandlerMethod(handlerMethodAnnotations, specsAnnotation);

                    //==========================================
                    // @Parameters
                    //==========================================
                    addParametersAnnotationToHandlerMethod(handlerMethodAnnotations,
                                                           route.getPath(),
                                                           specsAnnotation);

                    classBuilder = classBuilder.defineMethod(operationId, void.class, Visibility.PUBLIC)
                                               .intercept(StubMethod.INSTANCE)
                                               .annotateMethod(handlerMethodAnnotations);
                }
            }

            Class<? extends Object> clazz = classBuilder.make()
                                                        .load(getClass().getClassLoader())
                                                        .getLoaded();

            return Sets.newHashSet(clazz);

        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected boolean isSupportedHttpMethod(HttpMethod httpMethod) {
        return httpMethod == HttpMethod.GET ||
               httpMethod == HttpMethod.POST ||
               httpMethod == HttpMethod.PUT ||
               httpMethod == HttpMethod.PATCH ||
               httpMethod == HttpMethod.DELETE ||
               httpMethod == HttpMethod.HEAD ||
               httpMethod == HttpMethod.OPTIONS;
    }

    protected Paths getPathsFromYamlString(String routePath, String specs) {

        String specsStr = getOpenApiPrefix() + "\n" +
                          "paths:\n" +
                          "  " + routePath + ":\n";

        String[] lines = ((String)specs).split("\\r?\\n");
        for (int i = 0; i < lines.length; i++) {
            specsStr += "    " + lines[i] + "\n";
        }

        SwaggerParseResult readContents = new OpenAPIV3Parser().readContents(specsStr);
        OpenAPI openAPI = readContents.getOpenAPI();
        if (openAPI == null) {
            throw new RuntimeException("Unable to parse the YAML specs on route with path \"" + routePath + "\":\n" +
                                       Arrays.toString(readContents.getMessages().toArray()));
        }

        Paths paths = openAPI.getPaths();

        return paths;
    }

    protected String getOpenApiPrefix() {
        return "openapi: 3.0.1";
    }

    protected boolean isToHideFromId(Route<R> route) {
        return route.getId() != null && getRouteIdsToHide().contains(route.getId());
    }

    protected boolean isToHideFromHttpMethodAndPath(Route<R> route, HttpMethod httpMethod) {

        String key = createHttpMethodAndPathKey(httpMethod, route.getPath());
        String keyAnyMethod = createHttpMethodAndPathKey(null, route.getPath());

        return getRouteHttpMethodAndPathToHide().contains(key) || getRouteHttpMethodAndPathToHide().contains(keyAnyMethod);
    }

    protected String createOperationId(Route<R> route, HttpMethod httpMethod) {
        String operationId = route.getId();
        if (StringUtils.isBlank(operationId)) {
            operationId = httpMethod + route.getPath();
        }

        //==========================================
        // Will be used as a method name, can't start with a number.
        //==========================================
        operationId = "r" + DigestUtils.md5Hex(operationId);
        return operationId;
    }

    protected void addHttpMethodAnnotation(List<Annotation> handlerMethodAnnotations, HttpMethod httpMethod) {
        Annotation annotation = getHttpMethodAnnotation(httpMethod);
        handlerMethodAnnotations.add(annotation);
    }

    protected Annotation getHttpMethodAnnotation(HttpMethod httpMethod) {
        if (httpMethod == HttpMethod.GET) {
            return new GET() {

                @Override
                public Class<? extends Annotation> annotationType() {
                    return GET.class;
                }
            };
        } else if (httpMethod == HttpMethod.DELETE) {
            return new DELETE() {

                @Override
                public Class<? extends Annotation> annotationType() {
                    return DELETE.class;
                }
            };
        } else if (httpMethod == HttpMethod.HEAD) {
            return new HEAD() {

                @Override
                public Class<? extends Annotation> annotationType() {
                    return HEAD.class;
                }
            };
        } else if (httpMethod == HttpMethod.OPTIONS) {
            return new OPTIONS() {

                @Override
                public Class<? extends Annotation> annotationType() {
                    return OPTIONS.class;
                }
            };
        } else if (httpMethod == HttpMethod.PATCH) {
            return new PATCH() {

                @Override
                public Class<? extends Annotation> annotationType() {
                    return PATCH.class;
                }
            };
        } else if (httpMethod == HttpMethod.POST) {
            return new POST() {

                @Override
                public Class<? extends Annotation> annotationType() {
                    return POST.class;
                }
            };
        } else if (httpMethod == HttpMethod.PUT) {
            return new PUT() {

                @Override
                public Class<? extends Annotation> annotationType() {
                    return PUT.class;
                }
            };
        } else {
            throw new RuntimeException("HTTP method not managed: " + httpMethod);
        }
    }

    protected void addPathAnnotationToHandlerMethod(List<Annotation> handlerMethodAannotations,
                                                    String routePath) {
        handlerMethodAannotations.add(new Path() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return Path.class;
            }

            @Override
            public String value() {
                String path = convertSpincastRoutePathToOpenApiFormat(routePath);
                return path;
            }
        });
    }

    /**
     * Convert dynamic path parameters to Open API format.
     * <p>
     * /${param1}/${param3:\\d+}/${param4:&lt;A&gt;}
     * <p>
     * =>
     * <p>
     * /{param1}/{param2}/{param3}
     */
    protected String convertSpincastRoutePathToOpenApiFormat(String spincastRoutePath) {
        String path = spincastRoutePath.replaceAll("/(\\$\\{([^}:]+)(:([^}]+))?\\})", "/{$2}");
        return path;
    }

    protected Specs getSpecsAnnotation(Object specsObj) {
        try {
            if (specsObj == null) {
                return null;
            }

            Class<?> c = specsObj.getClass();

            AnnotatedType[] annotatedInterfaces = c.getAnnotatedInterfaces();
            Specs specsAnnotation = null;
            if (annotatedInterfaces != null && annotatedInterfaces.length > 0) {
                specsAnnotation = annotatedInterfaces[0].getAnnotation(Specs.class);
            } else {
                return null;
            }

            return specsAnnotation;
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected void addOperationAnnotationToHandlerMethod(List<Annotation> handlerMethodAnnotations,
                                                         Specs specs) {
        if (specs == null) {
            return;
        }
        Operation operationAnnotation = specs.value();
        if (operationAnnotation != null) {
            handlerMethodAnnotations.add(operationAnnotation);
        }
    }

    protected void addParametersAnnotationToHandlerMethod(List<Annotation> handlerMethodAnnotations,
                                                          String routePath,
                                                          Specs specs) {

        Operation operationAnnotation = specs != null ? specs.value() : null;

        Pattern pattern = Pattern.compile("/(\\$\\{([^}:]+)(:([^}]+))?\\})");
        Matcher matcher = pattern.matcher(routePath);

        List<Parameter> parameterAnnotationsList = new ArrayList<Parameter>();
        while (matcher.find()) {
            String paramName = matcher.group(2);
            String paramPattern = matcher.group(4);

            //==========================================
            // We add a @Parameter annotation
            // only if that parameter if not defined explicitly
            // on the @Operation annotation.
            //==========================================
            if (!isOperationAnnotationContainsParameter(operationAnnotation, paramName)) {

                String paramDescription = "";
                if (paramPattern != null && paramPattern.startsWith("<") && paramPattern.endsWith(">")) {
                    String alias = paramPattern.substring(1, paramPattern.length() - 1);
                    paramDescription = createParamDescriptionFromAlias(alias);
                    if (paramDescription == null) {
                        paramDescription = paramPattern;
                    }
                    paramPattern = createParamPatternFromAlias(alias);
                }

                Parameter parameterAnnotation =
                        getAnnotationsCreator().createPathParameterAnnotation(paramName, paramDescription, paramPattern);
                parameterAnnotationsList.add(parameterAnnotation);
            }
        }

        Parameters parameters = getAnnotationsCreator().createParametersAnnotation(parameterAnnotationsList);
        handlerMethodAnnotations.add(parameters);
    }

    protected String createParamDescriptionFromAlias(String alias) {

        if (getDefaultRouteParamAliasesBinder().getAlphaAliasKey().equals(alias)) {
            return "Alpha characters";
        }

        if (getDefaultRouteParamAliasesBinder().geNumericAliasKey().equals(alias)) {
            return "Numeric characters";
        }

        if (getDefaultRouteParamAliasesBinder().getAlphaPlusAliasKey().equals(alias)) {
            return "Alpha characters, \"_\" and \"-\"";
        }

        if (getDefaultRouteParamAliasesBinder().geNumericPlusAliasKey().equals(alias)) {
            return "Numeric characters, \"_\" and \"-\"";
        }

        if (getDefaultRouteParamAliasesBinder().getAlphaNumericAliasKey().equals(alias)) {
            return "Alphanumeric characters";
        }

        if (getDefaultRouteParamAliasesBinder().getAlphaNumericPlusAliasKey().equals(alias)) {
            return "Alphanumeric characters, \"_\" and \"-\"";
        }

        return null;
    }

    protected String createParamPatternFromAlias(String alias) {
        Map<String, String> routeParamPatternAliases = getRouter().getRouteParamPatternAliases();
        String pattern = routeParamPatternAliases.get(alias);
        return pattern;
    }

    protected boolean isOperationAnnotationContainsParameter(Operation operationAnnotation, String paramName) {

        if (operationAnnotation == null || paramName == null || operationAnnotation.parameters() == null) {
            return false;
        }

        for (Parameter parameter : operationAnnotation.parameters()) {
            if (parameter != null && paramName.equals(parameter.name())) {
                return true;
            }
        }

        return false;
    }


    protected void addConsumesAnnotationToHandlerMethod(List<Annotation> handlerMethodAnnotations,
                                                        Specs specs,
                                                        Route<R> route) {
        Consumes consumesAnnotation = null;
        if (specs != null && specs.consumes() != null && specs.consumes().value() != null &&
            specs.consumes().value().length > 0 && !("".equals(specs.consumes().value()[0]))) {
            consumesAnnotation = specs.consumes();
        } else {

            //==========================================
            // We use the "accept" content types of the
            // route, if there are some.
            //==========================================
            String[] acceptedContentTypes = null;
            Set<String> acceptedContentTypesFromRoute = route.getAcceptedContentTypes();
            if (acceptedContentTypesFromRoute != null && acceptedContentTypesFromRoute.size() > 0) {
                acceptedContentTypes = acceptedContentTypesFromRoute.toArray(new String[acceptedContentTypesFromRoute.size()]);
            } else {
                acceptedContentTypes = getSpincastOpenApiBottomUpPluginConfig().getDefaultConsumesContentTypes();
            }

            final String[] acceptedContentTypesFinal = acceptedContentTypes;
            if (acceptedContentTypes != null && acceptedContentTypes.length > 0) {

                consumesAnnotation = new Consumes() {

                    @Override
                    public Class<? extends Annotation> annotationType() {
                        return Consumes.class;
                    }

                    @Override
                    public String[] value() {
                        return acceptedContentTypesFinal;
                    }
                };
            }
        }

        if (consumesAnnotation != null) {
            handlerMethodAnnotations.add(consumesAnnotation);
        }
    }

    protected void addProducesAnnotationToHandlerMethod(List<Annotation> handlerMethodAnnotations,
                                                        Specs specs) {
        Produces producesAnnotation = null;
        if (specs != null && specs.produces() != null && specs.produces().value() != null &&
            specs.produces().value().length > 0 && !("".equals(specs.produces().value()[0]))) {
            producesAnnotation = specs.produces();
        } else {

            String[] defaultProducesContentTypes = getSpincastOpenApiBottomUpPluginConfig().getDefaultProducesContentTypes();
            if (defaultProducesContentTypes != null && defaultProducesContentTypes.length > 0) {
                producesAnnotation = new Produces() {

                    @Override
                    public Class<? extends Annotation> annotationType() {
                        return Produces.class;
                    }

                    @Override
                    public String[] value() {
                        return defaultProducesContentTypes;
                    }
                };
            }
        }

        if (producesAnnotation != null) {
            handlerMethodAnnotations.add(producesAnnotation);
        }
    }

    @Override
    public String getOpenApiAsJson() {
        return getOpenApiAsJson(true);
    }

    @Override
    public String getOpenApiAsJson(boolean prettyFormatted) {

        String val = prettyFormatted ? this.openApiAsJsonPretty : this.openApiAsJson;
        if (val == null) {

            try {
                OpenAPI openAPI = getOpenApi();

                if (prettyFormatted) {
                    this.openApiAsJsonPretty = Json.pretty(openAPI);
                } else {
                    this.openApiAsJson = Json.mapper()
                                             .writeValueAsString(openAPI);
                }

            } catch (Exception ex) {
                throw SpincastStatics.runtimize(ex);
            }
        }

        return prettyFormatted ? this.openApiAsJsonPretty : this.openApiAsJson;

    }

    @Override
    public String getOpenApiAsYaml() {
        return getOpenApiAsYaml(true);
    }

    @Override
    public String getOpenApiAsYaml(boolean prettyFormatted) {
        String val = prettyFormatted ? this.openApiAsYamlPretty : this.openApiAsYaml;
        if (val == null) {

            try {
                OpenAPI openAPI = getOpenApi();

                if (prettyFormatted) {
                    this.openApiAsYamlPretty = Yaml.pretty(openAPI);
                } else {
                    this.openApiAsYaml = Yaml.mapper()
                                             .writeValueAsString(openAPI);
                }
            } catch (Exception ex) {
                throw SpincastStatics.runtimize(ex);
            }
        }
        return prettyFormatted ? this.openApiAsYamlPretty : this.openApiAsYaml;
    }

    @Override
    public void ignoreRoutesByIds(String... ids) {
        if (ids != null) {
            for (String id : ids) {
                getRouteIdsToHide().add(id);
            }
        }
    }

    protected String createHttpMethodAndPathKey(HttpMethod method, String path) {
        String key = "";
        if (method != null) {
            key += method.name();
        }
        key += " - " + path;
        return key;
    }

    @Override
    public void ignoreRouteUsingHttpMethodAndPath(HttpMethod method, String path) {
        String key = createHttpMethodAndPathKey(method, path);
        getRouteHttpMethodAndPathToHide().add(key);
    }


    protected void addYamlStringSpecifiedPaths(OpenAPI openApi) {

        List<Route<R>> mainRoutes = getRouter().getMainRoutes();
        if (mainRoutes == null || mainRoutes.size() == 0) {
            return;
        }

        //==========================================
        // For each main route...
        //==========================================
        for (Route<R> route : mainRoutes) {

            //==========================================
            // Hidden route?
            // From inline annotation or from id.
            //==========================================
            if (route.isSpecsIgnore() || isToHideFromId(route)) {
                logger.info("Route hidden from specs, won't be added:" + route);
                continue;
            }

            Object specs = route.getSpecs();

            //==========================================
            // Paths specified as a YAML string
            //==========================================
            if (specs != null && specs instanceof String) {
                Paths pathsFromYamlString = getPathsFromYamlString(route.getPath(), (String)specs);
                if (pathsFromYamlString != null && pathsFromYamlString.size() > 0) {
                    Paths paths = openApi.getPaths();
                    if (paths == null) {
                        paths = new Paths();
                        openApi.setPaths(paths);
                    }

                    for (Entry<String, PathItem> entry : pathsFromYamlString.entrySet()) {
                        String path = entry.getKey();
                        PathItem pathItem = entry.getValue();
                        if (pathItem != null) {
                            Map<io.swagger.v3.oas.models.PathItem.HttpMethod, io.swagger.v3.oas.models.Operation> operationsMap =
                                    pathItem.readOperationsMap();

                            if (operationsMap != null) {
                                for (Entry<io.swagger.v3.oas.models.PathItem.HttpMethod, io.swagger.v3.oas.models.Operation> entry2 : operationsMap.entrySet()) {

                                    //==========================================
                                    // Hidden route from HTTP method and path.
                                    //==========================================
                                    HttpMethod httpMethod = convertHttpMethodToSpincast(entry2.getKey());
                                    if (isToHideFromHttpMethodAndPath(route, httpMethod)) {
                                        logger.info("Route hidden from specs, won't be added: [" + httpMethod.name() + "] " +
                                                    route);

                                        if (httpMethod == HttpMethod.GET) {
                                            pathItem.setGet(null);
                                        } else if (httpMethod == HttpMethod.POST) {
                                            pathItem.setPost(null);
                                        } else if (httpMethod == HttpMethod.PUT) {
                                            pathItem.setPut(null);
                                        } else if (httpMethod == HttpMethod.PATCH) {
                                            pathItem.setPatch(null);
                                        } else if (httpMethod == HttpMethod.DELETE) {
                                            pathItem.setDelete(null);
                                        } else if (httpMethod == HttpMethod.HEAD) {
                                            pathItem.setHead(null);
                                        } else if (httpMethod == HttpMethod.OPTIONS) {
                                            pathItem.setOptions(null);
                                        } else if (httpMethod == HttpMethod.TRACE) {
                                            pathItem.setTrace(null);
                                        }
                                    }
                                }
                            }

                            paths.addPathItem(path, pathItem);

                        }
                    }
                }
            }
        }
    }

    protected HttpMethod convertHttpMethodToSpincast(io.swagger.v3.oas.models.PathItem.HttpMethod httpMethod) {
        if (httpMethod == io.swagger.v3.oas.models.PathItem.HttpMethod.GET) {
            return HttpMethod.GET;
        } else if (httpMethod == io.swagger.v3.oas.models.PathItem.HttpMethod.POST) {
            return HttpMethod.POST;
        } else if (httpMethod == io.swagger.v3.oas.models.PathItem.HttpMethod.PUT) {
            return HttpMethod.PUT;
        } else if (httpMethod == io.swagger.v3.oas.models.PathItem.HttpMethod.PATCH) {
            return HttpMethod.PATCH;
        } else if (httpMethod == io.swagger.v3.oas.models.PathItem.HttpMethod.DELETE) {
            return HttpMethod.DELETE;
        } else if (httpMethod == io.swagger.v3.oas.models.PathItem.HttpMethod.HEAD) {
            return HttpMethod.HEAD;
        } else if (httpMethod == io.swagger.v3.oas.models.PathItem.HttpMethod.OPTIONS) {
            return HttpMethod.OPTIONS;
        } else if (httpMethod == io.swagger.v3.oas.models.PathItem.HttpMethod.TRACE) {
            return HttpMethod.TRACE;
        }

        throw new RuntimeException("HTTP method from Swagger not managed: " + httpMethod);
    }


}
