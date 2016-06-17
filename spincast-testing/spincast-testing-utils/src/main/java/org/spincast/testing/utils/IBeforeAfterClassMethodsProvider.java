package org.spincast.testing.utils;

/**
 * Provides methods that can be called before and after tests from a 
 * class are ran.
 * 
 * To use with {@link org.spincast.testing.utils.SpincastJUnitRunner SpincastJUnitRunner}.
 */
public interface IBeforeAfterClassMethodsProvider {

    /**
     * Called before the tests of the class are ran.
     */
    public void beforeClass();

    /**
     * Called after the tests of the class are ran.
     * <p>
     * This will only be called if the beforeClass()
     * method completed successfully, so you can be sure that
     * the instanciation of the class is complete here.
     * </p>
     */
    public void afterClass();

}
