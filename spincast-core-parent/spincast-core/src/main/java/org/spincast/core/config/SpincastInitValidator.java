package org.spincast.core.config;

import com.google.inject.Inject;

/**
 * Init validation performs by Spincast when
 * an application starts.
 */
public class SpincastInitValidator {

    private final SpincastConfig spincastConfig;

    /**
     * Constructor
     */
    @Inject
    public SpincastInitValidator(SpincastConfig spincastConfig) {
        this.spincastConfig = spincastConfig;
    }

    protected SpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    /**
     * Init
     */
    @Inject
    protected void init() {
        validateLocalhostHost();
    }

    protected void validateLocalhostHost() {

        if(!getSpincastConfig().isDebugEnabled() && !"local".equals(getSpincastConfig().getEnvironmentName()) &&
           "localhost".equals(getSpincastConfig().getPublicServerHost()) &&
           getSpincastConfig().isValidateLocalhostHost()) {
            throw new RuntimeException("Did you forget to override the SpincastConfig#getPublicServerSchemeHostPort() " +
                                       "method? The application was started on an environment other than 'local', with the debug mode disabled, " +
                                       "but the host returned by SpincastConfig#getPublicServerSchemeHostPort() is 'localhost'... Make " +
                                       "sure the host specified in this config is the *public* one. Note : you can disable this validation " +
                                       "by changing the SpincastConfig#isValidateLocalhostHost() config.");
        }
    }

}
