package org.spincast.website.controllers.demos;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.flash.FlashMessageFactory;
import org.spincast.core.json.JsonArray;
import org.spincast.core.json.JsonManager;
import org.spincast.core.json.JsonObject;
import org.spincast.core.request.Form;
import org.spincast.core.response.AlertLevel;
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
    private final JsonManager jsonManager;
    private final ValidationFactory validationFactory;

    @Inject
    public DemoHtmlFormsDynamicFieldsController(FlashMessageFactory flashMessageFactory,
                                                JsonManager jsonManager,
                                                ValidationFactory validationFactory) {
        this.flashMessageFactory = flashMessageFactory;
        this.jsonManager = jsonManager;
        this.validationFactory = validationFactory;
    }

    protected FlashMessageFactory getFlashMessageFactory() {
        return this.flashMessageFactory;
    }

    protected JsonManager getJsonManager() {
        return this.jsonManager;
    }

    protected ValidationFactory getValidationFactory() {
        return this.validationFactory;
    }

    /**
     * Dynamic Fields demo - GET
     */
    public void dynamicFields(AppRequestContext context) {

        //==========================================
        // We check if the form already exists on
        // the response model. This may be the case
        // if the POST handler called this GET handler 
        // to redisplay the form.
        //
        // If the form doesn't exist yet, we create it.
        //==========================================
        JsonObject form = context.response().getModel().getJsonObject("demoForm");
        if (form == null) {
            form = context.json().create();
            context.response().getModel().set("demoForm", form);

            //==========================================
            // The initial users array
            //==========================================
            JsonArray users = context.json().createArray();
            form.set("users", users);

            JsonObject firstUser = createFirstUser();
            users.add(firstUser);
        }

        //==========================================
        // Sends HTML by rendering the specified template.
        //==========================================
        context.response().sendTemplateHtml("/templates/demos/htmlForms/dynamic.html");
    }

    /**
     * Creates a first default user
     */
    protected JsonObject createFirstUser() {

        JsonObject firstUser = getJsonManager().create();
        firstUser.set("name", "Stromgol");
        firstUser.set("city", "Amos");

        JsonArray firstUserTags = getJsonManager().createArray();
        firstUserTags.add("myTag1");
        firstUserTags.add("myTag2");
        firstUserTags.add("myTag3");
        firstUser.set("tags", firstUserTags);

        return firstUser;
    }

    /**
     * Dynamic Fields demo - POST
     */
    public void dynamicFieldsSubmit(AppRequestContext context) {

        //==========================================
        // Gets the form data as a Form object,
        // and adds it back to the response's model.
        // The validation message will be added to the
        // default "validation" element of the model.
        //==========================================
        Form form = context.request().getFormWithRootKey("demoForm");
        context.response().addForm(form);

        //==========================================
        // Adds a new user (when javascript is disabled)
        //==========================================
        boolean actionDone = false;
        if (context.request().getFormBodyAsJsonObject().isElementExists("addUserBtn")) {
            actionDone = true;

            JsonArray users = form.getJsonArrayOrEmpty("users");
            JsonObject newUser = context.json().create();
            newUser.set("name", "");
            newUser.set("city", "");
            users.add(newUser);

            JsonArray newUserTags = context.json().createArray();
            newUserTags.add("");
            newUser.set("tags", newUserTags);
        }

        //==========================================
        // Adds a new tag (when javascript is disabled)
        //==========================================
        if (!actionDone) {
            Integer addTagToUserPos = getUserToAddTagTo(context);
            if (addTagToUserPos != null) {
                actionDone = true;

                JsonObject user = form.getJsonObject("users[" + addTagToUserPos + "]");
                if (user != null) {

                    JsonArray tags = user.getJsonArray("tags");
                    if (tags == null) {
                        tags = context.json().createArray();
                        user.set("tags", tags);
                    }
                    tags.add("");
                }
            }
        }

        //==========================================
        // Otherwise, we validate the form!
        //==========================================
        if (!actionDone) {

            //==========================================
            // Quick check, in case one day we tweak the demo to be
            // able to *delete* users...
            //==========================================
            if (form.getJsonArrayOrEmpty("users").size() > 0) {
                validateForm(form);
            } else {
                context.response().addAlert(AlertLevel.WARNING, "No users to validate!");
            }
        }

        //==========================================
        // Redisplays the form, by calling the
        // GET handler.
        //==========================================
        dynamicFields(context);
    }

    /**
     * Checks if there is a User to add, server-side.
     */
    protected Integer getUserToAddTagTo(AppRequestContext context) {

        Integer userPositionToAddTagTo = null;
        for (Entry<String, Object> formDatas : context.request().getFormBodyAsJsonObject()) {

            String key = formDatas.getKey();
            if (key != null && key.startsWith("addTag_")) {

                try {
                    userPositionToAddTagTo = Integer.parseInt(key.substring("addTag_".length()));
                    break;
                } catch (Exception ex) {
                    // invalid, we do nothing
                }
            }
        }

        return userPositionToAddTagTo;
    }

    /**
     * Validation of the form. There is no need to return
     * anything : the validation results are saved in the
     * form itself.
     */
    protected void validateForm(Form form) {

        //==========================================
        // Trims all String fields.
        //==========================================
        form.trimAll();

        //==========================================
        // Gets all the users and validates them, one
        // by one.
        //==========================================
        JsonArray users = form.getJsonArrayOrEmpty("users");
        for (int i = 0; i < users.size(); i++) {
            JsonObject user = users.getJsonObject(i);

            //==========================================
            // The validation of a specific user returns 
            // a Validation set which we can merge into our
            // form!
            //==========================================
            ValidationSet userValidation = validateUser(user);
            form.mergeValidationSet("users[" + i + "].", userValidation);
        }
    }

    /**
     * Validation of a user.
     */
    protected ValidationSet validateUser(JsonObject user) {

        ValidationSet userValidation = getValidationFactory().createValidationSet();

        //==========================================
        // Validates the name
        //==========================================
        String name = user.getString("name");

        if (StringUtils.isBlank(name)) {
            userValidation.addError("name", "name_empty", "The name can't be empty");
        }

        if (userValidation.isValid("name") && name.length() < 3) {
            userValidation.addError("name", "name_minLength", "The name must be at least 3 characters.");
        }

        //==========================================
        // Validates the city
        //==========================================
        String city = user.getString("city");

        if (StringUtils.isBlank(city)) {
            userValidation.addError("city", "city_empty", "The city can't be empty");
        }

        if (userValidation.isValid("city") && city.length() < 4) {
            userValidation.addError("city", "city_minLength", "The city must be at least 3 characters.");
        }

        //==========================================
        // Validates the tags
        //==========================================
        JsonArray tags = user.getJsonArray("tags");
        if (tags.size() < 1) {
            userValidation.addError("tags",
                                    "tags_minSize",
                                    "At least one tag is required.");
        }
        if (userValidation.isValid("tags")) {

            //==========================================
            // We validate each tag and also meake
            // sure there are two identical tags.
            //==========================================
            Set<String> tagsSet = new HashSet<String>();
            boolean atLeastOneTagError = false;
            for (int i = 0; i < tags.size(); i++) {

                String tag = tags.getString(i);
                String tagLowercase = tag.toLowerCase();
                String tagValidationKey = "tags[" + i + "]";

                if (StringUtils.isBlank(tag)) {
                    userValidation.addError(tagValidationKey, "tag_empty", "A tag can't be empty");
                }

                if (userValidation.isValid(tagValidationKey) && tag.length() < 3) {
                    userValidation.addError(tagValidationKey, "tag_minLength", "A tag must be at least 3 characters.");
                }

                if (userValidation.isValid(tagValidationKey) && tag.length() > 10) {
                    userValidation.addError(tagValidationKey, "tag_maxLength", "A tag must be max 10 characters long.");
                }

                if (userValidation.isValid(tagValidationKey) && tagsSet.contains(tagLowercase)) {
                    userValidation.addError(tagValidationKey, "tag_duplicate", "This tag already exists.");
                }
                tagsSet.add(tagLowercase);

                atLeastOneTagError = atLeastOneTagError || userValidation.isError(tagValidationKey);
            }

            //==========================================
            // If there is at least one invalid tag, 
            // we add an error *on the tags array itself*.
            //==========================================
            if (atLeastOneTagError) {
                userValidation.addError("tags", "tags_someInvalid", "At least one tag is invalid.");
            }
        }

        return userValidation;
    }
}
