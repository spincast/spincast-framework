package org.spincast.core.exceptions;

/**
 * Exception that will immediately send redirection headers and will skip
 * any remaining handlers.
 */
public class RedirectException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String newUrl;
    private final boolean redirectPermanently;

    /**
     * @param newUrl The new route to redirect to. This can be a
     * full URL or a relative path (+ potential queryString)
     * 
     * @param redirectPermanently Is this a temporary or permanent redirection?
     */
    public RedirectException(String newUrl, boolean redirectPermanently) {
        super();
        this.newUrl = newUrl;
        this.redirectPermanently = redirectPermanently;
    }

    /**
     * The new route to redirect to. This can be a
     * full URL or a relative path (+ potential queryString)
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

}
