package org.spincast.testing.junitrunner.tests.utils;

import java.util.HashSet;
import java.util.Set;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.spincast.testing.junitrunner.SpincastJUnitRunner;

/** 
 * Class that extends SpincastJUnitRunner only to 
 * be able to test it.
 */
public class SpincastJUnitRunnerTester extends SpincastJUnitRunner {

    private final Set<String> testsFailed = new HashSet<String>();

    public SpincastJUnitRunnerTester(Class<?> klass) throws InitializationError {
        super(klass);
    }

    protected Set<String> getTestsFailed() {
        return this.testsFailed;
    }

    protected String getTestExpectedToFail() {

        String testExpectedToFail = null;
        if(getTestClassInstance() instanceof TestExpectedToFailProvider) {
            testExpectedToFail = ((TestExpectedToFailProvider)getTestClassInstance()).getTestExpectedToFail();
        }

        return testExpectedToFail;
    }

    @Override
    protected void spincastTestError(String testName, Throwable exception) {

        this.testsFailed.add(testName);

        //==========================================
        // Are we expecting this test to fail?
        //==========================================
        String testExpectedToFail = getTestExpectedToFail();
        if(testExpectedToFail != null && testExpectedToFail.equals(testName)) {
            setIgnoreRemainingTests();
            return;
        }

        super.spincastTestError(testName, exception);
    }

    @Override
    public void run(RunNotifier notifier) {

        super.run(notifier);

        String testExpectedToFail = getTestExpectedToFail();
        if(testExpectedToFail != null && !getTestsFailed().contains(testExpectedToFail)) {

            String errorMessage = "[Spincast] This test was expected to fail but didn't: " + testExpectedToFail;
            Description description = Description.createTestDescription(getTestClass().getJavaClass(),
                                                                        errorMessage);

            getRunNotifier().fireTestStarted(description);
            getRunNotifier().fireTestFailure(new Failure(description, new RuntimeException(errorMessage)));
            getRunNotifier().fireTestFinished(description);
        }
    }

}
