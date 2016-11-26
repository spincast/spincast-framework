package org.spincast.plugins.httpclient;

import com.google.inject.ImplementedBy;

/**
 * Configurations for the Spincast Http Client plugin.
 * 
 * We use "@ImplementedBy" to specify the default configurations
 * to use if none is specified in a Guice module.
 */
@ImplementedBy(SpincastHttpClientConfigDefault.class)
public interface SpincastHttpClientConfig {
    // none for now
}
