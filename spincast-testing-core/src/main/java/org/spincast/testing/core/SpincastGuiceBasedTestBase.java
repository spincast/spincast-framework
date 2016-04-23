package org.spincast.testing.core;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.runner.RunWith;
import org.spincast.core.config.ISpincastConfig;
import org.spincast.shaded.org.apache.commons.io.FileUtils;
import org.spincast.testing.utils.IBeforeAfterClassMethodsProvider;
import org.spincast.testing.utils.OneInstancePerClassJUnitRunner;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * Base class for Spincast test classes.
 * 
 * <p>
 * Uses a custom Junit runner, 
 * {@link org.spincast.testing.utils.OneInstancePerClassJUnitRunner OneInstancePerClassJUnitRunner}.
 * </p>
 * <p>
 * This runner create a single instance of the test class for all of its tests,
 * instead of a new instance for each test. It also calls a <code>beforeClass()</code>
 * method before the tests are run and an <code>afterClass()</code> method after
 * they are run.
 * </p>
 * <p>
 * A class extending this will be part of a Guice context 
 * (created using the {@link #createInjector() createInjector} method) and 
 * the required dependencies will be injected into it.
 * </p>
 */
@RunWith(OneInstancePerClassJUnitRunner.class)
public abstract class SpincastGuiceBasedTestBase implements IBeforeAfterClassMethodsProvider {

    private Injector guice;
    private File testingWritableDir;
    private ISpincastConfig spincastConfig;

    @Override
    public void beforeClass() {

        this.guice = createInjector();
        assertNotNull(this.guice);

        this.guice.injectMembers(this);

        deleteTempDir();
    }

    @Override
    public void afterClass() {
        //...
    }

    @Inject
    protected void setSpincastConfig(ISpincastConfig spincastConfig) {
        this.spincastConfig = spincastConfig;
    }

    protected ISpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    protected Injector getInjector() {
        return this.guice;
    }

    protected void deleteTempDir() {
        FileUtils.deleteQuietly(getTestingWritableDir());
    }

    protected File getTestingWritableDir() {
        if(this.testingWritableDir == null) {
            this.testingWritableDir = new File(getSpincastConfig().getSpincastWritableDir().getAbsolutePath() +
                                               "/testing");
            if(!this.testingWritableDir.isDirectory()) {
                boolean mkdirs = this.testingWritableDir.mkdirs();
                assertTrue(mkdirs);
            }
        }
        return this.testingWritableDir;
    }

    protected String createTestingFilePath(String relativePath) {
        return getTestingWritableDir().getAbsolutePath() + "/" + relativePath;
    }

    /**
     * The test class must implement this method to create
     * the Guice injector. It can be done by starting a real
     * application (with a <code>main(...)</code> method) or by 
     * creating a custom Injector.
     */
    protected abstract Injector createInjector();

}
