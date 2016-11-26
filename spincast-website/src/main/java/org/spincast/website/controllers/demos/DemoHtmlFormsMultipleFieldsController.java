package org.spincast.website.controllers.demos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.json.JsonArray;
import org.spincast.core.json.JsonManager;
import org.spincast.core.json.JsonObject;
import org.spincast.core.session.FlashMessageLevel;
import org.spincast.core.validation.JsonObjectValidationSet;
import org.spincast.core.validation.ValidationFactory;
import org.spincast.core.validation.ValidationSet;
import org.spincast.website.exchange.AppRequestContext;

import com.google.inject.Inject;

/**
 * HTML Forms  - Multiple Fields demo controller
 */
public class DemoHtmlFormsMultipleFieldsController {

    protected final Logger logger = LoggerFactory.getLogger(DemoHtmlFormsMultipleFieldsController.class);

    private final ValidationFactory validationFactory;
    private final JsonManager jsonManager;

    @Inject
    public DemoHtmlFormsMultipleFieldsController(ValidationFactory validationFactory,
                                                 JsonManager jsonManager) {
        this.validationFactory = validationFactory;
        this.jsonManager = jsonManager;
    }

    protected ValidationFactory getValidationFactory() {
        return this.validationFactory;
    }

    protected JsonManager getJsonManager() {
        return this.jsonManager;
    }

    protected void sendTemplate(AppRequestContext context) {
        context.response().sendTemplateHtml("/templates/demos/htmlForms/multiple.html");
    }

    /**
     * Multiple Fields demo - GET
     */
    public void multipleFields(AppRequestContext context) {
        sendTemplate(context);
    }

    /**
     * Multiple Fields demo - POST
     */
    public void multipleFieldsSubmit(AppRequestContext context) {

        //==========================================
        // Gets the form's data.
        // We make it mutable to we can modify the values
        // of its elements (we're going to trim them!)
        //==========================================
        JsonObject form = context.request().getFormData().getJsonObject("demoForm").clone(true);

        //==========================================
        // We validate the form and we prefix the 
        // generated validation keys with "demoForm." 
        // so they match as much as possible the JsonPath 
        // of their associated validated element!
        //==========================================
        ValidationSet validationResult = validateForm(form).prefixValidationKeys("demoForm.");

        //==========================================
        // If there are validation errors, we add the
        // validation results to the response's model and
        // display the form again.
        //==========================================
        if(!validationResult.isValid() || "stay".equals(form.getString("action"))) {

            //==========================================
            // We add the form object back to the response
            // model.
            //==========================================
            context.response().getModel().put("demoForm", form);

            //==========================================
            // We add the Validation Set to the response model too, 
            // using a "validation" key to scope it.
            //==========================================
            context.response().getModel().put("validation", validationResult);

            sendTemplate(context);

        //==========================================@formatter:off 
        // If the form is valid, we redirect to a new
        // page with a "success" Flash Message.
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
        // We get a Validation Set associated with the
        // form object.
        //==========================================
        JsonObjectValidationSet validationSet = form.validationSet();

        //==========================================
        // Trims all String fields.
        // We can do this because we got a *mutable*
        // version of the form data.
        //==========================================
        form.trimAll();

        //==========================================
        // Validates the email field
        //==========================================
        ValidationSet lastResult =
                validationSet.validationNotBlank().jsonPath("email").failMessageText("Please enter the email").validate();

        if(lastResult.isValid()) {
            validationSet.validationEmail().jsonPath("email").failMessageText("Please enter a valid email").validate();
        }

        lastResult = validationSet.validationNotBlank().jsonPath("emailAgain")
                                  .failMessageText("Please enter the email again").validate();

        if(lastResult.isValid()) {
            lastResult = validationSet.validationEquivalent(form.getString("email"))
                                      .jsonPath("emailAgain")
                                      .failMessageText("Must match the first email field.")
                                      .validate();
        }

        //==========================================
        // Validates that tags are not blank
        //
        // Notice how this is going to validate *all*
        // the tags, in a single validation.
        //
        // Also notice that we add an Error Validation Message
        // *on the array itself*, if at least one of the tag is
        // invalid!
        //==========================================
        lastResult = validationSet.validationNotBlank()
                                  .jsonPathAll("tags")
                                  .failMessageText("Please specify the tag.")
                                  .arrayItselfAddFailMessage("Some tags are invalid.")
                                  .validate();

        //==========================================
        // Validates the "Action on submit"
        //==========================================
        lastResult = validationSet.validationNotEquivalent("error")
                                  .jsonPath("action")
                                  .failMessageText("This option is not valid!")
                                  .validate();

        lastResult = validationSet.validationNotEquivalent("warning")
                                  .jsonPath("action")
                                  .treatErrorAsWarning()
                                  .failMessageText("This option is a warning...")
                                  .validate();

        //==========================================
        // A drink must be checked
        //==========================================
        lastResult = validationSet.validationNotBlank()
                                  .jsonPath("drink")
                                  .failMessageText("Please choose a drink!")
                                  .validate();

        //==========================================
        // "beer" is invalid as a favorite drink if 
        // the "Action on submit" is a warning...
        //
        // This is an example of a custom validation
        // where we add a Validation Message
        // by ourself on the Validation Set.
        //==========================================
        if(lastResult.isSuccess()) {
            if("beer".equals(form.getString("drink")) && validationSet.isWarning("action")) {
                validationSet.addError("drink",
                                       "BEER_AND_WARNING_ACTION",
                                       "'Beer' is invalid if the 'Action on submit' field is a Warning!");
            }
        }

        //==========================================
        // Two numbers must be selected.
        //
        // The "true" parameter means that the size validation
        // will ignore null values when counting.
        //==========================================
        lastResult = validationSet.validationSize(2, true)
                                  .jsonPath("numbers")
                                  .failMessageText("Please select exactly 2 numbers.")
                                  .validate();

        //==========================================
        // Both numbers must be odd or both even.
        //==========================================
        if(lastResult.isSuccess()) {

            JsonArray numbers = form.getJsonArray("numbers");

            Boolean num1Even = null;
            for(int i = 0; i < numbers.size(); i++) {
                Integer num = numbers.getInteger(i);
                if(num != null) {
                    if(num1Even == null) {
                        num1Even = num % 2 == 0;
                    } else {
                        boolean num2Even = num % 2 == 0;
                        if(!num1Even.equals(num2Even)) {
                            validationSet.addError("numbers",
                                                   "ODD_OR_EVEN",
                                                   "Both numbers must be odd or both even.");
                            break;
                        }
                    }
                }
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
        lastResult = validationSet.validationEquivalent(true)
                                  .jsonPath("acceptTos")
                                  .failMessageText("You need to check this")
                                  .validate();

        return validationSet;
    }
}
