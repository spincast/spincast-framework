package org.spincast.tests.appbasedtesting.app;

import org.spincast.core.json.JsonObject;
import org.spincast.core.routing.Handler;
import org.spincast.core.routing.Router;
import org.spincast.core.server.Server;
import org.spincast.core.websocket.DefaultWebsocketContext;
import org.spincast.defaults.bootstrapping.Spincast;

import com.google.inject.Inject;

/**
 * A simple app that we'll test.
 */
public class App {

    public static void main(String[] args) {
        Spincast.configure()
                .module(new AppModule())
                .requestContextImplementationClass(AppRequestContextDefault.class)
                .init(args);
    }

    @Inject
    protected void init(Router<AppRequestContext, DefaultWebsocketContext> router, Server server) {

        //==========================================
        // Adds a route
        //==========================================
        router.GET("/").handle(new Handler<AppRequestContext>() {

            @Override
            public void handle(AppRequestContext context) {

                JsonObject obj = context.json().create();
                obj.set("k1", "v1");
                obj.set("k2", "v2");

                context.response().sendJson(obj);
            }
        });

        //==========================================
        // Starts the server
        //==========================================
        server.start();
    }

}
