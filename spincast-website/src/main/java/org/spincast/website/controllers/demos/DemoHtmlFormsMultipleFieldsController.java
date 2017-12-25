package org.spincast.website.controllers.demos;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.json.JsonArray;
import org.spincast.core.json.JsonManager;
import org.spincast.core.request.Form;
import org.spincast.core.request.FormFactory;
import org.spincast.core.session.FlashMessageLevel;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.website.exchange.AppRequestContext;

import com.google.inject.Inject;

/**
 * HTML Forms  - Multiple Fields demo controller
 */
public class DemoHtmlFormsMultipleFieldsController {

    protected final Logger logger = LoggerFactory.getLogger(DemoHtmlFormsMultipleFieldsController.class);

    private final FormFactory formFactory;
    private final JsonManager jsonManager;

    @Inject
    public DemoHtmlFormsMultipleFieldsController(FormFactory formFactory,
                                                 JsonManager jsonManager) {
        this.formFactory = formFactory;
        this.jsonManager = jsonManager;
    }

    protected FormFactory getFormFactory() {
        return this.formFactory;
    }

    protected JsonManager getJsonManager() {
        return this.jsonManager;
    }

    /**
     * Multiple Fields demo - GET
     */
    public void multipleFields(AppRequestContext context) {

        //==========================================
        // Sends HTML by rendering the specified template.
        //==========================================
        context.response().sendTemplateHtml("/templates/demos/htmlForms/multiple.html");
    }

    /**
     * Multiple Fields demo - POST
     */
    public void multipleFieldsSubmit(AppRequestContext context) {

        //==========================================
        // Gets the form data as a Form object,
        // and adds it to the response's model.
        // The validation message will be added to the
        // default "validation" model element.
        //==========================================
        Form form = context.request().getForm("demoForm");
        context.response().addForm(form);

        //==========================================
        // Validates the form
        //==========================================
        validateForm(form);

        //==========================================
        // If there are validation errors, we
        // display the form again. We do so
        // simply by calling the GET handler.
        //==========================================
        if (!form.isValid() || "stay".equals(form.getString("action"))) {
            multipleFields(context);

        //==========================================@formatter:off 
        // If the form is valid, we redirect to a new
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
        // Trims all String fields.
        //==========================================
        form.trimAll();

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
        // if the previous one is valid.
        //==========================================
        if (form.isValid("email") && !form.validators().isEmailValid(email)) {
            form.addError("email",
                          "email_invalid",
                          "The email is invalid");
        }

        String emailAgain = form.getString("emailAgain");
        if (StringUtils.isBlank(email)) {
            form.addError("emailAgain",
                          "emailAgain_empty",
                          "Please enter the email again");
        }

        if (form.isValid("emailAgain") && !emailAgain.equals(email)) {
            form.addError("emailAgain",
                          "emailAgain_mustMatch",
                          "Must match the first email field.");
        }

        //==========================================
        // Validates that tags are not blank
        //==========================================
        JsonArray tags = form.getJsonArray("tags");
        if (tags.size() < 1) {
            form.addError("tags",
                          "tags_minSize",
                          "Please specify the tags.");
        }
        if (form.isValid("tags")) {

            boolean atLeastOneTagError = false;
            for (int i = 0; i < tags.size(); i++) {

                String tag = tags.getString(i);
                String tagValidationKey = "tags[" + i + "]";
                if (StringUtils.isBlank(tag)) {
                    form.addError(tagValidationKey, "tag_empty", "Please specify the tag.");
                }
                atLeastOneTagError = atLeastOneTagError || form.isError(tagValidationKey);
            }

            //==========================================
            // If there is at least one invalid tag, 
            // we add an error *on the tags array itself*.
            //==========================================
            if (atLeastOneTagError) {
                form.addError("tags", "tags_someInvalid", "Some tags are invalid.");
            }
        }

        //==========================================
        // Validates the "Action on submit"
        //==========================================
        String action = form.getString("action");
        if ("error".equals(action)) {
            form.addError("action",
                          "action_invalid",
                          "This option is not valid!");
        }
        if ("warning".equals(action)) {
            form.addWarning("action",
                            "action_warning",
                            "This option is a warning...");
        }

        //==========================================
        // A drink must be checked
        //==========================================
        String drink = form.getString("drink");
        if (StringUtils.isBlank(drink)) {
            form.addError("drink",
                          "drink_empty",
                          "Please choose a drink!");
        }

        //==========================================
        // "beer" is invalid as a favorite drink if 
        // the "Action on submit" is a warning...
        //
        // This is an example of a custom validation
        // where we add a Validation Message
        // by ourself on the Validation Set.
        //==========================================
        if (form.isValid("drink") && "beer".equals(drink) && form.isWarning("action")) {
            form.addError("drink",
                          "beer_and_warning_action",
                          "'Beer' is invalid if the 'Action on submit' field is a Warning!");
        }

        //==========================================
        // Two numbers must be selected.
        //
        // We ignore null values
        //==========================================
        List<Integer> validNumbers = new ArrayList<Integer>();
        JsonArray numbers = form.getJsonArrayOrEmpty("numbers");
        for (int i = 0; i < numbers.size(); i++) {
            Integer num = numbers.getInteger(i);
            if (num != null) {
                validNumbers.add(num);
            }
        }
        if (validNumbers.size() != 2) {
            form.addError("numbers",
                          "numbers_nbrRequired",
                          "Please select exactly 2 numbers.");
        } else {
            //==========================================
            // Both numbers must be odd or both even.
            //==========================================
            if ((validNumbers.get(0) + validNumbers.get(1)) % 2 != 0) {
                form.addError("numbers",
                              "ODD_OR_EVEN",
                              "Both numbers must be odd or both even.");
            }
        }

        //==========================================
        // Music styles - no validation
        //==========================================
        @SuppressWarnings("unused")
        JsonArray musicStyles = form.getJsonArray("musicStyles");

        //==========================================
        // Validates the acceptation of the TOS,
        // which is a "boolean checkbox".
        //==========================================
        boolean acceptTos = form.getBoolean("acceptTos", false);
        if (!acceptTos) {
            form.addError("acceptTos",
                          "acceptTos_false",
                          "You need to check this");
        }
    }
}
