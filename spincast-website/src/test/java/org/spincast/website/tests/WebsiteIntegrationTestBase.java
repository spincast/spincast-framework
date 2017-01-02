package org.spincast.website.tests;

import java.io.File;
import java.nio.file.Files;

import org.spincast.core.guice.GuiceTweaker;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.websocket.DefaultWebsocketContext;
import org.spincast.plugins.configpropsfile.SpincastConfigPropsFilePluginModule;
import org.spincast.shaded.org.apache.commons.io.FileUtils;
import org.spincast.testing.core.IntegrationTestAppBase;
import org.spincast.testing.core.utils.SpincastTestUtils;
import org.spincast.website.App;
import org.spincast.website.AppConfigDefault;
import org.spincast.website.exchange.AppRequestContext;

/**
 * Integration test base class specifically made for 
 * our application.
 */
public abstract class WebsiteIntegrationTestBase extends IntegrationTestAppBase<AppRequestContext, DefaultWebsocketContext> {

    /**
     * We'll manage the testing configurations by ourself.
     */
    @Override
    protected boolean isEnableGuiceTweakerTestingConfigMecanism() {
        return false;
    }

    @Override
    protected GuiceTweaker createGuiceTweaker() {

        GuiceTweaker guiceTweaker = super.createGuiceTweaker();

        //==========================================
        // We use the .properties file based Configurations plugin.
        //==========================================
        guiceTweaker.module(new SpincastConfigPropsFilePluginModule());

        return guiceTweaker;
    }

    protected File tempAppPropertiesFile;

    @Override
    public void beforeClass() {
        createTestAppConfigPropertiesFile();
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
    protected void createTestAppConfigPropertiesFile() {

        try {
            this.tempAppPropertiesFile = Files.createTempFile(WebsiteIntegrationTestBase.class.getName(), ".properties").toFile();

            String content = getAppConfigPropertiesFileContent();

            FileUtils.writeStringToFile(this.tempAppPropertiesFile, content, "UTF-8");

        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected String getAppConfigPropertiesFileContent() {
        return AppConfigDefault.APP_PROPERTIES_KEY_HTTP_SERVER_PORT + "=" + SpincastTestUtils.findFreePort() + "\n";
    }

    protected void deleteTestAppPropertiesFile() {
        FileUtils.deleteQuietly(this.tempAppPropertiesFile);
    }

    @Override
    protected void initApp() {
        App.main(getMainArgs());
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
