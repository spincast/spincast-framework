package org.spincast.testing.defaults.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Locale;

import org.junit.Test;
import org.spincast.core.guice.GuiceTweaker;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.locale.LocaleResolver;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.defaults.bootstrapping.Spincast;
import org.spincast.plugins.localeresolver.SpincastLocaleResolverPlugin;
import org.spincast.testing.defaults.tests.utils.SpincastUtilsTesting;

import com.google.inject.Inject;
import com.google.inject.Scopes;

public class ITAppDisablePluginAddModuleTest extends IntegrationConfigTestClassBase {

    @Override
    protected GuiceTweaker createGuiceTweaker() {
        GuiceTweaker tweaker = super.createGuiceTweaker();

        //==========================================
        // Disables the LocaleResolver plugin
        //==========================================
        tweaker.pluginToDisable(SpincastLocaleResolverPlugin.PLUGIN_ID);

        //==========================================
        // Custom module
        //==========================================
        tweaker.overridingModule(new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(LocaleResolver.class).toInstance(new LocaleResolver() {

                    @Override
                    public Locale getLocaleToUse() {
                        return Locale.ITALY;
                    }
                });
                bind(SpincastUtils.class).to(SpincastUtilsTesting.class).in(Scopes.SINGLETON);
            }
        });

        return tweaker;
    }

    @Inject
    protected App app;

    protected App getApp() {
        return this.app;
    }

    /**
     * Testing App
     */
    public static class App {

        private LocaleResolver localeResolver;
        private SpincastUtils spincastUtils;

        public LocaleResolver getLocaleResolver() {
            return this.localeResolver;
        }

        public SpincastUtils getSpincastUtils() {
            return this.spincastUtils;
        }

        public static void main(String[] args) {
            Spincast.init(args);
        }

        @Inject
        public void init(LocaleResolver localeResolver, SpincastUtils spincastUtils) {
            this.localeResolver = localeResolver;
            this.spincastUtils = spincastUtils;
        }
    }

    @Override
    protected void callAppMainMethod() {
        App.main(null);
    }

    @Test
    public void test() throws Exception {
        assertNotNull(getApp().getLocaleResolver());
        assertEquals(Locale.ITALY, getApp().getLocaleResolver().getLocaleToUse());

        assertNotNull(getApp().getSpincastUtils());
        assertEquals("42", getApp().getSpincastUtils().getCacheBusterCode());
    }

}
