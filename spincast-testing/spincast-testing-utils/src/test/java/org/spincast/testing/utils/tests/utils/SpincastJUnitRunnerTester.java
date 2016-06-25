package org.spincast.testing.utils.tests.utils;

import org.junit.runner.Description;
import org.junit.runners.model.InitializationError;
import org.spincast.testing.utils.SpincastJUnitRunner;

/** 
 * Class that extends SpincastJUnitRunner only to be able to test it.
 * It simply considere some tests are having failed when they have
 * succeded and succeded when they have failed.
 */
public class SpincastJUnitRunnerTester extends SpincastJUnitRunner {

    public SpincastJUnitRunnerTester(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected void spincastTestSuccess(Description description) {

        if(isShouldInvertResult(description)) {
            super.spincastTestError(description, new RuntimeException("Unexpected success"));
        } else {
            super.spincastTestSuccess(description);
        }
    }

    @Override
    protected void spincastTestError(Description description, Throwable exception) {

        //==========================================
        // We should still ignore remaining tests!
        //==========================================
        setIgnoreRemainingTests();

        if(isShouldInvertResult(description)) {
            super.spincastTestSuccess(description);
        } else {
            super.spincastTestError(description, exception);
        }
    }

    protected boolean isShouldInvertResult(Description description) {

        if((SPINCAST_TEST_NAME_NO_TESTS_AND_NO_EXPECTION_EXCEPTION_ANNOTATION.equals(description.getMethodName()) &&
            (getTestClassInstance() instanceof ITestingNoTestAndNoExpectingBeforeClassException)) ||
           (SPINCAST_TEST_NAME_BEFORE_CLASS_ANNOTATIONS_VALIDATION.equals(description.getMethodName()) &&
            (getTestClassInstance() instanceof ITestingBeforeClassAnnotationAreInvalid)) ||
           (SPINCAST_TEST_NAME_AFTER_CLASS_ANNOTATIONS_VALIDATION.equals(description.getMethodName()) &&
            (getTestClassInstance() instanceof ITestingAfterClassAnnotationAreInvalid)) ||
           (SPINCAST_TEST_NAME_EXPECTING_BEFORE_CLASS_EXCEPTION_VALIDATION.equals(description.getMethodName()) &&
            (getTestClassInstance() instanceof ITestingBeforeClassShouldHaveThrowAnException)) ||
           (SPINCAST_TEST_NAME_EXPECTING_BEFORE_CLASS_EXCEPTION_VALIDATION.equals(description.getMethodName()) &&
            (getTestClassInstance() instanceof ITestingBeforeClassShouldNotHaveThrowAnException))) {
            return true;
        }

        return false;
    }

}
