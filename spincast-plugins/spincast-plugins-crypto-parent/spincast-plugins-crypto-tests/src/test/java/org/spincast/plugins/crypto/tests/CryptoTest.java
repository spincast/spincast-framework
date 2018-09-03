package org.spincast.plugins.crypto.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.guice.SpincastPlugin;
import org.spincast.defaults.testing.NoAppStartHttpServerTestingBase;
import org.spincast.plugins.crypto.SpincastCryptoPlugin;
import org.spincast.plugins.crypto.SpincastCryptoUtils;
import org.spincast.plugins.crypto.SpincastCryptoUtilsDefault;
import org.spincast.plugins.crypto.config.SpincastCryptoConfig;
import org.spincast.testing.core.utils.SpincastTestUtils;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.util.Modules;

public class CryptoTest extends NoAppStartHttpServerTestingBase {

    @Override
    protected List<SpincastPlugin> getExtraPlugins() {
        return Lists.newArrayList(new SpincastCryptoPlugin());
    }

    @Override
    protected Module getExtraOverridingModule() {
        return Modules.override(super.getExtraOverridingModule()).with(new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(SpincastCryptoUtils.class).to(TestSpincastCryptoUtils.class).in(Scopes.SINGLETON);
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

    @Inject
    protected SpincastCryptoUtils spincastCryptoUtils;
    SpincastCryptoUtilsDefault spincastCryptoUtilsDefaultMock;

    protected SpincastCryptoUtils getSpincastCryptoUtils() {
        return this.spincastCryptoUtils;
    }

    @Test
    public void removeJavaCryptoRestrictionsOnInitDefault() throws Exception {
        assertEquals(1, removeCryptographyRestrictionsCalled[0]);
    }

    @Test
    public void encryptDecrypt() throws Exception {

        String encrypted2 = getSpincastCryptoUtils().encrypt("my String to encrypt", "my$ecretKey!42");
        System.out.println(encrypted2);

        String key = "Stromgol123";
        String str = SpincastTestUtils.TEST_STRING;

        String encrypted = getSpincastCryptoUtils().encrypt(str, key);
        assertNotEquals(str, encrypted);

        String decrypted = getSpincastCryptoUtils().decrypt(encrypted, key);
        assertEquals(str, decrypted);

        try {
            getSpincastCryptoUtils().decrypt(encrypted, "nope");
            fail();
        } catch (Exception ex) {
        }
    }




}
