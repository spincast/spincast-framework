package org.spincast.website.controllers.demos;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.json.JsonArray;
import org.spincast.core.json.JsonManager;
import org.spincast.core.json.JsonObject;
import org.spincast.core.response.AlertLevel;
import org.spincast.core.session.FlashMessageFactory;
import org.spincast.core.validation.JsonObjectValidationSet;
import org.spincast.core.validation.ValidationFactory;
import org.spincast.core.validation.ValidationSet;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.website.exchange.AppRequestContext;

import com.google.inject.Inject;

/**
 * HTML Forms - Dynamic Fields demo controller
 */
public class DemoHtmlFormsDynamicFieldsController {

    protected final Logger logger = LoggerFactory.getLogger(DemoHtmlFormsDynamicFieldsController.class);

    private final FlashMessageFactory flashMessageFactory;
    private final ValidationFactory validationFactory;
    private final JsonManager jsonManager;

    @Inject
    public DemoHtmlFormsDynamicFieldsController(FlashMessageFactory flashMessageFactory,
                                                ValidationFactory validationFactory,
                                                JsonManager jsonManager) {
        this.flashMessageFactory = flashMessageFactory;
        this.validationFactory = validationFactory;
        this.jsonManager = jsonManager;
    }

    protected FlashMessageFactory getFlashMessageFactory() {
        return this.flashMessageFactory;
    }

    protected ValidationFactory getValidationFactory() {
        return this.validationFactory;
    }

    protected JsonManager getJsonManager() {
        return this.jsonManager;
    }

    protected void sendTemplate(AppRequestContext context) {
        context.response().sendTemplateHtml("/templates/demos/htmlForms/dynamic.html");
    }

    /**
     * Dynamic Fields demo - GET
     */
    public void dynamicFields(AppRequestContext context) {

        JsonObject form = context.json().create();

        //==========================================
        // The users array
        //==========================================
        JsonArray users = context.json().createArray();
        form.put("users", users);

        //==========================================
        // A first user
        //==========================================
        JsonObject firstUser = createFirstUser();
        users.add(firstUser);

        //==========================================
        // We adds the form model to the response model
        //==========================================
        context.response().getModel().put("demoForm", form);

        sendTemplate(context);
    }

    /**
     * Creates a first default user
     */
    protected JsonObject createFirstUser() {

        JsonObject firstUser = getJsonManager().create();
        firstUser.put("name", "Stromgol");
        firstUser.put("city", "Amos");

        JsonArray firstUserTags = getJsonManager().createArray();
        firstUserTags.add("myTag1");
        firstUserTags.add("myTag2");
        firstUserTags.add("myTag3");
        firstUser.put("tags", firstUserTags);

        return firstUser;
    }

    /**
     * Dynamic Fields demo - POST
     */
    public void dynamicFieldsSubmit(AppRequestContext context) {

        //==========================================
        // Gets the form's data.
        // We make it mutable to we can modify the values
        // of its elements (we're going to trim them!)
        //==========================================
        JsonObject form = context.request().getFormData()
                                 .getJsonObjectOrEmpty("demoForm")
                                 .clone(true);

        //==========================================
        // We add the form back to the response model
        // so it can be redisplayed.
        //==========================================
        context.response().getModel().put("demoForm", form);

        //==========================================
        // Adds a new user (when javascript is disabled)
        //==========================================
        boolean actionDone = false;
        if(context.request().getFormData().isElementExists("addUserBtn")) {
            actionDone = true;

            JsonArray users = form.getJsonArrayOrEmpty("users");
            JsonObject newUser = context.json().create();
            newUser.put("name", "");
            newUser.put("city", "");
            users.add(newUser);

            JsonArray newUserTags = context.json().createArray();
            newUserTags.add("");
            newUser.put("tags", newUserTags);
        }

        //==========================================
        // Adds a new tag (when javascript is disabled)
        //==========================================
        if(!actionDone) {
            Integer addTagToUserPos = getUserToAddTagTo(context);
            if(addTagToUserPos != null) {
                actionDone = true;

                JsonObject user = form.getJsonObject("users[" + addTagToUserPos + "]");
                if(user != null) {

                    JsonArray tags = user.getJsonArray("tags");
                    if(tags == null) {
                        tags = context.json().createArray();
                        user.put("tags", tags);
                    }
                    tags.add("");
                }
            }
        }

        //==========================================
        // Otherwise, we validate the form!
        //==========================================
        if(!actionDone) {

            //==========================================
            // Quick check, in case one day we tweak the demo to be
            // able to *delete* users...
            //==========================================
            if(form.getJsonArrayOrEmpty("users").size() > 0) {
                ValidationSet validationResult = validateForm(form);

                //==========================================
                // Our dynamic form uses the exact JsonPath of an
                // element as its validation key inside the following
                // "validation" object : we need to prefix the keys
                // of the current ValidationSet with "demoForm." for those
                // keys to match the JsonPath of their associated element!
                //==========================================
                validationResult.prefixValidationKeys("demoForm.");

                context.response().getModel().putNoKeyParsing("validation", validationResult);
            } else {
                context.response().addAlert(AlertLevel.WARNING, "No users to validate!");
            }
        }

        sendTemplate(context);
    }

    /**
     * Checks if there is a Us er to add, server-side.
     */
    protected Integer getUserToAddTagTo(AppRequestContext context) {

        Integer userPositionToAddTagTo = null;
        for(Entry<String, Object> formDatas : context.request().getFormData()) {

            String key = formDatas.getKey();
            if(key != null && key.startsWith("addTag_")) {

                try {
                    userPositionToAddTagTo = Integer.parseInt(key.substring("addTag_".length()));
                    break;
                } catch(Exception ex) {
                    // invalid, we do nothing
                }
            }
        }

        return userPositionToAddTagTo;
    }

    /**
     * Validation of the form
     */
    protected ValidationSet validateForm(JsonObject form) {

        //==========================================
        // We create a Validation Set associated with
        // the form object.
        //==========================================
        JsonObjectValidationSet demoFormValidation = form.validationSet();

        //==========================================
        // Trims all String fields.
        // We can do this because we got a *mutable*
        // version of the form data.
        //==========================================
        form.trimAll();

        //==========================================
        // Gets all the user ans validates them, one
        // by one.
        //==========================================
        JsonArray users = form.getJsonArrayOrEmpty("users");
        for(int i = 0; i < users.size(); i++) {
            JsonObject user = users.getJsonObject(i);

            //==========================================
            // The validation of a specific user returns 
            // a Validation Set which we merge into our
            // main Validation Set!
            //==========================================
            ValidationSet userValidation = validateUser(user);
            demoFormValidation.mergeValidationSet("users[" + i + "].", userValidation);
        }

        return demoFormValidation;
    }

    /**
     * Validation of a user.
     */
    protected ValidationSet validateUser(JsonObject user) {

        JsonObjectValidationSet userValidation = user.validationSet();

        //==========================================
        // Validates the name
        //==========================================
        ValidationSet lastResult = userValidation.validationNotBlank().jsonPath("name").validate();
        if(lastResult.isValid()) {
            userValidation.validationMinLength(3).jsonPath("name").validate();
        }

        //==========================================
        // Validates the city
        //==========================================
        lastResult = userValidation.validationNotBlank().jsonPath("city").validate();
        if(lastResult.isValid()) {
            userValidation.validationMinLength(4).jsonPath("city").validate();
        }

        //==========================================
        // Validates the tags
        //==========================================
        lastResult = userValidation.validationMinSize(1, false).jsonPath("tags").validate();
        if(lastResult.isValid()) {

            boolean atLeastOneTagError = false;

            lastResult = userValidation.validationNotBlank().jsonPathAll("tags").validate();
            atLeastOneTagError = atLeastOneTagError || lastResult.isError();

            //==========================================
            // Min 3 characters
            //
            // "validate(true)" => The validation is run only
            // if there are no errors yet!
            //==========================================
            lastResult = userValidation.validationMinLength(3).jsonPathAll("tags").validate(true);
            atLeastOneTagError = atLeastOneTagError || lastResult.isError();

            //==========================================
            // Max 10 characters
            //
            // "validate(true)" => The validation is run only
            // if there are no errors yet!
            //==========================================
            lastResult = userValidation.validationMaxLength(10).jsonPathAll("tags").validate(true);
            atLeastOneTagError = atLeastOneTagError || lastResult.isError();

            //==========================================
            // Not two identical tags
            //==========================================
            Set<String> tagsSet = new HashSet<String>();
            JsonArray tagsArray = user.getJsonArrayOrEmpty("tags");
            for(int i = 0; i < tagsArray.size(); i++) {
                String tag = tagsArray.getString(i, "");
                if(tag != null) {
                    tag = tag.toLowerCase();
                    if(!StringUtils.isBlank(tag) && tagsSet.contains(tag)) {

                        //==========================================
                        // We add an Error Validation Message manually
                        //==========================================
                        lastResult = userValidation.addError("tags[" + i + "]",
                                                             "DUPLICATE_TAG",
                                                             "This tag already exists.");
                        atLeastOneTagError = true;
                        continue;
                    }
                    tagsSet.add(tag);
                }
            }

            //==========================================
            // If there is at least one invalid tag, 
            // we add an error *on the array itself*.
            //==========================================
            if(atLeastOneTagError) {
                lastResult = userValidation.addError("tags",
                                                     "CONTAINS_INVALID_TAGS",
                                                     "At least one tag is invalid.");
            }
        }

        return userValidation;
    }
}
