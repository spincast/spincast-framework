package org.spincast.testing.junitrunner;

import java.util.Collection;

import org.junit.runners.model.FrameworkMethod;

/**
 * Allows you to ignore a test class entirely.
 * <p>
 * To use with {@link org.spincast.testing.junitrunner.SpincastJUnitRunner SpincastJUnitRunner}.
 */
public interface CanBeDisabled {

    /**
     * Should the tests class be disabled?
     * <p>
     * Note that this will be run <em>before</em> everything
     * (including {@link #beforeClass()}):
     * no Guice context is available... But you can look
     * at System properties, for example as a way of
     * finding if the file must be ran or not.
     * <p>
     * Use {@link #isTestClassDisabledPostBeforeClass()} if
     * you need the Guice context in order to perform
     * your logic.
     * <p>
     * @param filteredTests the tests that are going to be run.
     */
    public boolean isTestClassDisabledPreBeforeClass(Collection<FrameworkMethod> filteredTests);

    /**
     * Should the tests of this class all be disabled?
     * <p>
     * Note that this will be run <em>after</em>
     * {@link #beforeClass()}: the Guice context
     * is created and available.
     * <p>
     * If you need to disable the tests because on
     * some environments you are not even able to create
     * the Guice context, use
     * {@link #isTestClassDisabledPreBeforeClass()}
     * instead.
     */
    public boolean isTestClassDisabledPostBeforeClass();


}
