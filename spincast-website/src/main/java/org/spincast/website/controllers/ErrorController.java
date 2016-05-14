package org.spincast.website.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.config.SpincastConstants;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.website.AppConstants;
import org.spincast.website.exchange.IAppRequestContext;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;

public class ErrorController {

    protected final Logger logger = LoggerFactory.getLogger(ErrorController.class);

    /**
     * Not Found (404) handler
     */
    public void notFoundHandler(IAppRequestContext context) {

        Map<String, Object> params = new HashMap<String, Object>();

        //==========================================
        // Do we have a custom message to display?
        //==========================================
        String message = "404 - Page not found";
        String customMessage = context.variables()
                                      .getAsString(SpincastConstants.RequestScopedVariables.NOT_FOUND_PUBLIC_MESSAGE);
        if(!StringUtils.isBlank(customMessage)) {
            message = customMessage;
        }
        params.put("message", message);

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
        params.put("htmlSectionClasses", htmlSectionClasses);

        context.response().sendHtmlTemplate("/templates/notFound.html", params);
    }

    /**
     * Exception handler
     */
    public void exceptionHandler(IAppRequestContext context) {

        Exception exception = context.variables().get(SpincastConstants.RequestScopedVariables.EXCEPTION,
                                                      Exception.class);
        this.logger.error(SpincastStatics.getStackTrace(exception));

        if(context.request().isJsonRequest()) {
            context.response().sendJsonObj("Server error");
        } else {
            context.response().sendHtmlTemplate("/templates/exception.html", null);
        }
    }

}
