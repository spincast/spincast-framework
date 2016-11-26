package org.spincast.quickstart.exchange;

import org.spincast.core.exchange.RequestContextBase;
import org.spincast.core.exchange.RequestContextBaseDeps;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * Implementation of our custom Request Context type.
 */
public class AppRequestContextDefault extends RequestContextBase<AppRequestContext>
                                      implements AppRequestContext {

    @AssistedInject
    public AppRequestContextDefault(@Assisted Object exchange, RequestContextBaseDeps<AppRequestContext> requestContextBaseDeps) {
        super(exchange, requestContextBaseDeps);
    }

    /**
     * Our simple example will take a "name" parameter from the path
     * ("/greet/${name}" for example) and will output
     * a greeting message.
     */
    @Override
    public void customGreetingMethod() {
        String name = request().getPathParam("name");
        if(name == null) {
            throw new RuntimeException("The 'name' parameter was not found, " +
                                       "full url : " + request().getFullUrl());
        }

        response().sendPlainText("Hello " + name + "!");
    }
}
