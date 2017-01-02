package org.spincast.demos.better;

import org.spincast.core.server.Server;
import org.spincast.defaults.bootstrapping.Spincast;
import org.spincast.plugins.routing.DefaultRouter;

import com.google.inject.Inject;

public class App {

    public static void main(String[] args) {
        Spincast.configure()
                .module(new AppModule())
                .init();
    }

    @Inject
    protected void init(Server server, DefaultRouter router, AppController ctrl) {
        router.GET("/").save(ctrl::index);
        server.start();
    }

}
