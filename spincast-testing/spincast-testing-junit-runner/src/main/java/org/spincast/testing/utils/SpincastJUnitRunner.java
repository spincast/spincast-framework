package org.spincast.testing.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Spincast JUnit Runner.
 * <p>
 * Only creates <em>one</em> instance of the test class for all
 * the tests.
 * </p>
 * <p>
 * If the class is annotated with {@link BeforeAfterClassMethodsProvider}, then
 * a <code>beforeClass()</code> and <code>afterClass()</code> methods will be
 * called.
 * </p>
 * <p>
 * You can use the {@link ExpectingBeforeClassException} annotation on the test
 * class to indicate that an exception is expected from the <code>beforeClass()</code>
 * method.
 * </p>
 * <p>
 * If you try to debug a test that only fails <em>sometimes</em> (those are the
 * worst!), you can use the {@link Repeat} annotation on the test or on
 * its test class. This allows you to run the test or the whole test class multiple times.
 * </p>
 */
public class SpincastJUnitRunner extends BlockJUnit4ClassRunner {

    protected final Logger logger = LoggerFactory.getLogger(SpincastJUnitRunner.class);

    public final static String SPINCAST_TEST_NAME_BEFORE_CLASS_ANNOTATIONS_VALIDATION =
            "[Spincast] @BeforeClass annotations validation";

    public final static String SPINCAST_TEST_NAME_AFTER_CLASS_ANNOTATIONS_VALIDATION =
            "[Spincast] @AfterClass annotations validation";

    public final static String SPINCAST_TEST_NAME_BEFORE_CLASS_METHOD_VALIDATION =
            "[Spincast] beforeClass method validation";

    public final static String SPINCAST_TEST_NAME_AFTER_CLASS_METHOD_VALIDATION =
            "[Spincast] afterClass method validation";

    public final static String SPINCAST_TEST_NAME_NO_TESTS_AND_NO_EXPECTION_EXCEPTION_ANNOTATION =
            "[Spincast] No tests and no @" + ExpectingBeforeClassException.class.getSimpleName() + " validation";

    public final static String SPINCAST_TEST_NAME_AFTER_CLASS_LOOPS_EXCEPTION =
            "[Spincast] afterClassLoops method validation";

    private Object testClassInstance = null;
    private boolean exceptionInBeforeClass = false;
    private Boolean isExpectingBeforeClassException = null;
    private boolean atLeastOneTestFailed = false;
    private RunNotifier runNotifier = null;
    private int currentClassLoopPosition = 1;
    private boolean ignoreRemainingTests = false;

    public SpincastJUnitRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    protected RunNotifier getRunNotifier() {
        return this.runNotifier;
    }

    protected void setIgnoreRemainingTests() {
        this.ignoreRemainingTests = true;
    }

    protected boolean isIgnoreRemainingTests() {
        return this.ignoreRemainingTests;
    }

    protected void setExceptionInBeforeClass() {
        this.exceptionInBeforeClass = true;
    }

    protected boolean isExceptionInBeforeClass() {
        return this.exceptionInBeforeClass;
    }

    protected int getCurrentClassLoopPosition() {
        return this.currentClassLoopPosition;
    }

    protected void setCurrentClassLoopPosition(int currentClassLoopPosition) {
        this.currentClassLoopPosition = currentClassLoopPosition;
    }

    /**
     * JUnit calls that method to create an instance of the 
     * test class for each individual test. We change this
     * behavior and always return the *same* instance of the
     * test class: 'testClassInstance'.
     */
    @Override
    public Object createTest() throws Exception {

        if (this.testClassInstance == null) {
            this.testClassInstance = getTestClass().getOnlyConstructor().newInstance();
        }
        return this.testClassInstance;
    }

    protected Object getTestClassInstance() {
        try {
            return createTest();
        } catch (Exception ex) {
            throw(ex instanceof RuntimeException) ? (RuntimeException)ex : new RuntimeException(ex);
        }
    }

    /**
     * Test class display name.
     */
    @Override
    protected String getName() {

        String name = super.getName();

        int loopsNbr = getTestClassLoopsNbr();
        if (loopsNbr > 1) {
            name = name + " [" + loopsNbr + " loops]";
        }

        return name;
    }

    @Override
    protected final List<FrameworkMethod> computeTestMethods() {

        //==========================================
        // No tests are required if we simply validate 
        // that an exception must occure in the
        // beforeClass() method.
        //
        // But JUnit required at least one test to
        // exist, so we create a dummy one.
        //==========================================
        List<FrameworkMethod> tests = super.computeTestMethods();
        if ((tests == null || tests.size() == 0)) {

            tests = new ArrayList<FrameworkMethod>(tests);
            tests.add(new NoTestsFrameworkMethod());
        }

        return tests;
    }

    /**
     * Runs the test class.
     */
    @Override
    public void run(final RunNotifier notifier) {

        this.runNotifier = notifier;

        //==========================================
        // We add a testFailure listener in case the 
        // Test class implements TestFailureListener
        // to be informed of any failing test.
        //==========================================
        addTestFailureListener(notifier);

        //==========================================
        // Run some initial validation tests
        //==========================================
        runPreClassLoopsSpincastTests();

        try {

            //==========================================
            // How many loops of the class should we make?
            //==========================================
            int loopsNbr = getTestClassLoopsNbr();
            int sleep = getTestClassLoopsSleep();

            try {

                //==========================================
                // Executes the loops
                //==========================================
                for (int i = 0; i < loopsNbr; i++) {

                    setCurrentClassLoopPosition(i + 1);

                    if (loopsNbr > 1) {
                        this.logger.info("Running loop " + getCurrentClassLoopPosition() + "/" + loopsNbr + " of " +
                                         "test class " + getTestClass().getJavaClass().getName());
                    }

                    //==========================================
                    // We set the test class instance to null so 
                    // a new instance is created for each loop.
                    //==========================================
                    this.testClassInstance = null;

                    //==========================================
                    // Calls the "beforeClass()" method
                    //==========================================
                    if (getTestClassInstance() instanceof BeforeAfterClassMethodsProvider) {

                        try {

                            ((BeforeAfterClassMethodsProvider)getTestClassInstance()).beforeClass();

                            if (isExpectingBeforeClassException()) {
                                String msg = "An exception was expected in the 'beforeClass()' method since " +
                                             "the @" + ExpectingBeforeClassException.class.getSimpleName() +
                                             " is used on the class, but none occured.";
                                spincastTestError(SPINCAST_TEST_NAME_BEFORE_CLASS_METHOD_VALIDATION, msg);
                            }
                        } catch (Throwable ex) { // assertions are Errors, not Exceptions

                            setExceptionInBeforeClass();
                            setIgnoreRemainingTests();

                            if (!isExpectingBeforeClassException()) {
                                spincastTestError(SPINCAST_TEST_NAME_BEFORE_CLASS_METHOD_VALIDATION, ex);
                            }

                            try {
                                ((BeforeAfterClassMethodsProvider)getTestClassInstance()).beforeClassException(ex);
                            } catch (Exception ex2) {
                                this.logger.error("Error managing the 'beforeClass' exception : ", ex2);
                            }
                        }
                    } else {

                        if (isExpectingBeforeClassException()) {
                            String msg = "The @" + ExpectingBeforeClassException.class.getSimpleName() + " annotation " +
                                         "can only be used on a class implementing the " +
                                         BeforeAfterClassMethodsProvider.class.getSimpleName() + " interface.";
                            spincastTestError(SPINCAST_TEST_NAME_BEFORE_CLASS_METHOD_VALIDATION, msg);
                        }
                    }

                    //==========================================
                    // Runs the regular tests
                    //==========================================
                    super.run(notifier);

                    //==========================================
                    // Calls the "afterClass()" method, if required
                    //==========================================
                    if (!isIgnoreRemainingTests() && getTestClassInstance() instanceof BeforeAfterClassMethodsProvider) {

                        try {
                            ((BeforeAfterClassMethodsProvider)getTestClassInstance()).afterClass();
                        } catch (Throwable ex) { // assertions are Errors, not Exceptions
                            spincastTestError(SPINCAST_TEST_NAME_AFTER_CLASS_METHOD_VALIDATION, ex);
                            break;
                        }
                    }

                    if (this.atLeastOneTestFailed || i >= (loopsNbr - 1)) {
                        break;
                    }

                    //==========================================
                    // Sleep
                    //==========================================
                    if (sleep > 0) {
                        try {
                            Thread.sleep(sleep);
                        } catch (Exception ex) {
                            //....
                        }
                    }
                }
            } finally {

                if (getTestClassInstance() instanceof RepeatedClassAfterMethodProvider) {
                    if (!isExceptionInBeforeClass()) {
                        try {
                            ((RepeatedClassAfterMethodProvider)getTestClassInstance()).afterClassLoops();
                        } catch (Throwable ex) { // assertions are Errors, not Exceptions
                            spincastTestError(SPINCAST_TEST_NAME_AFTER_CLASS_LOOPS_EXCEPTION, ex);
                        }
                    } else {
                        this.logger.info("An exception occured in the 'beforeClass()' method, " +
                                         "so the 'afterClassLoops()' method won't be called.");
                    }
                }
            }

        } catch (Exception ex) {
            throw(ex instanceof RuntimeException) ? (RuntimeException)ex : new RuntimeException(ex);
        } finally {

            int classLoopsNbr = getTestClassLoopsNbr();
            if (this.atLeastOneTestFailed && classLoopsNbr > 1) {
                this.logger.error("The test failure occured during the class loop #" + getCurrentClassLoopPosition());
            }
        }
    }

    /**
     * Runs a specific test
     */
    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {

        if (NoTestsFrameworkMethod.class.getName().equals(method.getDeclaringClass().getName())) {
            Description description = describeChild(method);
            notifier.fireTestStarted(description);
            notifier.fireTestFinished(description);
            return;
        }

        if (isIgnoreRemainingTests()) {
            Description description = describeChild(method);
            notifier.fireTestIgnored(description);
            return;
        }

        //==========================================
        // How many times should we run the test?
        //==========================================
        int sleep = getMethodLoopsSleep(method.getMethod());
        int loopsNbr = getMethodLoopsNbr(method.getMethod());
        for (int i = 0; i < loopsNbr; i++) {

            if (loopsNbr > 1) {
                this.logger.info("Execution " + (i + 1) + "/" + loopsNbr + " of " +
                                 "test " + method.getMethod().getName() + " from " +
                                 "test class " + getTestClass().getJavaClass().getName());
            }

            super.runChild(method, notifier);

            if (loopsNbr > 1 && this.atLeastOneTestFailed) {
                this.logger.error("The test \"" + method.getMethod().getName() + "\" failed during the loop #" + (i + 1));
                break;
            }

            if (i >= (loopsNbr - 1)) {
                break;
            }

            if (sleep > 0) {
                try {
                    Thread.sleep(sleep);
                } catch (Exception ex) {
                    //....
                }
            }
        }
    }

    protected void addTestFailureListener(RunNotifier notifier) {
        notifier.addListener(new RunListener() {

            @Override
            public void testFailure(Failure failure) throws Exception {
                SpincastJUnitRunner.this.atLeastOneTestFailed = true;

                logTestFailure(failure);

                if (SpincastJUnitRunner.this.testClassInstance instanceof TestFailureListener) {
                    ((TestFailureListener)SpincastJUnitRunner.this.testClassInstance).testFailure(failure);
                }
            }
        });
    }

    protected void logTestFailure(Failure failure) {
        System.err.println(getStackTrace(failure.getException()));
    }

    protected void runPreClassLoopsSpincastTests() {
        validateNoBeforeClassAnnotations();
        validateNoAfterClassAnnotations();
        validateNoTestsAndNoExpectingBeforeClassExceptionAnnotation();
    }

    protected void validateNoBeforeClassAnnotations() {
        List<FrameworkMethod> befores = getTestClass().getAnnotatedMethods(BeforeClass.class);
        if (befores != null && befores.size() > 0) {
            String msg = "The @BeforeClass JUnit annotation can't be used with the " +
                         SpincastJUnitRunner.class.getSimpleName() + " " +
                         "custom runner. Use the beforeClass() method instead by implementing the " +
                         BeforeAfterClassMethodsProvider.class.getSimpleName() + " interface.";

            spincastTestError(SPINCAST_TEST_NAME_BEFORE_CLASS_ANNOTATIONS_VALIDATION, msg);
        }
    }

    protected void validateNoAfterClassAnnotations() {
        List<FrameworkMethod> afters = getTestClass().getAnnotatedMethods(AfterClass.class);
        if (afters != null && afters.size() > 0) {
            String msg = "The @AfterClass JUnit annotation can't be used with the " +
                         SpincastJUnitRunner.class.getSimpleName() + " " +
                         "custom runner. Use the afterClass() method instead by implementing the " +
                         BeforeAfterClassMethodsProvider.class.getSimpleName() + " interface.";
            spincastTestError(SPINCAST_TEST_NAME_AFTER_CLASS_ANNOTATIONS_VALIDATION, msg);
        }
    }

    protected void validateNoTestsAndNoExpectingBeforeClassExceptionAnnotation() {

        List<FrameworkMethod> regularTests = SpincastJUnitRunner.super.computeTestMethods();
        if ((regularTests == null || regularTests.size() == 0) && !isExpectingBeforeClassException()) {
            String msg =
                    "There must be at least one @Test or the @" + ExpectingBeforeClassException.class.getSimpleName() + " must " +
                         "be present.";
            spincastTestError(SPINCAST_TEST_NAME_NO_TESTS_AND_NO_EXPECTION_EXCEPTION_ANNOTATION, msg);
        }
    }

    protected void spincastTestError(String testName, String errorMessage) {
        spincastTestError(testName, new RuntimeException(errorMessage));
    }

    protected void spincastTestError(String testName, Throwable exception) {

        this.logger.error("Test error", exception);

        Description description = Description.createTestDescription(getTestClass().getJavaClass(),
                                                                    testName);

        setIgnoreRemainingTests();
        getRunNotifier().fireTestStarted(description);
        getRunNotifier().fireTestFailure(new Failure(description, exception));
        getRunNotifier().fireTestFinished(description);
    }

    protected String getStackTrace(Throwable ex) {

        if (ex == null) {
            return "";
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        return sw.toString();
    }

    public boolean isExpectingBeforeClassException() {
        if (this.isExpectingBeforeClassException == null) {
            this.isExpectingBeforeClassException = getTestClass().getAnnotation(ExpectingBeforeClassException.class) != null;
        }
        return this.isExpectingBeforeClassException;
    }

    protected int getTestClassLoopsNbr() {
        return getLoopsNbr(getTestClass().getAnnotation(Repeat.class));
    }

    protected int getMethodLoopsNbr(Method method) {
        return getLoopsNbr(method.getAnnotation(Repeat.class));
    }

    protected int getLoopsNbr(Repeat repeatAnnotation) {

        int loopsNbr = 1;
        if (repeatAnnotation != null) {
            loopsNbr = repeatAnnotation.value();
            if (loopsNbr < 1) {
                loopsNbr = 1;
            }
        }
        return loopsNbr;
    }

    protected int getMethodLoopsSleep(Method method) {
        return getLoopsSleep(method.getAnnotation(Repeat.class));
    }

    protected int getTestClassLoopsSleep() {
        return getLoopsSleep(getTestClass().getAnnotation(Repeat.class));
    }

    protected int getLoopsSleep(Repeat repeatAnnotation) {

        int sleep = 0;
        if (repeatAnnotation != null) {
            sleep = repeatAnnotation.sleep();
        }
        return sleep;
    }

}
