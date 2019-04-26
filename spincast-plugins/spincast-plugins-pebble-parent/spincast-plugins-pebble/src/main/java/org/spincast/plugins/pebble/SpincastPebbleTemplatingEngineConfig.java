package org.spincast.plugins.pebble;

import com.google.inject.ImplementedBy;
import com.mitchellbosecke.pebble.extension.Extension;

/**
 * Configurations for the Pebble templating engine plugin.
 *
 * We use "@ImplementedBy" to specify the default configurations
 * to use if none is specified in a Guice module.
 */
@ImplementedBy(SpincastPebbleTemplatingEngineConfigDefault.class)
public interface SpincastPebbleTemplatingEngineConfig {

    /**
     * Pebble extension to register: allows you to add custom
     * filters, functions, etc.
     */
    public Extension getExtension();

    /**
     * The number of template item to keep in cache.
     *
     * @return the max number of items to cache. If <code>&lt;= 0</code>,
     * thi cache will be disabled.
     */
    public int getTemplateCacheItemNbr();

    /**
     * The number of "cache" tag item to keep in cache.
     * <a href="https://github.com/PebbleTemplates/pebble/wiki/cache">more info</a>
     *
     * @return the max number of items to cache. If <code>&lt;= 0</code>,
     * this cache will be disabled.
     */
    public int getTagCacheTypeItemNbr();

    /**
     * Is the strict variable policy on?
     * <p>
     * From the doc : "If set to true, Pebble will throw an exception
     * if you try to access a variable or attribute that does not exist
     * (or an attribute of a null variable). If set to false,
     * your template will treat non-existing variables/attributes
     * as null without ever skipping a beat."
     */
    public boolean isStrictVariablesEnabled();

    /**
     * The path to the template to use to display
     * the validation messages of a Form' field.
     */
    public String getValidationMessagesTemplatePath();

    /**
     * The path to the template to use to display
     * the validation messages of a Form's field group.
     */
    public String getValidationGroupMessagesTemplatePath();

}
