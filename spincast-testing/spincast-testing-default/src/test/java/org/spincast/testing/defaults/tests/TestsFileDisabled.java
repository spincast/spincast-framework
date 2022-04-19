package org.spincast.testing.defaults.tests;

import static org.junit.Assert.fail;

import java.util.Collection;

import org.junit.Test;
import org.junit.runners.model.FrameworkMethod;
import org.spincast.testing.defaults.NoAppTestingBase;

public class TestsFileDisabled extends NoAppTestingBase {

    @Override
    public boolean isTestClassDisabledPreBeforeClass(Collection<FrameworkMethod> filteredTests) {
        return true;
    }

    @Test
    public void test() throws Exception {
        fail();
    }
}
