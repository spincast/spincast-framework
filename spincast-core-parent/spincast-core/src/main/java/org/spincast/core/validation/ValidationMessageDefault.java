package org.spincast.core.validation;

import org.spincast.core.json.JsonManager;
import org.spincast.core.json.JsonObject;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * Validation message implementation.
 */
public class ValidationMessageDefault implements ValidationMessage {

    private final JsonManager jsonManager;
    private final ValidationLevel validationLevel;
    private final String code;
    private final String text;
    private final ValidationHtmlEscapeType htmlEscapeType;
    private JsonObject jsonObjectVersion;

    @AssistedInject
    public ValidationMessageDefault(@Assisted("validationLevel") ValidationLevel validationLevel,
                                    @Assisted("code") String code,
                                    @Assisted("text") String text,
                                    @Assisted("htmlEscapeType") ValidationHtmlEscapeType htmlEscapeType,
                                    JsonManager jsonManager) {
        this.validationLevel = validationLevel;
        this.code = code;
        this.text = text;
        this.htmlEscapeType = htmlEscapeType;
        this.jsonManager = jsonManager;
    }

    @Override
    public String getText() {
        return this.text;
    }

    @Override
    public ValidationHtmlEscapeType getHtmlEscapeType() {
        return this.htmlEscapeType;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public ValidationLevel getValidationLevel() {
        return this.validationLevel;
    }

    @Override
    public boolean isWarning() {
        return getValidationLevel() == ValidationLevel.WARNING;
    }

    @Override
    public boolean isSuccess() {
        return getValidationLevel() == ValidationLevel.SUCCESS;
    }

    @Override
    public boolean isError() {
        return getValidationLevel() == ValidationLevel.ERROR;
    }

    protected JsonManager getJsonManager() {
        return this.jsonManager;
    }

    @Override
    public JsonObject convertToJsonObject() {

        if (this.jsonObjectVersion == null) {

            JsonObject obj = getJsonManager().create();
            obj.set("code", getCode());
            obj.set("level", getValidationLevel().toString());
            obj.set("text", getText());
            obj.set("htmlEscapeType", getHtmlEscapeType().name());

            // Immutable
            this.jsonObjectVersion = obj.clone(false);
        }

        return this.jsonObjectVersion;
    }

    @Override
    public String toString() {
        return getCode() + " - " + getText();
    }

}
