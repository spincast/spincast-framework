package org.spincast.quickstart.controller;

import java.util.HashMap;
import java.util.Map;

import org.spincast.core.config.SpincastConstants.RequestScopedVariables;
import org.spincast.core.exceptions.IPublicException;
import org.spincast.core.exceptions.PublicException;
import org.spincast.core.json.IJsonObject;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.quickstart.config.IAppConfig;
import org.spincast.quickstart.exchange.IAppRequestContext;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.shaded.org.apache.http.HttpStatus;

import com.google.inject.Inject;

/**
 * Implementation of an application controller.
 */
public class AppController implements IAppController {

    private final IAppConfig appConfig;

    @Inject
    public AppController(IAppConfig appConfig) {
        this.appConfig = appConfig;
    }

    protected IAppConfig getAppConfig() {
        return this.appConfig;
    }

    @Override
    public void notFound(IAppRequestContext context) {

        String notFoundMessage = "Page not found";

        //==========================================
        // There may be a public and custom "Not Found" message
        // set by the controller that we should display:
        //==========================================
        String specificNotFoundMessage =
                context.variables().getAsString(RequestScopedVariables.NOT_FOUND_PUBLIC_MESSAGE);

        if(specificNotFoundMessage != null) {
            notFoundMessage = specificNotFoundMessage;
        }

        //==========================================
        // Our Not Found handler manages Json and HTML
        // requests. We return the response in the
        // appropriated format.
        //==========================================
        if(context.request().isJsonShouldBeReturn()) {
            IJsonObject errorObj = context.json().create();
            errorObj.put("message", notFoundMessage);
            context.response().sendJsonObj(errorObj);
        } else {
            context.response().sendHtmlTemplate("/templates/notFound.html",
                                                SpincastStatics.params("notFoundMessage", notFoundMessage));
        }
    }

    @Override
    public void exception(IAppRequestContext context) {

        String errorMessage = "An error occured! Please try again later...";

        //==========================================
        // We have access to the exception which was threw,
        // using the associated request scoped variables:
        //==========================================
        Throwable originalException =
                context.variables().get(RequestScopedVariables.EXCEPTION, Throwable.class);

        //==========================================
        // If the exception which was thres is an instance of 
        // IPublicException, it means we should display its
        // message to the user.
        //==========================================
        if(originalException != null && originalException instanceof IPublicException) {
            errorMessage = originalException.getMessage();
        }

        //==========================================
        // Our Exception handler manages Json and HTML
        // requests. We return the response in the
        // appropriated format.
        //==========================================
        if(context.request().isJsonShouldBeReturn()) {
            IJsonObject errorObj = context.json().create();
            errorObj.put("error", errorMessage);
            context.response().sendJsonObj(errorObj);
        } else {
            context.response().sendHtmlTemplate("/templates/exception.html",
                                                SpincastStatics.params("errorMessage", errorMessage));
        }
    }

    @Override
    public void indexPage(IAppRequestContext context) {

        //==========================================
        // Render an HTML template with some parameters.
        //==========================================
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("appName", getAppConfig().getAppName());
        variables.put("serverPort", getAppConfig().getHttpServerPort());

        context.response().sendHtmlTemplate("/templates/index.html", variables);
    }

    @Override
    public void sumRoute(IAppRequestContext context) {

        String firstNbr = context.request().getFormDataFirst("first");
        if(StringUtils.isBlank(firstNbr)) {
            throw new PublicException("The 'first' post parameter is required.", HttpStatus.SC_BAD_REQUEST);
        }
        String secondNbr = context.request().getFormDataFirst("second");
        if(StringUtils.isBlank(secondNbr)) {
            throw new PublicException("The 'second' post parameter is required.", HttpStatus.SC_BAD_REQUEST);
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

        IJsonObject resultObj = context.json().create();
        if(error != null) {
            context.response().setStatusCode(HttpStatus.SC_BAD_REQUEST);
            resultObj.put("error", error);
        } else {
            resultObj.put("result", String.valueOf(sum));
        }

        context.response().sendJsonObj(resultObj);
    }
}
