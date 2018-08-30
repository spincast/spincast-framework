package org.spincast.core.config;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.spincast.core.server.ServerStartedListener;
import org.spincast.core.server.ServerUtils;

import com.google.inject.Provider;

/**
 * Interceptor that will call the
 * {@link ServerStartedListener} listeners once the server
 * is started.
 */
public class ServerStartedInterceptor implements MethodInterceptor {

    private final Provider<ServerUtils> serverUtilsProvider;
    private ServerUtils serverUtils;

    public ServerStartedInterceptor(Provider<ServerUtils> serverUtilsProvider) {
        this.serverUtilsProvider = serverUtilsProvider;
    }

    protected ServerUtils getServerUtils() {
        if (this.serverUtils == null) {
            this.serverUtils = this.serverUtilsProvider.get();
        }
        return this.serverUtils;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        Object result = invocation.proceed();

        getServerUtils().callServerStartedListeners();

        return result;
    }

}
