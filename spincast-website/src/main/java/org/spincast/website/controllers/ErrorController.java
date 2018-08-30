package org.spincast.website.controllers;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.config.SpincastConstants;
import org.spincast.core.exceptions.CustomStatusCodeException;
import org.spincast.core.exceptions.PublicException;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.website.AppConstants;
import org.spincast.website.exchange.AppRequestContext;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;

public class ErrorController {

    protected final Logger logger = LoggerFactory.getLogger(ErrorController.class);

    /**
     * Not Found (404) handler
     */
    public void notFoundHandler(AppRequestContext context) {

        //==========================================
        // Do we have a custom message to display?
        //==========================================
        String message = "404 - Page not found";
        String customMessage = context.variables()
                                      .getAsString(SpincastConstants.RequestScopedVariables.NOT_FOUND_PUBLIC_MESSAGE);
        if(!StringUtils.isBlank(customMessage)) {
            message = customMessage;
        }
        context.response().getModel().set("message", message);

        //==========================================
        // Do we have HTML classes for the original section
        // which triggers that Not Found route?
        //==========================================
        List<String> htmlSectionClasses =
                context.variables().get(AppConstants.RC_VARIABLE_HTML_SECTION_CLASSES,
                                        Key.get(new TypeLiteral<List<String>>() {}));
        if(htmlSectionClasses == null) {
            htmlSectionClasses = new ArrayList<String>();
        }
        context.response().getModel().set("htmlSectionClasses", htmlSectionClasses);

        context.response().sendTemplateHtml("/templates/errorNotFound.html");
    }

    /**
     * Exception handler
     */
    public void exceptionHandler(AppRequestContext context) {

        Exception exception = context.variables().get(SpincastConstants.RequestScopedVariables.EXCEPTION,
                                                      Exception.class);

        if(!(exception instanceof PublicException)) {
            this.logger.error(SpincastStatics.getStackTrace(exception));
        }

        //==========================================
        // Custom status code to use?
        //==========================================
        if(exception instanceof CustomStatusCodeException) {
            context.response().setStatusCode(((CustomStatusCodeException)exception).getStatusCode());
        }

        //==========================================
        // Public exception?
        //==========================================
        if(exception instanceof PublicException) {
            if(context.request().isJsonShouldBeReturn()) {
                context.response().sendJson(exception.getMessage());
            } else {
                context.response().getModel().set("message", exception.getMessage());
                context.response().sendTemplateHtml("/templates/exceptionPublic.html");
            }
        } else {
            if(context.request().isJsonShouldBeReturn()) {
                context.response().sendJson("Server error");
            } else {
                context.response().sendTemplateHtml("/templates/exception.html");
            }
        }

    }

}
