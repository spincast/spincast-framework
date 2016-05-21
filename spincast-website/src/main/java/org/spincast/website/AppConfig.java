package org.spincast.website;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.guice.MainArgs;
import org.spincast.core.utils.ISpincastUtils;
import org.spincast.plugins.configpropsfile.ISpincastConfigPropsFileBasedConfig;
import org.spincast.plugins.configpropsfile.SpincastConfigPropsFileBased;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.shaded.org.apache.commons.lang3.tuple.Pair;

import com.google.inject.Inject;

/**
 * We use the "spincast-plugins-config-properties-file" plugin,
 * so we can override some configurations in an "app.properties" file.
 */
public class AppConfig extends SpincastConfigPropsFileBased implements IAppConfig {

    protected final Logger logger = LoggerFactory.getLogger(AppConfig.class);

    public static final String APP_PROPERTIES_KEY_SERVER_SCHEME_HOST_PORT = "app.server.scheme_host_port";
    public static final String APP_PROPERTIES_KEY_ADMIN_CREDENTIAL_KEYS = "app.admin.admin_credentials_keys";

    private List<Pair<String, String>> adminUsernamesPasswords;

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
        return getConfig(APP_PROPERTIES_KEY_SERVER_SCHEME_HOST_PORT, "http://localhost:" + getHttpServerPort());
    }

    @Override
    public int getNbrNewsEntriesOnNewsPage() {
        return 5;
    }

    @Override
    public int getNbrNewsEntriesPerFeedRequest() {
        return 25;
    }

    @Override
    public List<Pair<String, String>> getAdminUsernamesPasswords() {

        if(this.adminUsernamesPasswords == null) {

            List<Pair<String, String>> usernamesPasswords = new ArrayList<Pair<String, String>>();

            String credentialKeysStr = getConfig(APP_PROPERTIES_KEY_ADMIN_CREDENTIAL_KEYS, "");
            if(!StringUtils.isBlank(credentialKeysStr)) {

                String[] credentialKeys = credentialKeysStr.split(",");
                for(String credentialKey : credentialKeys) {
                    if(!StringUtils.isBlank(credentialKey)) {
                        credentialKey = credentialKey.trim();
                        String userPassStr = getConfig(credentialKey);
                        if(!StringUtils.isBlank(userPassStr)) {

                            int commaPos = userPassStr.indexOf(",");
                            if(commaPos < 0) {
                                throw new RuntimeException("Invalid configuration in the .properties file. A " +
                                                           "admin credential entry doesn't contain a comma: " + credentialKey);
                            }
                            String userName = userPassStr.substring(0, commaPos).trim();
                            if(StringUtils.isBlank(userName)) {
                                throw new RuntimeException("Invalid configuration in the .properties file. A " +
                                                           "empty suername is not valid for key: " + credentialKey);
                            }

                            String password = userPassStr.substring(commaPos + 1).trim();
                            if(StringUtils.isBlank(password)) {
                                throw new RuntimeException("Invalid configuration in the .properties file. A " +
                                                           "empty password is not valid for user: " + userName);
                            }

                            Pair<String, String> userPassPair = Pair.of(userName, password);
                            usernamesPasswords.add(userPassPair);
                        }
                    }
                }
            } else {

                //==========================================
                // Dev credentials
                //==========================================
                if(isDebugEnabled() && getServerSchemeHostPort().startsWith("http://localhost:")) {
                    Pair<String, String> userPassPair = Pair.of("admin", "admin");
                    usernamesPasswords.add(userPassPair);
                }
            }

            this.adminUsernamesPasswords = usernamesPasswords;
        }
        return this.adminUsernamesPasswords;

    }

}
