package org.spincast.core.validation;

import org.spincast.core.json.JsonObject;
import org.spincast.core.json.ToJsonObjectConvertible;

/**
 * A validation message.
 */
public interface ValidationMessage extends ToJsonObjectConvertible {

    /**
     * The validation level of thr message : 
     * ERROR, WARNING or SUCCESS.
     */
    public ValidationLevel getValidationLevel();

    /**
     * Returns <code>true</code> if the message level
     * is WARNING.
     */
    public boolean isWarning();

    /**
     * Returns <code>true</code> if the message level
     * is SUCCESS.
     */
    public boolean isSuccess();

    /**
     * Returns <code>true</code> if the message level
     * is ERROR.
     */
    public boolean isError();

    /**
     * The validation code.
     */
    public String getCode();

    /**
     * The text of the message.
     */
    public String getText();

    /**
     * How should the text be escaped when displayed in
     * HTML?
     */
    public ValidationHtmlEscapeType getHtmlEscapeType();

    /**
     * Converts the message to a <code>JsonObject</code> object.
     * <p>
     * The resulting <code>JsonObject</code> object 
     * is <em>immutable</em>.
     */
    @Override
    public JsonObject convertToJsonObject();

}
