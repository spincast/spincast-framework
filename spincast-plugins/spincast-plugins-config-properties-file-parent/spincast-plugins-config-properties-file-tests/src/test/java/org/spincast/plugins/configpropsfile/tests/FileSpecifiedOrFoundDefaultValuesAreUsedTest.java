package org.spincast.plugins.configpropsfile.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.annotation.Nullable;

import org.junit.Test;
import org.spincast.core.config.ISpincastConfig;
import org.spincast.core.guice.MainArgs;
import org.spincast.core.utils.ISpincastUtils;
import org.spincast.defaults.tests.DefaultIntegrationTestingBase;
import org.spincast.defaults.tests.DefaultTestingModule;
import org.spincast.plugins.configpropsfile.SpincastConfigPropsFileBased;

import com.google.inject.Inject;
import com.google.inject.Module;

public class FileSpecifiedOrFoundDefaultValuesAreUsedTest extends DefaultIntegrationTestingBase {

    protected String appPropertiesPath;

    public static class PropsFileBasedConfig extends SpincastConfigPropsFileBased {

        @Inject
        public PropsFileBasedConfig(ISpincastUtils spincastUtils,
                                    @MainArgs @Nullable String[] mainArgs) {
            super(spincastUtils, mainArgs);
        }
    }

    @Override
    public Module getTestingModule() {
        return new DefaultTestingModule(getMainArgsToUse()) {

            @Override
            protected Class<? extends ISpincastConfig> getSpincastConfigClass() {
                return PropsFileBasedConfig.class;
            }
        };
    }

    @Test
    public void testDefaultConfigOverriden() throws Exception {

        assertNotNull(getSpincastConfig().getHttpServerPort());
        assertEquals(44419, getSpincastConfig().getHttpServerPort());
    }

}
