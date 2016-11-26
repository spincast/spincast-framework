package org.spincast.quickstart.controller;

import org.spincast.core.config.SpincastConstants.RequestScopedVariables;
import org.spincast.core.exceptions.PublicException;
import org.spincast.core.exceptions.PublicExceptionDefault;
import org.spincast.core.json.JsonObject;
import org.spincast.quickstart.config.AppConfig;
import org.spincast.quickstart.exchange.AppRequestContext;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.shaded.org.apache.http.HttpStatus;

import com.google.inject.Inject;

/**
 * The application controller.
 */
public class AppController {

    private final AppConfig appConfig;

    @Inject
    public AppController(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    protected AppConfig getAppConfig() {
        return this.appConfig;
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

        if(specificNotFoundMessage != null) {
            notFoundMessage = specificNotFoundMessage;
        }

        //==========================================
        // We return the response in the
        // appropriated format.
        //==========================================
        if(context.request().isJsonShouldBeReturn()) {
            JsonObject errorObj = context.json().create();
            errorObj.put("message", notFoundMessage);
            context.response().sendJson(errorObj);
        } else {
            context.response().getModel().put("notFoundMessage", notFoundMessage);
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

        //==========================================
        // If the exception that was threw is an instance of 
        // PublicException, it means we should display its
        // message to the user.
        //==========================================
        if(originalException != null && originalException instanceof PublicException) {
            errorMessage = originalException.getMessage();
        }

        //==========================================
        // We return the response in the
        // appropriated format.
        //==========================================
        if(context.request().isJsonShouldBeReturn()) {
            JsonObject errorObj = context.json().create();
            errorObj.put("error", errorMessage);
            context.response().sendJson(errorObj);
        } else {
            context.response().getModel().put("errorMessage", errorMessage);
            context.response().sendTemplateHtml("/templates/exception.html");
        }
    }

    /**
     * Index page handler
     */
    public void indexPage(AppRequestContext context) {

        //==========================================
        // Render an HTML template with some parameters.
        //==========================================
        context.response().getModel().put("appName", getAppConfig().getAppName());
        context.response().getModel().put("serverPort", getAppConfig().getHttpServerPort());

        context.response().sendTemplateHtml("/templates/index.html");
    }

    /**
     * /sum route handler
     */
    public void sumRoute(AppRequestContext context) {

        String firstNbr = context.request().getFormData().getString("first");
        if(StringUtils.isBlank(firstNbr)) {
            throw new PublicExceptionDefault("The 'first' post parameter is required.",
                                             HttpStatus.SC_BAD_REQUEST);
        }
        String secondNbr = context.request().getFormData().getString("second");
        if(StringUtils.isBlank(secondNbr)) {
            throw new PublicExceptionDefault("The 'second' post parameter is required.",
                                             HttpStatus.SC_BAD_REQUEST);
        }

        String error = null;
        long sum = 0;
        do {
            int firstInt;
            try {
                firstInt = Integer.parseInt(firstNbr);
            } catch(NumberFormatException ex) {
                error = ex.getMessage();
                break;
            }

            int secondInt;
            try {
                secondInt = Integer.parseInt(secondNbr);
            } catch(NumberFormatException ex) {
                error = ex.getMessage();
                break;
            }

            sum = (long)firstInt + (long)secondInt;

            if(sum > Integer.MAX_VALUE) {
                error = "The sum overflows the maximum integer value, " + Integer.MAX_VALUE;
                break;
            }
            if(sum < Integer.MIN_VALUE) {
                error = "The sum overflows the minimum integer value, " + Integer.MIN_VALUE;
                break;
            }

        } while(false);

        JsonObject resultObj = context.json().create();
        if(error != null) {
            context.response().setStatusCode(HttpStatus.SC_BAD_REQUEST);
            resultObj.put("error", error);
        } else {
            resultObj.put("result", String.valueOf(sum));
        }

        context.response().sendJson(resultObj);
    }
}
