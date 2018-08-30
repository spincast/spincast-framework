package org.spincast.plugins.pebble.tests;

import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.spincast.core.dictionary.Dictionary;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.json.JsonManager;
import org.spincast.core.json.JsonObject;
import org.spincast.core.templating.TemplatingEngine;
import org.spincast.core.utils.ObjectConverter;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.defaults.testing.NoAppTestingBase;
import org.spincast.plugins.pebble.SpincastMainPebbleExtension;
import org.spincast.plugins.pebble.SpincastMainPebbleExtensionDefault;
import org.spincast.plugins.pebble.SpincastPebbleTemplatingEngineConfig;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.extension.core.DefaultFilter;

public class OverrideSpincastExtensionTest extends NoAppTestingBase {

    @Override
    protected Module getExtraOverridingModule() {
        return new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(SpincastMainPebbleExtension.class).to(ModifiedSpincastPebbleExtension.class)
                                                       .in(Scopes.SINGLETON);
            }
        };
    }

    public static class ModifiedSpincastPebbleExtension extends SpincastMainPebbleExtensionDefault {

        @Inject
        public ModifiedSpincastPebbleExtension(Provider<TemplatingEngine> templatingEngineProvider,
                                               SpincastPebbleTemplatingEngineConfig spincastPebbleTemplatingEngineConfig,
                                               ObjectConverter objectConverter,
                                               SpincastUtils spincastUtils,
                                               Dictionary dictionary,
                                               Provider<RequestContext<?>> requestContextProvider) {
            super(templatingEngineProvider,
                  spincastPebbleTemplatingEngineConfig,
                  objectConverter,
                  spincastUtils,
                  dictionary,
                  requestContextProvider);
        }

        //==========================================
        // Override the "get" filter to make it
        // do something different...
        //==========================================
        @Override
        protected Filter getGetFilter() {

            Filter filter = new DefaultFilter() {

                @Override
                public List<String> getArgumentNames() {
                    return null;
                }

                @Override
                public Object apply(Object value, Map<String, Object> args) {
                    return "custom!";
                }
            };

            return filter;
        }
    }

    @Inject
    protected JsonManager jsonManager;


    @Inject
    protected TemplatingEngine templatingEngine;

    @Test
    public void dateFormatModified() throws Exception {

        JsonObject model = this.jsonManager.create();
        model.set("now", Instant.now());

        String html = this.templatingEngine.evaluate("{{ now | get() }}", model);
        assertEquals("custom!", html);
    }

}
