package org.spincast.demos.better;

import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.json.JsonObject;

public class AppController {

    public void index(DefaultRequestContext context) {

        JsonObject user = context.json().create();
        user.set("name", "Stromgol");
        user.set("age", 42);

        context.response().sendJson(user);
    }
}
