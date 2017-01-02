package org.spincast.demos.better;

import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.json.JsonObject;

public class AppController {

    public void index(DefaultRequestContext context) {

        JsonObject user = context.json().create();
        user.put("name", "Stromgol");
        user.put("age", 42);

        context.response().sendJson(user);
    }
}
