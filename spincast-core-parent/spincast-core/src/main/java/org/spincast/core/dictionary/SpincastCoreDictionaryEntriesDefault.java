package org.spincast.core.dictionary;

import java.util.Map;

import com.google.inject.Inject;

/**
 * Provides a default english value for the messages that are 
 * <em>required</em> in any Spincast application. 
 * <p>
 * If you do not use the default 
 * {@link Dictionary} implementation, your have to provide a
 * value for the messages specified here!
 * 
 */
public class SpincastCoreDictionaryEntriesDefault extends DictionaryBase implements SpincastCoreDictionaryEntries {

    public static final String MESSAGE_KEY_ROUTE_NOT_FOUND_DEFAULTMESSAGE =
            SpincastCoreDictionaryEntriesDefault.class.getName() + ".route.notFound.defaultMessage";

    public static final String MESSAGE_KEY_EXCEPTION_DEFAULTMESSAGE =
            SpincastCoreDictionaryEntriesDefault.class.getName() + ".exception.defaultMessage";

    public static final String MESSAGE_KEY_VALIDATION_GENERICERROR_DEFAULTTEXT =
            SpincastCoreDictionaryEntriesDefault.class.getName() + ".validation.genericError.defaultText";


    @Inject
    public void init() {

        key(MESSAGE_KEY_ROUTE_NOT_FOUND_DEFAULTMESSAGE,
            msg("", "Not found"));

        key(MESSAGE_KEY_EXCEPTION_DEFAULTMESSAGE,
            msg("", "Internal Error"));

        key(MESSAGE_KEY_VALIDATION_GENERICERROR_DEFAULTTEXT,
            msg("", "Invalid value"));
    }

    @Override
    public Map<String, Map<String, String>> getDictionaryEntries() {
        return getMessages();
    }
}
