package org.spincast.plugins.openapi.bottomup.config;

import org.spincast.plugins.openapi.bottomup.SpincastOpenApiManager;

import io.swagger.v3.oas.models.OpenAPI;

/**
 * Configurations for the Spincast Open API Bottom Up plugin.
 */
public interface SpincastOpenApiBottomUpPluginConfig {

    /**
     * If this returns <code>true</code> no automatic
     * specs are going to be generated from the main
     * routes.
     * <p>
     * It is useful to disable such auto specs when you
     * want to specify the <em>full</em> {@link OpenAPI} object
     * using {@link SpincastOpenApiManager#setOpenApiBase(OpenAPI)}.
     */
    public boolean isDisableAutoSpecs();

    /**
     * The default consumes content types.
     * <p>
     * Returning <code>null</code> or an empty
     * array will result in a single "*&#47;*"
     * content type.
     * <p>
     * By default, returns "<code>application/json</code>".
     */
    public String[] getDefaultConsumesContentTypes();

    /**
     * The default produces content types.
     * <p>
     * Returning <code>null</code> or an empty
     * array will result in a single "*&#47;*"
     * content type.
     * <p>
     * By default, returns "<code>application/json</code>".
     */
    public String[] getDefaultProducesContentTypes();

}
