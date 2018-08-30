package org.spincast.plugins.formsprotection.csrf;

import org.spincast.core.exchange.RequestContext;
import org.spincast.plugins.formsprotection.exceptions.FormInvalidCsrfTokenException;
import org.spincast.plugins.formsprotection.exceptions.FormInvalidOriginException;

/**
 * The Forms CSRF Protection Filter.
 * <p>
 * You should add this filter with the
 * "skipResourcesRequests()" options so it
 * is ignored except for main routes.
 */
public interface SpincastFormsCsrfProtectionFilter {

    /**
     * Filter's handle main method.
     * 
     * @throws FormInvalidOriginException if the form was  submitted
     * from an invalid orgine.
     * 
     * @throws FormInvalidCsrfTokenException  if the form was  submitted
     * with an invalid CRSF token.
     */
    public void handle(RequestContext<?> context) throws FormInvalidOriginException, FormInvalidCsrfTokenException;

    /**
     * Returns the current CSRF token to use 
     * Will be taken from the user session by default.
     * <p>
     * If there is none, a new one is created and save 
     * in the user's session! This will make the session
     * being dirty and saved to the database.
     */
    public SpincastCsrfToken getCurrentCsrfToken();

}
