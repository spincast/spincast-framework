package org.spincast.plugins.swagger.ui.config;


public interface SpincastSwaggerUiConfig {

    /**
     * The URL path the Swagger UI will be served on.
     * <p>
     * Defaults to "<em>/swagger-ui</em>"
     */
    public String getSwaggerUiPath();

    /**
     * The absolute URL or relative path to the Open API
     * specifications file.
     * <p>
     * Defaults to "<em>https://petstore.swagger.io/v2/swagger.json</em>"
     */
    public String getOpenApiSpecificationsUrl();

    /**
     * If <code>true</code>, the top bar of the UI
     * will be shown.
     * <p>
     * Defaults to <code>false</code>.
     */
    public boolean showTopBar();

}
