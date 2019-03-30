package org.spincast.plugins.openapi.bottomup;

import org.spincast.core.routing.HttpMethod;

import io.swagger.v3.oas.models.OpenAPI;

/**
 * The component resposible to store and tweak the
 * informations requierd to generate the
 * Open API specifications.
 */
public interface SpincastOpenApiManager {

    /**
     * The base OpenAPI informations for the generated
     * specs file.
     * <p>
     * This can be a full and standalone OpenAPI object,
     * containing your paths, models, etc. Or it can
     * be the base information to which specs specific to
     * each routes will be added.
     */
    public void setOpenApiBase(OpenAPI baseOpenApiInfo);

    /**
     * The generated specifications as a
     * OpenAPI object.
     */
    public OpenAPI getOpenApi();

    /**
     * The generated specifications as pretty
     * formatted JSON.
     */
    public String getOpenApiAsJson();

    /**
     * The generated specifications as JSON.
     */
    public String getOpenApiAsJson(boolean prettyFormatted);

    /**
     * The generated specifications as pretty
     * formatted YAML.
     */
    public String getOpenApiAsYaml();

    /**
     * The generated specifications as YAML.
     */
    public String getOpenApiAsYaml(boolean prettyFormatted);

    /**
     * Ignore routes using their ids.
     */
    public void ignoreRoutesByIds(String... ids);

    /**
     * Ignore a route using its HTTP method and path.
     *
     * @param method If <code>null</code>, <em>all</em>
     * HTTP methods will be used.
     */
    public void ignoreRouteUsingHttpMethodAndPath(HttpMethod method, String path);

    /**
     * Clears the cache. You need to call this if
     * at runtime you dynamically add some new route/information
     * affecting the generated specifications.
     */
    public void clearCache();

    /**
     * Reset everything : delete caches, delete ignored routes, etc.
     */
    public void resetAll();
}
