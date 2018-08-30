package org.spincast.core.flash;

import javax.annotation.Nullable;

import org.spincast.core.json.JsonManager;
import org.spincast.core.json.JsonObject;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class FlashMessageDefault implements FlashMessage {

    private final JsonManager jsonManager;
    private final FlashMessageLevel messageType;
    private final String text;
    private final JsonObject variables;

    @AssistedInject
    public FlashMessageDefault(@Assisted FlashMessageLevel messageType,
                               @Assisted String text,
                               JsonManager jsonManager) {
        this(messageType, text, null, jsonManager);
    }

    @AssistedInject
    public FlashMessageDefault(@Assisted FlashMessageLevel messageType,
                               @Assisted String text,
                               @Assisted @Nullable JsonObject variables,
                               JsonManager jsonManager) {
        this.messageType = messageType;
        this.text = text;

        if(variables == null) {
            variables = jsonManager.create();
        }
        this.variables = variables;

        this.jsonManager = jsonManager;
    }

    protected JsonManager getJsonManager() {
        return this.jsonManager;
    }

    @Override
    public FlashMessageLevel getFlashType() {
        return this.messageType;
    }

    @Override
    public String getText() {
        return this.text;
    }

    @Override
    public JsonObject getVariables() {
        return this.variables;
    }

}
