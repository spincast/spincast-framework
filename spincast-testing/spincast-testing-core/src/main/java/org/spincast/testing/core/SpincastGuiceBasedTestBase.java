package org.spincast.testing.core;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.UUID;

import org.junit.runner.RunWith;
import org.spincast.core.config.ISpincastConfig;
import org.spincast.shaded.org.apache.commons.io.FileUtils;
import org.spincast.testing.utils.IBeforeAfterClassMethodsProvider;
import org.spincast.testing.utils.SpincastJUnitRunner;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * Base class for Spincast test classes.
 * 
 * <p>
 * Uses a custom Junit runner, 
 * {@link org.spincast.testing.utils.SpincastJUnitRunner SpincastJUnitRunner}.
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
@RunWith(SpincastJUnitRunner.class)
public abstract class SpincastGuiceBasedTestBase implements IBeforeAfterClassMethodsProvider {

    private Injector guice;
    private File testingWritableDir;
    private ISpincastConfig spincastConfig;

    @Override
    public void beforeClass() {

        this.guice = createInjector();
        assertNotNull(this.guice);

        this.guice.injectMembers(this);
    }

    @Override
    public void afterClass() {
        deleteTempDir();
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
        try {
            if(this.testingWritableDir != null) {
                FileUtils.deleteDirectory(this.testingWritableDir);
            }
        } catch(Exception ex) {
            System.err.println(ex);
        }
    }

    protected File getTestingWritableDir() {
        if(this.testingWritableDir == null) {

            //==========================================
            // We don't use the configurations to find a writable
            // directory since, sometimes, tests may want to create
            // files *before* the Guice context is created!
            //==========================================
            //this.testingWritableDir = new File(getSpincastConfig().getSpincastWritableDir().getAbsolutePath() +
            //                                   "/testing");
            this.testingWritableDir = new File(getTestingWritableDirBasePath() + "/spincast/testing");
            if(!this.testingWritableDir.isDirectory()) {
                boolean mkdirs = this.testingWritableDir.mkdirs();
                assertTrue(mkdirs);
            }
            assertTrue(this.testingWritableDir.canWrite());
        }
        return this.testingWritableDir;
    }

    protected String getTestingWritableDirBasePath() {
        File baseDir = new File(System.getProperty("java.io.tmpdir"));
        if(!baseDir.isDirectory()) {
            throw new RuntimeException("Temporary directory doesn't exist : " + baseDir.getAbsolutePath());
        }
        return baseDir.getAbsolutePath();
    }

    /**
     * Create a temporary test file, using the given relative path.
     */
    protected String createTestingFilePath(String relativePath) {
        return getTestingWritableDir().getAbsolutePath() + "/" + relativePath;
    }

    /**
     * Create a temporary test file.
     */
    protected String createTestingFilePath() {
        return createTestingFilePath(UUID.randomUUID().toString());
    }

    /**
     * The test class must implement this method to create
     * the Guice injector. It can be done by starting a real
     * application (with a <code>main(...)</code> method) or by 
     * creating a custom Injector.
     */
    protected abstract Injector createInjector();

}
