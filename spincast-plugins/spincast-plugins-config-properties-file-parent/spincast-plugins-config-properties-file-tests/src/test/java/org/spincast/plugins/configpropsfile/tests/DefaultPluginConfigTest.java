package org.spincast.plugins.configpropsfile.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.spincast.defaults.testing.IntegrationTestNoAppDefaultContextsBase;
import org.spincast.plugins.configpropsfile.SpincastConfigPropsFilePluginModule;

import com.google.inject.Module;

public class DefaultPluginConfigTest extends IntegrationTestNoAppDefaultContextsBase {

    /**
     * We'll manage the testing configurations by ourself.
     */
    @Override
    protected boolean isEnableGuiceTweakerTestingConfigMecanism() {
        return false;
    }

    @Override
    protected Module getExtraOverridingModule() {
        return new SpincastConfigPropsFilePluginModule();
    }

    @Test
    public void defaultConfig() throws Exception {

        assertNotNull(getSpincastConfig().getHttpServerPort());
        assertEquals(44419, getSpincastConfig().getHttpServerPort());
    }

}
