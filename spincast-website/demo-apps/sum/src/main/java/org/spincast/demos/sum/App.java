package org.spincast.demos.sum;

import org.spincast.core.server.Server;
import org.spincast.defaults.bootstrapping.Spincast;
import org.spincast.plugins.routing.DefaultRouter;

import com.google.inject.Inject;

public class App {

    public static void main(String[] args) {
        Spincast.configure()
                .module(new AppModule())
                .init(args);
    }

    @Inject
    protected void init(DefaultRouter router,
                        AppController ctrl,
                        Server server) {

        router.POST("/sum").handle(ctrl::sumRoute);
        server.start();
    }
}
