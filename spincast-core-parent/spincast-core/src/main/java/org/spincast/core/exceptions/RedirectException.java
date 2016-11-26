package org.spincast.core.exceptions;

import org.spincast.core.json.JsonObject;
import org.spincast.core.session.FlashMessageLevel;
import org.spincast.core.session.FlashMessage;

/**
 * Exception that will immediately send redirection headers and will make
 * any remaining route handlers being skipped.
 */
public class RedirectException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String newUrl;
    private final boolean redirectPermanently;
    private final FlashMessage flashMessage;
    private final FlashMessageLevel flashMessageType;
    private final String flashMessageText;
    private final JsonObject flashMessageVariables;

    /**
     * Redirects to the current URL.
     */
    public RedirectException() {
        this("", false, null, null, null, null);
    }

    /**
     * Redirects to the current URL with 
     * a Flash message.
     */
    public RedirectException(FlashMessage flashMessage) {
        this("", false, flashMessage, null, null, null);
    }

    /**
     * Redirects to the current URL with 
     * a Flash message.
     */
    public RedirectException(FlashMessageLevel flashMessageType, String flashMessageTex) {
        this("", false, null, flashMessageType, flashMessageTex, null);
    }

    /**
     * Redirects to the current URL with 
     * a Flash message.
     */
    public RedirectException(FlashMessageLevel flashMessageType, String flashMessageTex, JsonObject flashMessageVariables) {
        this("", false, null, flashMessageType, flashMessageTex, flashMessageVariables);
    }

    /**
     * @param newUrl The new route to 
     * redirect to (not permanently). This can be a
     * full URL or a relative path (+ a potential queryString)
     */
    public RedirectException(String newUrl) {
        this(newUrl, false, null, null, null, null);
    }

    /**
     * Redirect with a Flash message.
     * 
     * @param newUrl The new route to redirect to (not permanently). 
     * This can be a
     * full URL or a relative path (+ a potential queryString)
     * 
     */
    public RedirectException(String newUrl, FlashMessage flashMessage) {
        this(newUrl, false, flashMessage, null, null, null);
    }

    /**
     * Redirect with a Flash message.
     * 
     * @param newUrl The new route to 
     * redirect to (not permanently). This can be a
     * full URL or a relative path (+ a potential queryString)
     */
    public RedirectException(String newUrl, FlashMessageLevel flashMessageType, String flashMessageTex) {
        this(newUrl, false, null, flashMessageType, flashMessageTex, null);
    }

    /**
     * Redirect with a Flash message.
     * 
     * @param newUrl The new route to 
     * redirect to (not permanently). This can be a
     * full URL or a relative path (+ a potential queryString)
     */
    public RedirectException(String newUrl, FlashMessageLevel flashMessageType, String flashMessageTex,
                             JsonObject flashMessageVariables) {
        this(newUrl, false, null, flashMessageType, flashMessageTex, flashMessageVariables);
    }

    /**
     * @param newUrl The new route to redirect to. This can be a
     * full URL or a relative path (+ a potential queryString)
     * 
     * @param redirectPermanently Is this a temporary or permanent redirection?
     */
    public RedirectException(String newUrl, boolean redirectPermanently) {
        this(newUrl, redirectPermanently, null, null, null, null);
    }

    /**
     * Redirect with a Flash message.
     * 
     * @param newUrl The new route to 
     * redirect to (not permanently). This can be a
     * full URL or a relative path (+ a potential queryString)
     * 
     * @param redirectPermanently Is this a temporary or permanent redirection?
     */
    public RedirectException(String newUrl, boolean redirectPermanently, FlashMessageLevel flashMessageType,
                             String flashMessageTex) {
        this(newUrl, redirectPermanently, null, flashMessageType, flashMessageTex, null);
    }

    /**
     * Redirect with a Flash message.
     * 
     * @param newUrl The new route to 
     * redirect to (not permanently). This can be a
     * full URL or a relative path (+ a potential queryString)
     * 
     * @param redirectPermanently Is this a temporary or permanent redirection?
     */
    public RedirectException(String newUrl, boolean redirectPermanently, FlashMessageLevel flashMessageType,
                             String flashMessageTex, JsonObject flashMessageVariables) {
        this(newUrl, redirectPermanently, null, flashMessageType, flashMessageTex, flashMessageVariables);
    }

    /**
     * @param newUrl The new route to redirect to. This can be a
     * full URL or a relative path (+ a potential queryString)
     * 
     * @param redirectPermanently Is this a temporary or permanent redirection?
     * @param flashMessage a Flash message to display on the target
     * url?
     */
    protected RedirectException(String newUrl,
                                boolean redirectPermanently,
                                FlashMessage flashMessage,
                                FlashMessageLevel flashMessageType,
                                String flashMessageText,
                                JsonObject flashMessageVariables) {
        super();
        this.newUrl = newUrl;
        this.redirectPermanently = redirectPermanently;
        this.flashMessage = flashMessage;
        this.flashMessageType = flashMessageType;
        this.flashMessageText = flashMessageText;
        this.flashMessageVariables = flashMessageVariables;
    }

    /**
     * The new route to redirect to. This can be a
     * full URL or a relative path (+ a potential queryString)
     */
    public String getNewUrl() {
        return this.newUrl;
    }

    /**
     * Is this a temporary or permanent redirection?
     */
    public boolean isRedirectPermanently() {
        return this.redirectPermanently;
    }

    /**
     * Can be null.
     */
    public FlashMessage getFlashMessage() {
        return this.flashMessage;
    }

    /**
     * Can be null.
     */
    public FlashMessageLevel getFlashMessageType() {
        return this.flashMessageType;
    }

    /**
     * Can be null.
     */
    public String getFlashMessageText() {
        return this.flashMessageText;
    }

    /**
     * Can be null.
     */
    public JsonObject getFlashMessageVariables() {
        return this.flashMessageVariables;
    }

}
