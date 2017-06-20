package org.spincast.testing.utils;

/**
 * Provides methods that can be called before and after tests from a 
 * class are ran.
 * 
 * To use with {@link org.spincast.testing.utils.SpincastJUnitRunner SpincastJUnitRunner}.
 */
public interface BeforeAfterClassMethodsProvider {

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

    /**
     * This method will be called if an exception occures during
     * the {@link #beforeClass} execution.
     * 
     * Be careful!! Here, you are pretty much certain that
     * the instanciation of the class was not succesful, so you
     * can't use any of its methods! 
     */
    public void beforeClassException(Throwable ex);

}
