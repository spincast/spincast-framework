package org.spincast.core.server;

import java.util.Set;

import com.google.inject.Inject;

public class ServerUtilsDefault implements ServerUtils {

    private final Set<ServerStartedListener> serverStartedListeners;

    @Inject
    public ServerUtilsDefault(Set<ServerStartedListener> serverStartedListeners) {
        this.serverStartedListeners = serverStartedListeners;
    }

    protected Set<ServerStartedListener> getServerStartedListeners() {
        return this.serverStartedListeners;
    }

    @Override
    public void callServerStartedListeners() {

        for (ServerStartedListener serverStartedListener : getServerStartedListeners()) {
            Thread t = new Thread(new Runnable() {

                @Override
                public void run() {
                    serverStartedListener.serverStartedSuccessfully();
                }
            });
            t.start();
        }
    }

}
