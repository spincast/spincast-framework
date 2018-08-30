package org.spincast.demos.quick;

import org.spincast.core.server.Server;
import org.spincast.defaults.bootstrapping.Spincast;
import org.spincast.plugins.routing.DefaultRouter;

import com.google.inject.Inject;

public class App {

    public static void main(String[] args) {
        Spincast.init(args);
    }

    @Inject
    protected void init(DefaultRouter router, Server server) {
        router.GET("/").handle(context -> context.response().sendHtml("<h1>Hello World!</h1>"));
        server.start();
    }

}
