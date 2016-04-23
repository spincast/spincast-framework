package org.spincast.testing.utils;

/**
 * Provides methods that can be called before and after tests from a 
 * class are ran.
 * 
 * To use with {@link org.spincast.testing.utils.OneInstancePerClassJUnitRunner OneInstancePerClassJUnitRunner}.
 */
public interface IBeforeAfterClassMethodsProvider {

    /**
     * Called before the tests of the class are ran.
     */
    public void beforeClass();

    /**
     * Called after the tests of the class are ran.
     */
    public void afterClass();
}
