package org.spincast.website;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.guice.TestingMode;
import org.spincast.plugins.config.SpincastConfigDefault;
import org.spincast.plugins.config.SpincastConfigPluginConfig;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.shaded.org.apache.commons.lang3.tuple.Pair;

import com.google.inject.Inject;

/**
 * Application configurations
 */
public class AppConfigDefault extends SpincastConfigDefault implements AppConfig {

    protected static final Logger logger = LoggerFactory.getLogger(AppConfigDefault.class);

    private List<Pair<String, String>> adminUsernamesPasswords;

    /**
     * Constructor
     */
    @Inject
    public AppConfigDefault(SpincastConfigPluginConfig spincastConfigPluginConfig, @TestingMode boolean testingMode) {
        super(spincastConfigPluginConfig, testingMode);
    }

    @Override
    public String getPublicUrlBase() {
        return getString("api.base", super.getPublicUrlBase());
    }

    @Override
    public int getHttpServerPort() {
        return getInteger("server.port.http", -1);
    }

    @Override
    public int getHttpsServerPort() {
        return getInteger("server.port.https", 44420);
    }

    @Override
    public String getHttpsKeyStorePath() {
        return "certificates/devKeyStore.jks";
    }

    @Override
    public String getHttpsKeyStoreType() {
        return "JKS";
    }

    @Override
    public String getHttpsKeyStoreStorePass() {
        return "myStorePass";
    }

    @Override
    public String getHttpsKeyStoreKeyPass() {
        return "myKeyPass";
    }

    @Override
    public String getEnvironmentName() {
        return getString("environment.name", super.getEnvironmentName());
    }

    @Override
    public boolean isDevelopmentMode() {
        return getBoolean("environment.isDebug", super.isDevelopmentMode());
    }

    @Override
    protected String getSpincastWritableDirPath() {
        return getString("writableDirPath", super.getSpincastWritableDirPath());
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

        if (this.adminUsernamesPasswords == null) {

            List<Map<String, Object>> administrators =
                    getMapList("administrators", new ArrayList<Map<String, Object>>());

            List<Pair<String, String>> usernamesPasswords = new ArrayList<Pair<String, String>>();

            if (administrators != null && administrators.size() > 0) {

                for (Map<String, Object> administratorInfo : administrators) {

                    String name = (String)administratorInfo.get("username");
                    if (!StringUtils.isBlank(name)) {

                        String pass = (String)administratorInfo.get("password");
                        if (StringUtils.isBlank(pass)) {
                            logger.error("The password can't be empty for user '" + name + "'");
                        } else {
                            Pair<String, String> userPassPair = Pair.of(name, pass);
                            usernamesPasswords.add(userPassPair);
                        }
                    }
                }
            } else {

                //==========================================
                // Dev credentials
                //==========================================
                if (isDevelopmentMode() && getPublicUrlBase().startsWith("http://localhost:")) {
                    Pair<String, String> userPassPair = Pair.of("admin", "admin");
                    usernamesPasswords.add(userPassPair);
                }
            }

            this.adminUsernamesPasswords = usernamesPasswords;
        }
        return this.adminUsernamesPasswords;

    }

    @Override
    public String getQueryParamFlashMessageId() {
        return "flash";
    }

}
