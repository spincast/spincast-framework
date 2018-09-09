package org.spincast.plugins.dictionary;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.dictionary.Dictionary;
import org.spincast.core.dictionary.DictionaryBase;
import org.spincast.core.dictionary.DictionaryEntries;
import org.spincast.core.dictionary.DictionaryEntryNotFoundBehavior;
import org.spincast.core.dictionary.DictionaryKeyNotFoundException;
import org.spincast.core.locale.LocaleResolver;
import org.spincast.core.templating.TemplatingEngine;
import org.spincast.core.utils.Pair;

import com.google.inject.Inject;

/**
 * Default implementation of the {@link Dictionary}.
 * <p>
 * Provides a value for the core Spincast messages 
 * required by any application.
 * <p>
 * You have to override the {@link #addMessages()} method to
 * add your own messages.
 */
public class SpincastDictionaryDefault extends DictionaryBase implements Dictionary {

    protected final Logger logger = LoggerFactory.getLogger(SpincastDictionaryDefault.class);

    private final LocaleResolver localeResolver;
    private final TemplatingEngine templatingEngine;
    private final SpincastConfig spincastConfig;
    private final Set<DictionaryEntries> dictionaryEntries;

    private boolean messagesLoaded = false;

    @Inject
    public SpincastDictionaryDefault(LocaleResolver localeResolver,
                                     TemplatingEngine templatingEngine,
                                     SpincastConfig spincastConfig,
                                     @Nullable Set<DictionaryEntries> dictionaryEntries) {
        this.localeResolver = localeResolver;
        this.templatingEngine = templatingEngine;
        this.spincastConfig = spincastConfig;
        this.dictionaryEntries = dictionaryEntries;
    }

    protected Locale getDefaultLocale() {
        return this.localeResolver.getLocaleToUse();
    }

    protected TemplatingEngine getTemplatingEngine() {
        return this.templatingEngine;
    }

    protected SpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    protected Set<DictionaryEntries> getDictionaryEntries() {
        return this.dictionaryEntries;
    }

    /**
     * Gets a message.
     * 
     * Will use the proper Locale as provided by the 
     * {@link LocaleResolver}.
     */
    @Override
    public String get(String key) {
        return get(key, false);
    }

    @Override
    public String get(String key, Locale locale) {
        return get(key, locale, false);
    }

    /**
     * Gets a message.
     * 
     * Will use the proper Locale as provided by the 
     * {@link LocaleResolver}.
     */
    @Override
    public String get(String key, boolean forceTemplatingEngine) {
        return get(key, forceTemplatingEngine ? new HashMap<>() : null);
    }

    @Override
    public String get(String key, Locale locale, boolean forceEvaluation) {
        return get(key, locale, forceEvaluation ? new HashMap<>() : null);
    }

    /**
     * Gets a message.
     * 
     * Will use the proper Locale as provided by the 
     * {@link LocaleResolver}.
     */
    @Override
    public final String get(String key, Pair... params) {
        return get(key, null, params);
    }

    @Override
    public String get(String key, Locale locale, Pair... params) {

        Map<String, Object> paramsMap = new HashMap<String, Object>();
        if (params != null) {
            for (Pair param : params) {
                paramsMap.put(param.getKey(), param.getValue());
            }
        }

        return get(key, locale, paramsMap);
    }

    /**
     * Gets a message.
     * 
     * Will use the proper Locale as provided by the 
     * {@link LocaleResolver}.
     */
    @Override
    public String get(String key, Map<String, Object> params) {
        return get(key, null, params);
    }

    @Override
    public String get(String key, Locale locale, Map<String, Object> params) {
        return get(key, locale, params, locale);
    }

    public String get(String key, Locale locale, Map<String, Object> params, Locale originalLocale) {

        if (!this.messagesLoaded || getSpincastConfig().isDevelopmentMode()) {
            addCoreAndPluginsMessages();
            addMessages();
            this.messagesLoaded = true;
        }

        Locale localeToUse = locale != null ? locale : getDefaultLocale();

        Map<String, String> msgs = getMessages().get(key);
        if (msgs == null) {
            return keyNotFound(key, localeToUse, params);
        }

        String lang = localeToUse.getLanguage();

        String msg = null;
        if (msgs.containsKey(lang)) {
            msg = msgs.get(lang);
        } else {
            //==========================================
            // Tries the fallback Locale
            //==========================================
            if (msgs.containsKey("")) {
                msg = msgs.get("");
            } else {
                return keyNotFound(key, localeToUse, params);
            }
        }

        //==========================================
        // If the parameters are NULL we
        // skip the evaluation using the templating engine.
        //==========================================
        if (params == null) {
            return msg;
        }

        return getTemplatingEngine().evaluate(msg, params, localeToUse);
    }

    protected String keyNotFound(String key, Locale originalLocale, Map<String, Object> params) {

        DictionaryEntryNotFoundBehavior notFoundResult = getSpincastConfig().getDictionaryEntryNotFoundBehavior();

        if (!getSpincastConfig().isDevelopmentMode() && notFoundResult != DictionaryEntryNotFoundBehavior.EXCEPTION) {
            this.logger.error("A dictionary key is missing! Key \"" + key + "\" for Locale \"" + originalLocale + "\".");
        }

        if (notFoundResult == DictionaryEntryNotFoundBehavior.EXCEPTION) {
            throw new DictionaryKeyNotFoundException(key, originalLocale);
        } else if (notFoundResult == DictionaryEntryNotFoundBehavior.RETURN_KEY) {
            return key;
        } else if (notFoundResult == DictionaryEntryNotFoundBehavior.RETURN_EMPTY_STRING) {
            return "";
        } else {
            throw new RuntimeException("Not managed : " + notFoundResult);
        }
    }

    protected void addCoreAndPluginsMessages() {

        Set<DictionaryEntries> dictionaryEntries = getDictionaryEntries();
        if (dictionaryEntries == null) {
            return;
        }

        for (DictionaryEntries dictionaryEntriesOne : dictionaryEntries) {
            if (dictionaryEntriesOne == null || dictionaryEntriesOne.getDictionaryEntries() == null) {
                continue;
            }

            for (Entry<String, Map<String, String>> entry : dictionaryEntriesOne.getDictionaryEntries().entrySet()) {

                String messageKey = entry.getKey();
                Map<String, String> messagesPerLang = entry.getValue();
                if (messageKey == null || messagesPerLang == null) {
                    continue;
                }

                for (Entry<String, String> messagePerLangEntry : messagesPerLang.entrySet()) {
                    key(messageKey,
                        msg(messagePerLangEntry.getKey(), messagePerLangEntry.getValue()));
                }
            }
        }
    }

    /**
     * To override to add messages to the dictionary.
     * <p>
     * 
     * Example :
     * 
     * <code>
     * protected void addMessages() {
     *     super.addMessages();
     *     
     *     key("my.message.key",
     *         msg("en", "The message in english"),
     *         msg("fr", "Le message en français"));
     * }
     * </code>
     */
    protected void addMessages() {
        // To override to add messages....
    }


}
