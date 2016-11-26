package org.spincast.website.tests;

import java.io.File;
import java.nio.file.Files;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.websocket.DefaultWebsocketContext;
import org.spincast.core.websocket.WebsocketContext;
import org.spincast.shaded.org.apache.commons.io.FileUtils;
import org.spincast.testing.core.SpincastIntegrationTestBase;
import org.spincast.testing.core.utils.SpincastTestUtils;
import org.spincast.website.App;
import org.spincast.website.AppConfigDefault;
import org.spincast.website.exchange.AppRequestContext;

import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Integration test base class specifically made for 
 * our application.
 */
public abstract class WebsiteIntegrationTestBase extends
                                                 SpincastIntegrationTestBase<AppRequestContext, DefaultWebsocketContext> {

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

    /**
     * Creates the application and returns the Guice
     * injector.
     */
    @Override
    protected Injector createInjector() {
        return App.createApp(getMainArgs(), getTestOverridingModule(AppRequestContext.class, DefaultWebsocketContext.class));
    }

    protected String[] getMainArgs() {

        //==========================================
        // We pass the path to the test app properties
        // file as the first arg.
        //==========================================
        String[] mainArgs = new String[]{this.tempAppPropertiesFile.getAbsolutePath()};

        return mainArgs;

    }

    protected Module getTestOverridingModule(Class<? extends RequestContext<?>> requestContextClass,
                                             Class<? extends WebsocketContext<?>> websocketContextClass) {

        // No extra overriding bindings required by default.
        return getDefaultOverridingModule(requestContextClass, websocketContextClass);
    }
}
