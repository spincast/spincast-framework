package org.spincast.testing.utils;

public interface RepeatedClassAfterMethodProvider {

    /**
     * Called after all the loops of the class
     * as specified by the @repeat annotation.
     * <p>
     * This will only be called if the beforeClass()
     * method completed successfully, so you can be sure that
     * the instanciation of the class is complete here.
     * </p>
     */
    public void afterClassLoops();
}
