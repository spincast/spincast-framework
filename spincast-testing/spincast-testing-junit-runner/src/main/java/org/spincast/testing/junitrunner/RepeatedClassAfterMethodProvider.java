package org.spincast.testing.junitrunner;

public interface RepeatedClassAfterMethodProvider {

    /**
     * Called after all the loops of the class
     * as specified by the {@link RepeatUntilFailure} or
     * {@link RepeatUntilSuccess} annotations.
     * <p>
     * This will only be called if the beforeClass()
     * method completed successfully, so you can be sure that
     * the instanciation of the class is complete here.
     *
     */
    public void afterClassLoops();
}
