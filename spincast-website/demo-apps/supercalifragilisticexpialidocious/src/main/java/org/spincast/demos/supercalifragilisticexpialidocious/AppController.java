package org.spincast.demos.supercalifragilisticexpialidocious;

import org.spincast.core.json.JsonObject;
import org.spincast.core.routing.Router;
import org.spincast.core.websocket.DefaultWebsocketContext;

import com.google.inject.Inject;

public class AppController {

    /**
     * Init method : we inject the Router and add some Routes to it.
     */
    @Inject
    protected void init(Router<AppRequestContext, DefaultWebsocketContext> router) {

        router.GET("/").handle(this::indexPage);
        router.GET("/github-source/${username}").handle(this::githubSource);

        router.cors();
    }

    /**
     * Simple "Hello World" response on the "/" Route.
     */
    public void indexPage(AppRequestContext context) {
        context.response().sendPlainText("Hello World!");
    }

    /**
     * Route Handler for the "/github-source/${username}" Route.
     * 
     * We retrieve the HTML source of the GitHub page associated
     * with the specified username, and return it in a Json object.
     */
    public void githubSource(AppRequestContext context) {

        String username = context.request().getPathParam("username");

        String url = "https://github.com/" + username;

        String src = context.httpClient().GET(url).send().getContentAsString();

        JsonObject response = context.json().create();
        response.set("username", username);
        response.set("url", url);
        response.set("source", src);

        context.response().sendJson(response);
    }
}
