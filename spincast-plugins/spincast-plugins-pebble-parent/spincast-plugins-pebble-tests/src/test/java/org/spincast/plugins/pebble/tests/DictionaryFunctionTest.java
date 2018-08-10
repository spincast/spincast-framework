package org.spincast.plugins.pebble.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.dictionary.Dictionary;
import org.spincast.core.dictionary.DictionaryEntries;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.json.JsonManager;
import org.spincast.core.locale.LocaleResolver;
import org.spincast.core.templating.TemplatingEngine;
import org.spincast.defaults.testing.NoAppTestingBase;
import org.spincast.plugins.dictionary.SpincastDictionaryDefault;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Scopes;

public class DictionaryFunctionTest extends NoAppTestingBase {

    @Inject
    protected TemplatingEngine templatingEngine;

    @Inject
    protected JsonManager jsonManager;

    protected TemplatingEngine getTemplatingEngine() {
        return this.templatingEngine;
    }

    protected JsonManager getJsonManager() {
        return this.jsonManager;
    }

    @Override
    protected Module getExtraOverridingModule() {

        return new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(Dictionary.class).to(AppDictionary.class).in(Scopes.SINGLETON);
            }
        };
    }

    /**
     * Custom dictionary
     */
    protected static class AppDictionary extends SpincastDictionaryDefault {

        @Inject
        public AppDictionary(LocaleResolver localeResolver,
                             TemplatingEngine templatingEngine,
                             SpincastConfig spincastConfig,
                             @Nullable Set<DictionaryEntries> dictionaryEntries) {
            super(localeResolver, templatingEngine, spincastConfig, dictionaryEntries);
        }

        @Override
        protected void addMessages() {

            key("my.custom.msg.1",
                msg("", "custom 111"));

            key("my.custom.msg.2",
                msg("", "custom {{myParam}}"));

            key("my.custom.msg.3",
                msg("", "{% if 1 == 2 %}XXX{% else %}YYY{% endif %}"));

            key("my.custom.msg.4",
                msg("", "{% if myParam == '2' %}XXX{% else %}YYY{% endif %}"));

        }
    }

    @Test
    public void simple() throws Exception {
        String html = getTemplatingEngine().evaluate("{{ msg('my.custom.msg.1') }}", (Map<String, Object>)null);
        assertEquals("custom 111", html);
    }

    @Test
    public void notFound() throws Exception {

        try {
            getTemplatingEngine().evaluate("{{ msg('nope') }}", (Map<String, Object>)null);
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void withParam() throws Exception {
        String html =
                getTemplatingEngine().evaluate("{{ msg('my.custom.msg.2', 'myParam', 'Titi') }}", (Map<String, Object>)null);
        assertEquals("custom Titi", html);
    }

    @Test
    public void withParamNotProvided() throws Exception {
        String html =
                getTemplatingEngine().evaluate("{{ msg('my.custom.msg.2', 'nope', 'Titi') }}", (Map<String, Object>)null);
        assertEquals("custom ", html);
    }

    @Test
    public void withInvalidParamKeysValues() throws Exception {
        try {
            getTemplatingEngine().evaluate("{{ msg('my.custom.msg.2', 'myParam') }}", (Map<String, Object>)null);
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void noForceEvaluationNoParams() throws Exception {
        String html = getTemplatingEngine().evaluate("{{ msg('my.custom.msg.3') }}", (Map<String, Object>)null);
        assertEquals("{% if 1 == 2 %}XXX{% else %}YYY{% endif %}", html);
    }

    @Test
    public void noForceEvaluationParams() throws Exception {
        String html = getTemplatingEngine().evaluate("{{ msg('my.custom.msg.3', 'key1', 'val1') }}", (Map<String, Object>)null);
        assertEquals("YYY", html);
    }

    @Test
    public void noForceEvaluationNoParamsExplicit() throws Exception {
        String html = getTemplatingEngine().evaluate("{{ msg('my.custom.msg.3', false) }}", (Map<String, Object>)null);
        assertEquals("{% if 1 == 2 %}XXX{% else %}YYY{% endif %}", html);
    }

    @Test
    public void forceEvaluationNoParams() throws Exception {
        String html = getTemplatingEngine().evaluate("{{ msg('my.custom.msg.3', true) }}", (Map<String, Object>)null);
        assertEquals("YYY", html);
    }

    @Test
    public void evalAndParamEquals2() throws Exception {
        String html =
                getTemplatingEngine().evaluate("{{ msg('my.custom.msg.4', 'myParam', '2') }}", (Map<String, Object>)null);
        assertEquals("XXX", html);
    }

    @Test
    public void evalAndParamNotEquals2() throws Exception {
        String html =
                getTemplatingEngine().evaluate("{{ msg('my.custom.msg.4', 'myParam', '3') }}", (Map<String, Object>)null);
        assertEquals("YYY", html);
    }
}
