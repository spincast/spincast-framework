package org.spincast.plugins.timezoneresolver.tests;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.guice.TestingMode;
import org.spincast.core.json.JsonManager;
import org.spincast.core.templating.TemplatingEngine;
import org.spincast.core.timezone.TimeZoneResolver;
import org.spincast.plugins.config.SpincastConfigPluginConfig;
import org.spincast.plugins.timezoneresolver.config.SpincastTimeZoneResolverConfig;
import org.spincast.plugins.timezoneresolver.config.SpincastTimeZoneResolverConfigDefault;
import org.spincast.testing.core.utils.SpincastConfigTestingDefault;
import org.spincast.testing.defaults.NoAppTestingBase;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Scopes;

public class PebbleExtensionCustomConfigQsQueryNameTest extends NoAppTestingBase {

    @Override
    protected Module getExtraOverridingModule() {
        return new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(SpincastTimeZoneResolverConfig.class).to(CustomTimeZoneResolverConfig.class).in(Scopes.SINGLETON);
            }
        };
    }

    static class CustomTimeZoneResolverConfig extends SpincastTimeZoneResolverConfigDefault {

        @Inject
        public CustomTimeZoneResolverConfig(SpincastConfig spincastConfig) {
            super(spincastConfig);
        }

        @Override
        public boolean isRefreshPageAfterAddingPebbleTimeZoneCookie() {
            return true;
        }

        @Override
        public String getPebbleTimeZoneCookieDomain() {
            //==========================================
            // No "."
            //==========================================
            return getSpincastConfig().getPublicServerHost();
        }

        @Override
        public String getPebbleTimeZoneCookiePath() {
            return "/titi";
        }

        @Override
        public String getPebbleTimeZoneCookieReloadingQsParamName() {
            return "toto";
        }
    }

    @Override
    protected Class<? extends SpincastConfig> getTestingConfigImplementationClass() {
        return CustomSpincastConfig.class;
    }

    static class CustomSpincastConfig extends SpincastConfigTestingDefault {

        @Inject
        protected CustomSpincastConfig(SpincastConfigPluginConfig spincastConfigPluginConfig, @TestingMode boolean testingMode) {
            super(spincastConfigPluginConfig, testingMode);
        }

        @Override
        public String getCookieNameTimeZoneId() {
            return "prout";
        }
    }

    @Inject
    protected TimeZoneResolver timeZoneResolver;

    @Inject
    protected JsonManager jsonManager;

    @Inject
    protected TemplatingEngine templatingEngine;

    protected TimeZoneResolver getTimeZoneResolver() {
        return this.timeZoneResolver;
    }

    protected JsonManager getJsonManager() {
        return this.jsonManager;
    }

    protected TemplatingEngine getTemplatingEngine() {
        return this.templatingEngine;
    }

    @Test
    public void defaultTest() throws Exception {

        String content = "{{ timeZoneCookie() }}";
        String result = getTemplatingEngine().evaluate(content);
        assertTrue(result.indexOf("<script>") > -1);
        assertTrue(result.indexOf("document.cookie = \"spincast_timezone=") == -1);
        assertTrue(result.indexOf("document.cookie ='prout=") > -1);
        assertTrue(result.indexOf("domain=." + getSpincastConfig().getPublicServerHost() + ";") == -1);
        assertTrue(result.indexOf("domain=" + getSpincastConfig().getPublicServerHost() + ";") > -1);
        assertTrue(result.indexOf("path=/';") == -1);
        assertTrue(result.indexOf("path=/titi';") > -1);
        assertTrue(result.indexOf("var part = 'spincast_tz=1';") == -1);
        assertTrue(result.indexOf("var flag = 'toto=1';") > -1);
        assertTrue(result.indexOf("window.location.replace(url);") > -1);
        assertTrue(result.indexOf("</script>") > -1);
    }

}
