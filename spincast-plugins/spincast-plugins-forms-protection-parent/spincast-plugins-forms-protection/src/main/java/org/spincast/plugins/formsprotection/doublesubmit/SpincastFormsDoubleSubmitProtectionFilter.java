package org.spincast.plugins.formsprotection.doublesubmit;

import java.util.Dictionary;

import org.spincast.core.exceptions.PublicException;
import org.spincast.core.exchange.RequestContext;
import org.spincast.plugins.formsprotection.exceptions.FormAlreadySubmittedException;
import org.spincast.plugins.formsprotection.exceptions.FormTooOldException;

/**
 * The Forms Double Submit Protection Filter.
 * <p>
 * You should add this filter with the
 * "skipResourcesRequests()" options so it
 * is ignored except for main routes.
 */
public interface SpincastFormsDoubleSubmitProtectionFilter {

    /**
     * Creates an id to use in a form that needs to be
     * protected.
     */
    public String createNewFormDoubleSubmitProtectionId();

    /**
     * Filter's handle main method.
     * 
     * @throws FormAlreadySubmittedException if the form was already submitted. This
     * exception, by default, implements {@link PublicException} and contains a
     * message from the {@link Dictionary} to be displayed to the user.
     * 
     * @throws FormTooOldException if the submitted form is too old. This
     * exception, by default, implements {@link PublicException} and contains a
     * message from the {@link Dictionary} to be displayed to the user.
     */
    public void handle(RequestContext<?> context) throws FormAlreadySubmittedException, FormTooOldException;

}
