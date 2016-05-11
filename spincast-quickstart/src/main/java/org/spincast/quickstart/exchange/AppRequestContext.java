package org.spincast.quickstart.exchange;

import org.spincast.core.exchange.RequestContextBase;
import org.spincast.core.exchange.RequestContextBaseDeps;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * Implementation of our custom request context type.
 */
public class AppRequestContext extends RequestContextBase<IAppRequestContext>
                               implements IAppRequestContext {

    @AssistedInject
    public AppRequestContext(@Assisted Object exchange, RequestContextBaseDeps<IAppRequestContext> requestContextBaseDeps) {
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
