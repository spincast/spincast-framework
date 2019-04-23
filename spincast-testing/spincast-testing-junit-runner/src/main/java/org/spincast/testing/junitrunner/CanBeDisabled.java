package org.spincast.testing.junitrunner;

/**
 * Allows you to ignore a tests file, by making the
 * {@link #isTestsFileEnabled()} method return <code>true</code>.
 * <p>
 * To use with {@link org.spincast.testing.junitrunner.SpincastJUnitRunner SpincastJUnitRunner}.
 */
public interface CanBeDisabled {

    /**
     * Is the tests file disabled?
     */
    public boolean isTestsFileDisabled();

}
