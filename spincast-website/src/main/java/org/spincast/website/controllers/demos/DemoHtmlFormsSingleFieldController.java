package org.spincast.website.controllers.demos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.json.JsonObject;
import org.spincast.core.session.FlashMessageLevel;
import org.spincast.core.validation.JsonObjectValidationSet;
import org.spincast.core.validation.ValidationSet;
import org.spincast.website.exchange.AppRequestContext;

/**
 * HTML Forms - Single Field demo controller
 */
public class DemoHtmlFormsSingleFieldController {

    protected final Logger logger = LoggerFactory.getLogger(DemoHtmlFormsSingleFieldController.class);

    /**
     * Send HTML by rendering the specified template
     * and the current response model.
     */
    protected void sendTemplate(AppRequestContext context) {
        context.response().sendTemplateHtml("/templates/demos/htmlForms/single.html");
    }

    /**
     * Single Field demo - GET
     */
    public void singleField(AppRequestContext context) {
        sendTemplate(context);
    }

    /**
     * Single Field demo - POST
     */
    public void singleFieldSubmit(AppRequestContext context) {

        //==========================================
        // Gets the form data
        //==========================================
        JsonObject form = context.request().getFormData().getJsonObject("demoForm");

        //==========================================
        // Validates the form and prefixes the
        // resulting validation keys with "demoForm.".
        // Doing so, we make sure the keys represent the
        // JsonPath of the validated element they are
        // associated with.
        //==========================================
        ValidationSet validationResult = validateForm(form).prefixValidationKeys("demoForm.");

        //==========================================
        // This is only for our demo, to allow the
        // form to be redisplayed even if it's
        // valid!
        //==========================================
        boolean process = true;
        if(!validationResult.isValid() ||
           validationResult.getMessages().size() > 0 ||
           "stay@example.com".equals(form.getString("email"))) {
            process = false;
        }

        //==========================================
        // If the form is invalid, we add it back to
        // the response model. We also add the
        // Validation Set, using a "validation" key
        // to scope it.
        //==========================================
        if(!process) {
            context.response().getModel().put("demoForm", form);
            context.response().getModel().put("validation", validationResult);
            sendTemplate(context);

        //==========================================@formatter:off 
        // The form is valid!
        // We redirect the user to a confirmation
        // page with a Flash Message to display.
        //==========================================@formatter:on
        } else {
            context.response().redirect(FlashMessageLevel.SUCCESS,
                                        "The form has been processed successfully.");
        }
    }

    /**
     * Validation of the form
     */
    protected ValidationSet validateForm(JsonObject form) {

        //==========================================
        // We create a Validation Set for the form.
        //==========================================
        JsonObjectValidationSet validationSet = form.validationSet();

        //==========================================
        // We apply some predefine validations on the
        // email element...
        //==========================================
        @SuppressWarnings("unused")
        ValidationSet lastResult = validationSet.validationNotBlank()
                                                .jsonPath("email")
                                                .failMessageText("The email is required")
                                                .validate();

        if(!validationSet.hasMessages()) {
            lastResult = validationSet.validationEmail()
                                      .jsonPath("email")
                                      .validate();
        }

        if(!validationSet.hasMessages()) {
            lastResult = validationSet.validationMinLength(8)
                                      .jsonPath("email")
                                      .treatErrorAsWarning()
                                      .failMessageText("This is valid but seems quite short for an email.")
                                      .validate();
        }

        //==========================================
        // Some manual validations!
        //==========================================
        if(!validationSet.hasMessages()) {

            String email = form.getString("email");

            if("success@example.com".equalsIgnoreCase(email)) {
                lastResult = validationSet.addSuccess("email", "successEmail", "This is a success message");

            } else if("all@example.com".equalsIgnoreCase(email)) {
                lastResult = validationSet.addSuccess("email", "successEmail", "This is a success message");
                lastResult = validationSet.addWarning("email", "warningEmail", "This is a warning message");
                lastResult = validationSet.addError("email", "errorEmail", "This is an error message");
            }
        }

        return validationSet;
    }
}
