package org.spincast.core.dictionary;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.spincast.core.config.SpincastConfig;
import org.spincast.core.locale.LocaleResolver;
import org.spincast.core.templating.TemplatingEngine;
import org.spincast.core.utils.Pair;

/**
 * The entity to get a localized message given a
 * specified key.
 */
public interface Dictionary {

    /**
     * Gets a message.
     * <p>
     * Will use the proper Locale as provided by the
     * {@link LocaleResolver}.
     * <p>
     * The behavior if the key is not found can be configured using
     * {@link SpincastConfig#getDictionaryEntryNotFoundFallbackTo()} and
     * {@link SpincastConfig#getDictionaryEntryNotFoundBehavior()}.
     */
    public String get(String key);

    /**
     * Gets a message.
     * <p>
     * Uses the specified Locale.
     * <p>
     * The behavior if the key is not found can be configured using
     * {@link SpincastConfig#getDictionaryEntryNotFoundFallbackTo()} and
     * {@link SpincastConfig#getDictionaryEntryNotFoundBehavior()}.
     */
    public String get(String key, Locale locale);

    /**
     * Gets a message.
     * <p>
     * Will use the proper Locale as provided by the
     * {@link LocaleResolver}.
     * <p>
     * @param forceEvaluation if <code>true</code>,
     * the {@link TemplatingEngine} will be used even there
     * are no parameters. This may be useful to trigger some
     * templating function that don't need parameters. We don't
     * use the templting engine will default since it is costy.
     * <p>
     * The behavior if the key is not found can be configured using
     * {@link SpincastConfig#getDictionaryEntryNotFoundFallbackTo()} and
     * {@link SpincastConfig#getDictionaryEntryNotFoundBehavior()}.
     */
    public String get(String key, boolean forceEvaluation);

    /**
     * Gets a message.
     * <p>
     * Uses the specified Locale.
     * <p>
     * @param forceEvaluation if <code>true</code>,
     * the {@link TemplatingEngine} will be used even there
     * are no parameters. This may be useful to trigger some
     * templating function that don't need parameters. We don't
     * use the templting engine will default since it is costy.
     * <p>
     * The behavior if the key is not found can be configured using
     * {@link SpincastConfig#getDictionaryEntryNotFoundFallbackTo()} and
     * {@link SpincastConfig#getDictionaryEntryNotFoundBehavior()}.
     */
    public String get(String key, Locale locale, boolean forceEvaluation);

    /**
     * Gets a message.
     *
     * Will use the proper Locale as provided by the
     * {@link LocaleResolver}.
     * <p>
     * The behavior if the key is not found can be configured using
     * {@link SpincastConfig#getDictionaryEntryNotFoundFallbackTo()} and
     * {@link SpincastConfig#getDictionaryEntryNotFoundBehavior()}.
     */
    public String get(String key, Pair... params);

    /**
     * Gets a message.
     * <p>
     * Uses the specified Locale.
     * <p>
     * The behavior if the key is not found can be configured using
     * {@link SpincastConfig#getDictionaryEntryNotFoundFallbackTo()} and
     * {@link SpincastConfig#getDictionaryEntryNotFoundBehavior()}.
     */
    public String get(String key, Locale locale, Pair... params);

    /**
     * Gets a message.
     * <p>
     * Will use the proper Locale as provided by the
     * {@link LocaleResolver}.
     * <p>
     * The behavior if the key is not found can be configured using
     * {@link SpincastConfig#getDictionaryEntryNotFoundFallbackTo()} and
     * {@link SpincastConfig#getDictionaryEntryNotFoundBehavior()}.
     */
    public String get(String key, Map<String, Object> params);

    /**
     * Gets a message.
     * <p>
     * Uses the specified Locale.
     * <p>
     * The behavior if the key is not found can be configured using
     * {@link SpincastConfig#getDictionaryEntryNotFoundFallbackTo()} and
     * {@link SpincastConfig#getDictionaryEntryNotFoundBehavior()}.
     */
    public String get(String key, Locale locale, Map<String, Object> params);

    /**
     * Contains this key?
     */
    public boolean hasKey(String key);

    /**
     * Return all the possible values, for all languages used,
     * by language abreviation.
     */
    public Map<String, String> getAll(String key);

    /**
     * Return all the possible values, for all languages used,
     * by language abreviation.
     */
    public Map<String, String> getAll(String key, Map<String, Object> params);

    /**
     * Return all the possible values, for all languages used,
     * by language abreviation.
     */
    public Map<String, String> getAll(String key, Pair... params);

    /**
     * Returns all keys the dictionary contains.
     */
    public Set<String> getAllKeys();

}
