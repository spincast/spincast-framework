package org.spincast.website;

import org.spincast.core.config.ISpincastConfig;

public interface IAppConfig extends ISpincastConfig {

    /**
     * The application can't know for sure by itself on which
     * scheme/host/port it is served (for example : "https://www.example.com:8080").
     * We have to specify it in the configurations to be able to
     * build absolute URLs.
     */
    public String getServerSchemeHostPort();
}
