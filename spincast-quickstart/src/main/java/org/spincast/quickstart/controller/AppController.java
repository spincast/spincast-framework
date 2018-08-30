package org.spincast.quickstart.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.config.SpincastConstants.RequestScopedVariables;
import org.spincast.core.exceptions.PublicException;
import org.spincast.core.json.JsonObject;
import org.spincast.quickstart.config.AppConfig;
import org.spincast.quickstart.exchange.AppRequestContext;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;

/**
 * The application controller.
 */
public class AppController {

    protected final Logger logger = LoggerFactory.getLogger(AppController.class);

    private final AppConfig appConfig;

    @Inject
    public AppController(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    protected AppConfig getAppConfig() {
        return this.appConfig;
    }

    /**
     * Adds some common elements to the response's model.
     */
    protected void addCommonModelElements(AppRequestContext context) {
        context.response().getModel().set("appName", getAppConfig().getAppName());
        context.response().getModel().set("serverPort", getAppConfig().getHttpServerPort());
    }

    /**
     * Index page handler
     */
    public void index(AppRequestContext context) {

        addCommonModelElements(context);
        context.response().sendTemplateHtml("/templates/index.html");
    }

    /**
     * Simple Form example handler
     */
    public void formExample(AppRequestContext context) {

        String userName = context.request().getQueryStringParamFirst("userName");
        String greetings = "";
        if (!StringUtils.isBlank(userName)) {
            greetings = "Hi " + userName + "!";
        }
        context.response().getModel().set("userName", userName);
        context.response().getModel().set("greetings", greetings);

        addCommonModelElements(context);

        context.response().sendTemplateHtml("/templates/form.html");
    }

    /**
     * "Exception example" handler
     */
    public void exceptionExample(AppRequestContext context) {

        throw new RuntimeException("This simulates an exception in the application!");
    }

    /**
     * Route Handler to manage 404
     */
    public void notFound(AppRequestContext context) {

        String notFoundMessage = "Page not found";

        //==========================================
        // Is there a custom "Not Found" message
        // to display?
        //==========================================
        String specificNotFoundMessage =
                context.variables().getAsString(RequestScopedVariables.NOT_FOUND_PUBLIC_MESSAGE);

        if (specificNotFoundMessage != null) {
            notFoundMessage = specificNotFoundMessage;
        }

        //==========================================
        // We return the response in the
        // appropriated format.
        //==========================================
        if (context.request().isJsonShouldBeReturn()) {
            JsonObject errorObj = context.json().create();
            errorObj.set("message", notFoundMessage);
            context.response().sendJson(errorObj);
        } else {
            context.response().getModel().set("notFoundMessage", notFoundMessage);
            addCommonModelElements(context);
            context.response().sendTemplateHtml("/templates/notFound.html");
        }
    }

    /**
     * Route Handler to manage exceptions
     */
    public void exception(AppRequestContext context) {

        String errorMessage = "An error occured! Please try again later...";

        //==========================================
        // We have access to the exception that was threw :
        //==========================================
        Throwable originalException =
                context.variables().get(RequestScopedVariables.EXCEPTION, Throwable.class);

        this.logger.error("An exception occured : " + originalException);

        //==========================================
        // If the exception that was threw is an instance of 
        // PublicException, it means we should display its
        // message to the user.
        //==========================================
        if (originalException != null && originalException instanceof PublicException) {
            errorMessage = originalException.getMessage();
        }

        //==========================================
        // We return the response in the
        // appropriated format.
        //==========================================
        if (context.request().isJsonShouldBeReturn()) {
            JsonObject errorObj = context.json().create();
            errorObj.set("error", errorMessage);
            context.response().sendJson(errorObj);
        } else {
            context.response().getModel().set("errorMessage", errorMessage);
            addCommonModelElements(context);
            context.response().sendTemplateHtml("/templates/exception.html");
        }
    }
}
