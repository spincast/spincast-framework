package org.spincast.website;

import java.util.List;

import org.spincast.core.server.IServer;
import org.spincast.shaded.org.apache.commons.lang3.tuple.Pair;

import com.google.inject.Inject;

/**
 * Initilizes the HTTP authentication.
 */
public class HttpAuthInit {

    private final IAppConfig appConfig;
    private final IServer server;

    @Inject
    public HttpAuthInit(IAppConfig appConfig, IServer server) {
        this.appConfig = appConfig;
        this.server = server;
    }

    protected IAppConfig getAppConfig() {
        return this.appConfig;
    }

    protected IServer getServer() {
        return this.server;
    }

    @Inject
    protected void init() {

        //==========================================
        // Admin section
        //==========================================
        List<Pair<String, String>> adminUsernamesPasswords = getAppConfig().getAdminUsernamesPasswords();
        for(Pair<String, String> adminUsernamesPassword : adminUsernamesPasswords) {
            getServer().addHttpAuthentication(AppConstants.HTTP_AUTH_REALM_NAME_ADMIN,
                                              adminUsernamesPassword.getKey(),
                                              adminUsernamesPassword.getValue());
        }

        //==========================================
        // Example page
        //==========================================
        getServer().addHttpAuthentication(AppConstants.HTTP_AUTH_REALM_NAME_EXAMPLE, "Stromgol", "Laroche");

    }

}
