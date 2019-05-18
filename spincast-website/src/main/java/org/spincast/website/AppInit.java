package org.spincast.website;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.server.ServerStartedListener;
import org.spincast.plugins.httpclient.HttpClient;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.plugins.httpclient.builders.GetRequestBuilder;
import org.spincast.shaded.org.apache.http.HttpStatus;

import com.google.inject.Inject;

/**
 * Called when the HTTP server is started.
 */
public class AppInit implements ServerStartedListener {

    protected static final Logger logger = LoggerFactory.getLogger(AppInit.class);

    private final HttpClient httpClient;
    private final SpincastConfig spincastConfig;

    @Inject
    public AppInit(HttpClient httpClient, SpincastConfig spincastConfig) {
        this.httpClient = httpClient;
        this.spincastConfig = spincastConfig;
    }

    protected HttpClient getHttpClient() {
        return this.httpClient;
    }

    protected SpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    @Override
    public void serverStartedSuccessfully() {
        cacheWebBundle();
    }

    /**
     * Load the main page so the js and
     * css bundles caches are created.
     */
    protected void cacheWebBundle() {

        if (getSpincastConfig().isTestingMode()) {
            logger.info("Testing mode. We skip the web bundles generation.");
            return;
        }

        logger.info("Calling the homepage to generate the web bundles...");
        GetRequestBuilder builder = getHttpClient().GET(getSpincastConfig().getPublicUrlBase());
        if (getSpincastConfig().isDevelopmentMode()) {
            builder = builder.disableSslCertificateErrors();
        }
        HttpResponse response = builder.send();
        if (response.getStatus() != HttpStatus.SC_OK) {
            logger.error("Calling the homepage to generate the web bundles returned an error: " + response.getStatus());
        } else {
            logger.info("Homepage returned 200 to generate the web bundles!");
        }
    }

}
