package org.spincast.plugins.formsprotection.dictionary;

import java.util.Map;

import org.spincast.core.dictionary.DictionaryBase;
import org.spincast.core.dictionary.DictionaryEntries;

import com.google.inject.Inject;

/**
 * The Dictionary entries required by the Forms Protection plugin.
 */
public class SpincastFormsProtectionPluginDictionaryEntries extends DictionaryBase implements DictionaryEntries {

    /**
     * Public exception message when the submitted csrf token is not 
     * of a valid format.
     */
    public static final String MESSAGE_KEY_FORM_INVALID_PROTECTION_ID =
            SpincastFormsProtectionPluginDictionaryEntries.class.getName() + ".form.invalidToken";

    /**
     * Public exception message when the submitted form is too old.
     */
    public static final String MESSAGE_KEY_FORM_TOO_OLD =
            SpincastFormsProtectionPluginDictionaryEntries.class.getName() + ".form.tooOld";

    /**
     * Public exception message when the form was already submitted.
     */
    public static final String MESSAGE_KEY_FORM_ALREADY_SUBMITTED =
            SpincastFormsProtectionPluginDictionaryEntries.class.getName() + ".form.alreadySubmitted";

    /**
     * Public exception message when the submitted form is from an invalid
     * origin.
     */
    public static final String MESSAGE_KEY_FORM_INVALID_ORGIN =
            SpincastFormsProtectionPluginDictionaryEntries.class.getName() + ".form.invalidOrigin";

    /**
     * Public exception message when the submitted form doesn't have
     * a CSRF token.
     */
    public static final String MESSAGE_KEY_FORM_NO_CSRF_TOKEN_PROVIDED =
            SpincastFormsProtectionPluginDictionaryEntries.class.getName() + ".form.noCsrfToken";

    /**
     * Public exception message when the form was submitted with
     * an invalid CSRF token.
     */
    public static final String MESSAGE_KEY_FORM_INVALID_CSRF_TOKEN =
            SpincastFormsProtectionPluginDictionaryEntries.class.getName() + ".form.invalidCsrfToken";

    /**
     * Public exception message when a POST/PUT/etc. request is made
     * but the CSRF token is not found in the current session...
     */
    public static final String MESSAGE_KEY_FORM_CSRF_TOKEN_NOT_FOUND_IN_SESSION =
            SpincastFormsProtectionPluginDictionaryEntries.class.getName() + ".form.csrfTokenNotFoundInSession";

    @Inject
    public void init() {

        key(MESSAGE_KEY_FORM_INVALID_PROTECTION_ID,
            msg("", "The submitted form protection id is invalid."));

        key(MESSAGE_KEY_FORM_TOO_OLD,
            msg("", "The submitted form is too old. Please submit it again."));

        key(MESSAGE_KEY_FORM_ALREADY_SUBMITTED,
            msg("", "This form was already submitted. Please try again using fresh data."));

        key(MESSAGE_KEY_FORM_INVALID_ORGIN,
            msg("", "This form was submitted from an invalid origin."));

        key(MESSAGE_KEY_FORM_NO_CSRF_TOKEN_PROVIDED,
            msg("", "This form was submitted without a CSRF protection token."));

        key(MESSAGE_KEY_FORM_INVALID_CSRF_TOKEN,
            msg("", "This form was submitted with an invalid CSRF token."));

        key(MESSAGE_KEY_FORM_CSRF_TOKEN_NOT_FOUND_IN_SESSION,
            msg("",
                "This form was submitted with an invalid CSRF token. " +
                    "Maybe your session is expired? Please try again."));

    }

    @Override
    public Map<String, Map<String, String>> getDictionaryEntries() {
        return getMessages();
    }

}
