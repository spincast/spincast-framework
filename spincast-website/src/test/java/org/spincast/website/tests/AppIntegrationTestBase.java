package org.spincast.website.tests;

import java.io.File;
import java.nio.file.Files;

import org.spincast.core.utils.SpincastStatics;
import org.spincast.shaded.org.apache.commons.io.FileUtils;
import org.spincast.testing.core.SpincastIntegrationTestBase;
import org.spincast.testing.core.utils.SpincastTestUtils;
import org.spincast.website.App;
import org.spincast.website.AppConfig;
import org.spincast.website.exchange.IAppRequestContext;

import com.google.inject.Injector;

/**
 * Integration test base class specifically made for 
 * our application.
 */
public abstract class AppIntegrationTestBase extends SpincastIntegrationTestBase<IAppRequestContext> {

    protected File tempAppPropertiesFile;

    @Override
    public void beforeClass() {
        createTestAppPropertiesFile();
        super.beforeClass();
    };

    @Override
    public void afterClass() {
        deleteTestAppPropertiesFile();
        super.afterClass();
    }

    /**
     * We create a test properties file to specify some custom
     * configurations for our tests.
     */
    protected void createTestAppPropertiesFile() {

        try {
            this.tempAppPropertiesFile = Files.createTempFile(AppIntegrationTestBase.class.getName(), ".properties").toFile();

            String srt = AppConfig.APP_PROPERTIES_KEY_HTTP_SERVER_PORT + "=" + SpincastTestUtils.findFreePort();

            FileUtils.writeStringToFile(this.tempAppPropertiesFile, srt, "UTF-8");

        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected void deleteTestAppPropertiesFile() {
        FileUtils.deleteQuietly(this.tempAppPropertiesFile);
    }

    /**
     * Create the application and return the Guice
     * injector.
     */
    @Override
    protected Injector createInjector() {
        return App.createApp(getMainArgs());
    }

    protected String[] getMainArgs() {

        //==========================================
        // We pass the path to the test app properties
        // file as the first arg.
        //==========================================
        String[] mainArgs = new String[]{this.tempAppPropertiesFile.getAbsolutePath()};

        return mainArgs;

    }
}
