package org.spincast.website.controllers.demos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.flash.FlashMessageLevel;
import org.spincast.core.request.Form;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.website.exchange.AppRequestContext;

/**
 * HTML Forms - Single Field demo controller
 */
public class DemoHtmlFormsSingleFieldController {

    protected final Logger logger = LoggerFactory.getLogger(DemoHtmlFormsSingleFieldController.class);

    /**
     * Single Field demo - GET
     */
    public void singleField(AppRequestContext context) {

        //==========================================
        // Sends HTML by rendering the specified template.
        //==========================================
        context.response().sendTemplateHtml("/templates/demos/htmlForms/single.html");
    }

    /**
     * Single Field demo - POST
     */
    public void singleFieldSubmit(AppRequestContext context) {

        //==========================================
        // Gets the form data as a Form object,
        // and adds it to the response's model.
        // The validation message will be added to the
        // default "validation" model element.
        //==========================================
        Form form = context.request().getFormOrCreate("demoForm");
        context.response().addForm(form);

        //==========================================
        // Validates the form
        //==========================================
        validateForm(form);

        //==========================================
        // This is only for our demo, to allow the
        // form to be redisplayed even if it's
        // valid!
        //==========================================
        boolean process = true;
        if (!form.isValid() ||
            form.getMessages().size() > 0 ||
            "stay@example.com".equals(form.getString("email"))) {
            process = false;
        }

        //==========================================
        // If the form is invalid, we redisplay it
        // simply by calling the GET handler.
        //==========================================
        if (!process) {
            singleField(context);
            return;

        //==========================================@formatter:off 
        // The form is valid!
        // We redirect the user to a confirmation
        // page with a success Flash Message.
        //==========================================@formatter:on
        } else {
            context.response().redirect(FlashMessageLevel.SUCCESS,
                                        "The form has been processed successfully.");
        }
    }

    /**
     * Validates the form. 
     * <p>
     * The results of the validations
     * are saved in the form itself, so there is no need to
     * return anything.
     */
    protected void validateForm(Form form) {

        //==========================================
        // Email validation
        //==========================================
        String email = form.getString("email");

        if (StringUtils.isBlank(email)) {
            form.addError("email",
                          "email_empty",
                          "The email is required");
        }

        //==========================================
        // We perform this second validation only
        // if the previous one were valid.
        //==========================================
        if (form.isValid("email") && !form.validators().isEmailValid(email)) {
            form.addError("email",
                          "email_invalid",
                          "The email is invalid");
        }

        //==========================================
        // Here's an example of a *warning* message.
        //==========================================
        if (form.isValid("email") && email.length() < 8) {
            form.addWarning("email",
                            "email_minLength",
                            "This is valid but seems quite short for an email.");
        }

        //==========================================
        // Here's an example of a *success* message.
        //==========================================
        if (!form.hasMessages("email") && "success@example.com".equalsIgnoreCase(email)) {
            form.addSuccess("email",
                            "email_good",
                            "This is a success message");
        }

        //==========================================
        // Here's an example of adding multiple messages
        // for a single element.
        //==========================================
        if (!form.hasMessages("email") && "all@example.com".equalsIgnoreCase(email)) {

            form.addSuccess("email",
                            "email_success",
                            "This is a success message");

            form.addWarning("email",
                            "email_warning",
                            "This is a warning message");

            form.addError("email",
                          "email_error",
                          "This is an error message");
        }
    }
}
