package org.spincast.plugins.swagger.ui.config;


public class SpincastSwaggerUiConfigDefault implements SpincastSwaggerUiConfig {

    @Override
    public String getSwaggerUiPath() {
        return "/swagger-ui";
    }

    @Override
    public String getOpenApiSpecificationsUrl() {
        return "https://petstore.swagger.io/v2/swagger.json";
    }

    @Override
    public boolean showTopBar() {
        return false;
    }

}
