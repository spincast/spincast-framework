package org.spincast.plugins.crypto.tests;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.guice.SpincastPlugin;
import org.spincast.defaults.testing.NoAppStartHttpServerTestingBase;
import org.spincast.plugins.crypto.SpincastCryptoPlugin;
import org.spincast.plugins.crypto.SpincastCryptoUtils;
import org.spincast.plugins.crypto.SpincastCryptoUtilsDefault;
import org.spincast.plugins.crypto.config.SpincastCryptoConfig;
import org.spincast.plugins.crypto.config.SpincastCryptoConfigDefault;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.util.Modules;

public class DisableRemoveCryptoRestrictionsTest extends NoAppStartHttpServerTestingBase {

    @Override
    protected List<SpincastPlugin> getExtraPlugins() {
        List<SpincastPlugin> extraPlugins = super.getExtraPlugins();
        extraPlugins.add(new SpincastCryptoPlugin());
        return extraPlugins;
    }

    @Override
    protected Module getExtraOverridingModule() {
        return Modules.override(super.getExtraOverridingModule()).with(new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(SpincastCryptoUtils.class).to(TestSpincastCryptoUtils.class).in(Scopes.SINGLETON);
                bind(SpincastCryptoConfig.class).toInstance(new SpincastCryptoConfigDefault() {

                    @Override
                    public boolean removeJavaCryptoRestrictionsOnInit() {
                        return false;
                    }
                });
            }
        });
    }

    protected static int[] removeCryptographyRestrictionsCalled = new int[]{0};

    public static class TestSpincastCryptoUtils extends SpincastCryptoUtilsDefault {

        @Inject
        public TestSpincastCryptoUtils(SpincastCryptoConfig spincastCryptoConfig) {
            super(spincastCryptoConfig);
        }

        @Override
        public void removeCryptographyRestrictions() {
            removeCryptographyRestrictionsCalled[0]++;
        }
    }

    @Override
    public void beforeClass() {
        super.beforeClass();
        this.spincastCryptoUtilsDefaultMock = Mockito.mock(SpincastCryptoUtilsDefault.class);
    }

    @Inject
    protected SpincastCryptoUtils spincastCryptoUtils;
    SpincastCryptoUtilsDefault spincastCryptoUtilsDefaultMock;

    protected SpincastCryptoUtils getSpincastCryptoUtils() {
        return this.spincastCryptoUtils;
    }

    @Test
    public void removeJavaCryptoRestrictionsOnInitFalse() throws Exception {
        assertEquals(0, removeCryptographyRestrictionsCalled[0]);
    }


}
