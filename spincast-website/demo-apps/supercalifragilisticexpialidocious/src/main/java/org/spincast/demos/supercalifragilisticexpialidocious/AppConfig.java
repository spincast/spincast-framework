package org.spincast.demos.supercalifragilisticexpialidocious;

import org.spincast.core.guice.TestingMode;
import org.spincast.plugins.config.SpincastConfigDefault;
import org.spincast.plugins.config.SpincastConfigPluginConfig;

import com.google.inject.Inject;

public class AppConfig extends SpincastConfigDefault {

    /**
     * Constructor
     */
    @Inject
    protected AppConfig(SpincastConfigPluginConfig spincastConfigPluginConfig, @TestingMode boolean testingMode) {
        super(spincastConfigPluginConfig, testingMode);
    }

    /**
     * We change the port the Server will be started on.
     * 
     * Note that we could also have specified a 
     * default value, for example :
     * return getInteger("server.port", 8899);
     */
    @Override
    public int getHttpServerPort() {
        Integer port = getInteger("server.port");
        if (port == null) {
            throw new RuntimeException("The 'port' configuration is required!");
        }
        return port;
    }

    /**
     * It is recommended to always override the 
     * <code>getPublicUrlBase()</code> configuration.
     * Here, we externalize it and use the default only
     * if no custom value if found.
     */
    @Override
    public String getPublicUrlBase() {
        return getString("api.baseUrl", super.getPublicUrlBase());
    }
}
