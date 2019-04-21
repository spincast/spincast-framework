package org.spincast.plugins.session.tests;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.guice.SpincastPlugin;
import org.spincast.core.json.JsonManager;
import org.spincast.plugins.session.SpincastSession;
import org.spincast.plugins.session.SpincastSessionFilter;
import org.spincast.plugins.session.SpincastSessionManager;
import org.spincast.plugins.session.SpincastSessionPlugin;
import org.spincast.plugins.session.SpincastSessionRepository;
import org.spincast.plugins.session.config.SpincastSessionConfig;
import org.spincast.testing.defaults.NoAppStartHttpServerTestingBase;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.util.Modules;

public abstract class SessionTestBase extends NoAppStartHttpServerTestingBase {

    protected static Map<String, SpincastSession> savedSession = new HashMap<String, SpincastSession>();
    protected static int[] deleteOldSessionsCalled = new int[]{0};

    @Inject
    protected SpincastSessionManager sessionManager;

    @Inject
    protected JsonManager jsonManager;

    protected SpincastSessionManager getSessionManager() {
        return this.sessionManager;
    }

    protected JsonManager getJsonManager() {
        return this.jsonManager;
    }

    @Inject
    protected SpincastSessionConfig spincastSessionConfig;

    protected SpincastSessionConfig getSpincastSessionConfig() {
        return this.spincastSessionConfig;
    }

    @Inject
    protected SpincastSessionFilter spincastSessionFilter;

    protected SpincastSessionFilter getSpincastSessionFilter() {
        return this.spincastSessionFilter;
    }

    @Override
    protected List<SpincastPlugin> getExtraPlugins() {
        List<SpincastPlugin> extraPlugins = super.getExtraPlugins();
        extraPlugins.add(new SpincastSessionPlugin());
        return extraPlugins;
    }

    @Override
    protected Module getExtraOverridingModule() {
        return Modules.override(super.getExtraOverridingModule()).with(new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                if (getSpincastSessionRepositoryImplClass() != null) {
                    bind(SpincastSessionRepository.class).to(getSpincastSessionRepositoryImplClass()).in(Scopes.SINGLETON);
                }
            }
        });
    }

    protected Class<? extends SpincastSessionRepository> getSpincastSessionRepositoryImplClass() {
        return null; // use the default repo
    }

    @Override
    public void beforeClass() {
        super.beforeClass();

        savedSession = new HashMap<String, SpincastSession>();
        deleteOldSessionsCalled = new int[]{0};
    }

}
