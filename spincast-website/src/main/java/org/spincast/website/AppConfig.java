package org.spincast.website;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.guice.MainArgs;
import org.spincast.core.utils.ISpincastUtils;
import org.spincast.plugins.configpropsfile.ISpincastConfigPropsFileBasedConfig;
import org.spincast.plugins.configpropsfile.SpincastConfigPropsFileBased;

import com.google.inject.Inject;

/**
 * We use the "spincast-plugins-config-properties-file" plugin,
 * so we can override some configurations in an "app.properties" file.
 */
public class AppConfig extends SpincastConfigPropsFileBased implements IAppConfig {

    protected final Logger logger = LoggerFactory.getLogger(AppConfig.class);

    @Inject
    public AppConfig(ISpincastUtils spincastUtils,
                     @MainArgs @Nullable String[] mainArgs,
                     @Nullable ISpincastConfigPropsFileBasedConfig pluginConfig) {
        super(spincastUtils, mainArgs, pluginConfig);
    }

    /**
     * We use "44420" as the default port for the website, if
     * this port is not overriden in the .properties file.
     */
    @Override
    public int getHttpServerPort() {
        return getConfigInteger(getConfigKeyHttpServerPort(), 44420);
    }

    @Override
    public String getServerSchemeHostPort() {
        return getConfig("app.server.scheme_host_port", "http://localhost:" + getHttpServerPort());
    }

    @Override
    public int getNbrNewsEntriesPerPage() {
        return 3;
    }

}
