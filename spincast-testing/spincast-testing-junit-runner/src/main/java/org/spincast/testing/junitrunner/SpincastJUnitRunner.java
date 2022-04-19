package org.spincast.testing.junitrunner;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Spincast JUnit Runner.
 * <p>
 * Only creates <em>one</em> instance of the test class for all
 * its tests.
 * <p>
 * If the class implements {@link BeforeAfterClassMethodsProvider}, then
 * a <code>beforeClass()</code> and <code>afterClass()</code> methods will be
 * called.
 * <p>
 * You can use the {@link ExpectingBeforeClassException} annotation on the test
 * class to indicate that an exception is expected from the <code>beforeClass()</code>
 * method.
 * <p>
 * If you try to debug a test that only fails <em>sometimes</em> (those are the
 * worst!), you can use the {@link RepeatUntilFailure @RepeatUntilFail} annotation on the test or on
 * its test class. This allows you to run the test or the whole test class multiple times.
 * <p>
 * You can also use {@link RepeatUntilSuccess @RepeatUntilSuccess} instead to repeat
 * the test class (or a single test) multiple time until it succeeds (or the maximum
 * number of tries is reached).
 */
public class SpincastJUnitRunner extends BlockJUnit4ClassRunner {

    protected static final Logger logger = LoggerFactory.getLogger(SpincastJUnitRunner.class);

    public final static String SPINCAST_TEST_NAME_BEFORE_CLASS_ANNOTATIONS_VALIDATION =
            "[Spincast] @" + BeforeClass.class.getSimpleName() + " annotations validation";

    public final static String SPINCAST_TEST_NAME_AFTER_CLASS_ANNOTATIONS_VALIDATION =
            "[Spincast] @" + AfterClass.class.getSimpleName() + " annotations validation";

    public final static String SPINCAST_TEST_NAME_BEFORE_CLASS_METHOD_VALIDATION =
            "[Spincast] beforeClass method validation";

    public final static String SPINCAST_TEST_NAME_AFTER_CLASS_METHOD_VALIDATION =
            "[Spincast] afterClass method validation";

    public final static String SPINCAST_TEST_NAME_NO_TESTS_AND_NO_EXPECTION_EXCEPTION_ANNOTATION =
            "[Spincast] No tests and no @" + ExpectingBeforeClassException.class.getSimpleName() + " validation";

    public final static String SPINCAST_TEST_NAME_AFTER_CLASS_LOOPS_EXCEPTION =
            "[Spincast] afterClassLoops method validation";

    public final static String SPINCAST_TEST_NAME_REPEAT_ANNOTATIONS_VALIDATION =
            "[Spincast] @" + RepeatUntilSuccess.class.getSimpleName() + " and @" + RepeatUntilFailure.class.getSimpleName() +
                                                                                  " annotations validation";

    public final static String SPINCAST_TEST_NAME_EXPECTING_FAILURE_BUT_ONLY_SUCCESSES =
            "[Spincast] @" + ExpectingFailure.class.getSimpleName() + " annotation but only successes!";

    private Object testClassInstance = null;
    private boolean exceptionInBeforeClass = false;
    private Boolean isExpectingBeforeClassException = null;
    private Boolean isExpectingFailure = null;
    private boolean currentTestFailed = false;
    private boolean atLeastOneTestFailedInAllLoops = false;
    private boolean atLeastOneTestFailedInCurrentLoop = false;
    private boolean stopLoopsForced = false;
    private RunNotifier runNotifier = null;
    private int currentClassLoopPosition = 1;
    private boolean ignoreRemainingTests = false;
    private boolean isLastLoop = false;
    private boolean isLastSingleTestLoop = false;

    public SpincastJUnitRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
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

        Integer repeatUntilFailureLoopsNbr = getTestClassRepeatUntilFailureAnnotationLoopsNbr();
        Integer repeatUntilSuccessLoopsNbr = getTestClassRepeatUntilSuccessAnnotationLoopsNbr();

        Integer loopsNbr = 1;
        if (repeatUntilFailureLoopsNbr != null) {
            loopsNbr = repeatUntilFailureLoopsNbr;
        } else if (repeatUntilSuccessLoopsNbr != null) {
            loopsNbr = repeatUntilSuccessLoopsNbr;
        }

        if (loopsNbr > 1) {
            name = name + " [" + loopsNbr + " loops]";
        }

        return name;
    }

    @Override
    protected final List<FrameworkMethod> computeTestMethods() {

        //==========================================
        // No tests are required if we simply validate
        // that an exception must occur in the
        // beforeClass() method.
        //
        // But JUnit required at least one test to
        // be defined, so we create a dummy one.
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
        this.stopLoopsForced = false;
        this.isLastLoop = false;

        //==========================================
        // If the tests class disabled?
        //==========================================
        if (getTestClassInstance() instanceof CanBeDisabled) {

            //==========================================
            // This is a hack to know what are the tests
            // that are going to be run. We get those to
            // pass them to "isTestClassDisabledPreBeforeClass"
            // so they can be used to determine if the tests
            // class must be ignored.
            //==========================================
            Collection<FrameworkMethod> filteredTests = null;
            try {
                Method getFilteredChildrenMethod = ParentRunner.class.getDeclaredMethod("getFilteredChildren");
                getFilteredChildrenMethod.setAccessible(true);
                @SuppressWarnings("unchecked")
                Collection<FrameworkMethod> temp = (Collection<FrameworkMethod>)getFilteredChildrenMethod.invoke(this);
                filteredTests = temp;
            } catch (Exception ex) {
                ex.printStackTrace();
                return;
            }

            if (((CanBeDisabled)getTestClassInstance()).isTestClassDisabledPreBeforeClass(filteredTests)) {
                logger.info("Test class disabled (pre 'beforeClass')! Skipping...");
                return;
            }
        }

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
            Integer repeatUntilFailureLoopsNbr = getTestClassRepeatUntilFailureAnnotationLoopsNbr();
            Integer repeatUntilFailureSleep = getTestClassRepeatUntilFailureAnnotationLoopsSleep();
            Integer repeatUntilSuccessLoopsNbr = getTestClassRepeatUntilSuccessAnnotationLoopsNbr();
            Integer repeatUntilSuccessSleep = getTestClassRepeatUntilSuccessAnnotationLoopsSleep();

            int loopNbr = 1;
            if (repeatUntilFailureLoopsNbr != null) {
                loopNbr = repeatUntilFailureLoopsNbr;
            } else if (repeatUntilSuccessLoopsNbr != null) {
                loopNbr = repeatUntilSuccessLoopsNbr;
            }

            int sleep = 0;
            if (repeatUntilFailureSleep != null) {
                sleep = repeatUntilFailureSleep;
            } else if (repeatUntilSuccessSleep != null) {
                sleep = repeatUntilSuccessSleep;
            }

            try {

                //==========================================
                // Executes the loops
                //==========================================
                for (int i = 0; i < loopNbr; i++) {

                    this.currentClassLoopPosition = i + 1;

                    //==========================================
                    // Reset current loop errors
                    //==========================================
                    this.atLeastOneTestFailedInCurrentLoop = false;

                    //==========================================
                    // Is it the last loop?
                    //==========================================
                    this.isLastLoop = (i == (loopNbr - 1));

                    if (loopNbr > 1) {
                        logger.info("Running loop " + getCurrentClassLoopPosition() + "/" + loopNbr + " of " +
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
                                logger.error("Error managing the 'beforeClass' exception : ", ex2);
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
                    // If the tests class disabled?
                    //==========================================
                    if (getTestClassInstance() instanceof CanBeDisabled &&
                        ((CanBeDisabled)getTestClassInstance()).isTestClassDisabledPostBeforeClass()) {
                        logger.info("Test class disabled (post 'beforeClass')! Skipping...");
                        return;
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

                    if (this.stopLoopsForced || i >= (loopNbr - 1)) {
                        break;
                    }

                    if (this.atLeastOneTestFailedInCurrentLoop) {
                        if (repeatUntilFailureLoopsNbr != null) {
                            break;
                        }
                    } else {
                        if (repeatUntilSuccessLoopsNbr != null) {
                            break;
                        }
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

                //==========================================
                // Failures were expected?
                //==========================================
                if (isExpectingFailure() && !this.atLeastOneTestFailedInAllLoops) {
                    String msg = "There were no failures but we were expecting some.";
                    spincastTestError(SPINCAST_TEST_NAME_EXPECTING_FAILURE_BUT_ONLY_SUCCESSES, msg);
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
                        logger.info("An exception occured in the 'beforeClass()' method, " +
                                    "so the 'afterClassLoops()' method won't be called.");
                    }
                }
            }

        } catch (Exception ex) {
            throw(ex instanceof RuntimeException) ? (RuntimeException)ex : new RuntimeException(ex);
        } finally {

            Integer classLoopsNbr = 1;
            if (getTestClassRepeatUntilFailureAnnotationLoopsNbr() != null) {
                classLoopsNbr = getTestClassRepeatUntilFailureAnnotationLoopsNbr();
            } else if (getTestClassRepeatUntilSuccessAnnotationLoopsNbr() != null) {
                classLoopsNbr = getTestClassRepeatUntilSuccessAnnotationLoopsNbr();
            }
            if (this.atLeastOneTestFailedInAllLoops && classLoopsNbr != null && classLoopsNbr > 1) {
                logger.error("The test failure occured during the class loop #" + getCurrentClassLoopPosition());
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

        this.isLastSingleTestLoop = false;

        //==========================================
        // How many times should we run the test?
        //==========================================
        Method methodObj = method.getMethod();
        Integer repeatUntilFailureLoopsNbr = getMethodRepeatUntilFailureAnnotationLoopsNbr(methodObj);
        Integer repeatUntilFailureSleep = getMethodRepeatUntilFailureAnnotationLoopsSleep(methodObj);
        Integer repeatUntilSuccessLoopsNbr = getMethodRepeatUntilSuccessAnnotationLoopsNbr(methodObj);
        Integer repeatUntilSuccessSleep = getMethodRepeatUntilSuccessAnnotationLoopsSleep(methodObj);

        int loopNbr = 1;
        if (repeatUntilFailureLoopsNbr != null && repeatUntilSuccessLoopsNbr != null) {
            String msg = "Only one of the @" + RepeatUntilSuccess.class.getSimpleName() + " or @" +
                         RepeatUntilFailure.class.getSimpleName() + " annotation can be used at the time.";
            spincastTestError(SPINCAST_TEST_NAME_REPEAT_ANNOTATIONS_VALIDATION, msg);
            return;
        } else if (repeatUntilFailureLoopsNbr != null) {
            loopNbr = repeatUntilFailureLoopsNbr;
        } else if (repeatUntilSuccessLoopsNbr != null) {
            loopNbr = repeatUntilSuccessLoopsNbr;
        }


        int sleep = 0;
        if (repeatUntilFailureSleep != null) {
            sleep = repeatUntilFailureSleep;
        } else if (repeatUntilSuccessSleep != null) {
            sleep = repeatUntilSuccessSleep;
        }

        for (int i = 0; i < loopNbr; i++) {

            //==========================================
            // Is the last single test loop?
            //==========================================
            this.isLastSingleTestLoop = (i == (loopNbr - 1));

            if (loopNbr > 1) {
                logger.info("Execution " + (i + 1) + "/" + loopNbr + " of " +
                            "test " + method.getMethod().getName() + " from " +
                            "test class " + getTestClass().getJavaClass().getName());
            }

            //==========================================
            // Reset current test error
            //==========================================
            this.currentTestFailed = false;

            super.runChild(method, notifier);

            if (this.stopLoopsForced || i >= (loopNbr - 1)) {
                break;
            }

            if (this.currentTestFailed) {
                if (repeatUntilFailureLoopsNbr != null) {
                    logger.error("The test \"" + method.getMethod().getName() + "\" failed during the loop #" + (i + 1));
                    break;
                }
            } else {
                if (repeatUntilSuccessLoopsNbr != null) {
                    logger.info("The test \"" + method.getMethod().getName() + "\" succeeded during the loop #" + (i + 1));
                    break;
                }
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

    @Override
    protected Statement methodBlock(FrameworkMethod method) {
        return customizeStatement(super.methodBlock(method), method);
    }

    protected Statement customizeStatement(Statement statement, FrameworkMethod method) {
        statement = addCustomErrorHandling(statement, method);
        return statement;
    }

    protected Statement addCustomErrorHandling(Statement baseStatement, FrameworkMethod method) {

        Statement statement = new Statement() {

            @Override
            public void evaluate() throws Throwable {
                try {
                    baseStatement.evaluate();
                } catch (Throwable ex) {

                    Description description = describeChild(method);
                    if (description.getAnnotation(IgnoreErrorButStopLoops.class) != null) {
                        //==========================================
                        // Bypass regular JUnit error handling code
                        // but set "stopLoopsForced" to true.
                        //==========================================
                        SpincastJUnitRunner.this.stopLoopsForced = true;
                        logger.info("The test failed, but is annotated with @" +
                                    IgnoreErrorButStopLoops.class.getSimpleName() + " so " +
                                    "we don't show it as an error but stop any test loops.");


                    } else if (((getTestClassRepeatUntilSuccessAnnotationLoopsNbr() != null ||
                                 getMethodRepeatUntilSuccessAnnotationLoopsNbr(method.getMethod()) != null) &&
                                !isLastInstanceOfThisTestToRun()) ||
                               isExpectingFailure()) {
                        //==========================================
                        // Bypass regular JUnit error handling code
                        // and only use our custom handling.
                        //==========================================
                        testFailureCustomHandling(new Failure(description, ex));

                    } else {

                        if (getTestClassRepeatUntilSuccessAnnotationLoopsNbr() != null) {
                            String msg = "The @" + RepeatUntilSuccess.class.getSimpleName() + " annotation " +
                                         "was used on the test class but there was no success after " +
                                         getTestClassRepeatUntilSuccessAnnotationLoopsNbr() +
                                         " loops.";
                            ex = new RuntimeException(msg, ex);
                        } else if (getMethodRepeatUntilSuccessAnnotationLoopsNbr(method.getMethod()) != null) {
                            String msg = "The @" + RepeatUntilSuccess.class.getSimpleName() + " annotation " +
                                         "was used on the test method but there was no success after " +
                                         getMethodRepeatUntilSuccessAnnotationLoopsNbr(method.getMethod()) +
                                         " loops.";
                            ex = new RuntimeException(msg, ex);
                        }

                        throw ex;
                    }
                }
            }
        };

        return statement;
    }

    protected void addTestFailureListener(RunNotifier notifier) {
        notifier.addListener(new RunListener() {

            @Override
            public void testFailure(Failure failure) throws Exception {
                testFailureCustomHandling(failure);
            }
        });
    }

    protected void testFailureCustomHandling(Failure failure) {
        SpincastJUnitRunner.this.currentTestFailed = true;
        SpincastJUnitRunner.this.atLeastOneTestFailedInCurrentLoop = true;
        SpincastJUnitRunner.this.atLeastOneTestFailedInAllLoops = true;

        logTestFailure(failure);

        if (SpincastJUnitRunner.this.testClassInstance instanceof TestFailureListener) {
            ((TestFailureListener)SpincastJUnitRunner.this.testClassInstance).testFailure(failure);
        }
    }

    protected void logTestFailure(Failure failure) {
        System.err.println(getStackTrace(failure.getException()));
    }

    protected boolean isLastInstanceOfThisTestToRun() {
        return this.isLastLoop && this.isLastSingleTestLoop;
    }

    protected void runPreClassLoopsSpincastTests() {
        validateNoBeforeClassAnnotations();
        validateNoAfterClassAnnotations();
        validateNoTestsAndNoExpectingBeforeClassExceptionAnnotation();
        validateTestClassRepeateAnnotations();
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

    protected void validateTestClassRepeateAnnotations() {
        Integer repeatUntilFailureLoopsNbr = getTestClassRepeatUntilFailureAnnotationLoopsNbr();
        Integer repeatUntilSuccessLoopsNbr = getTestClassRepeatUntilSuccessAnnotationLoopsNbr();

        if (repeatUntilFailureLoopsNbr != null && repeatUntilSuccessLoopsNbr != null) {
            String msg = "Only one of the @" + RepeatUntilSuccess.class.getSimpleName() + " or " +
                         RepeatUntilFailure.class.getSimpleName() + " annotation can be used at the time.";
            spincastTestError(SPINCAST_TEST_NAME_REPEAT_ANNOTATIONS_VALIDATION, msg);
        }
    }

    protected void spincastTestError(String testName, String errorMessage) {
        spincastTestError(testName, new RuntimeException(errorMessage));
    }

    protected void spincastTestError(String testName, Throwable exception) {

        logger.error("Test error", exception);

        if (isExpectingFailure()) {
            logger.info("Error expected due to the " + ExpectingFailure.class.getSimpleName() + " annotation. Error ignored.");
            return;
        }

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

    public boolean isExpectingFailure() {
        if (this.isExpectingFailure == null) {
            this.isExpectingFailure = getTestClass().getAnnotation(ExpectingFailure.class) != null;
        }
        return this.isExpectingFailure;
    }

    /**
     * Will be <code>null</code> if there if no
     * {@link RepeatUntilFailure} annotation.
     */
    protected Integer getTestClassRepeatUntilFailureAnnotationLoopsNbr() {

        return geRepeatUntilFailureAnnotationLoopsNbr(getTestClass().getAnnotation(RepeatUntilFailure.class));
    }

    /**
     * Will be <code>null</code> if there if no
     * {@link RepeatUntilFailure} annotation.
     */
    protected Integer getMethodRepeatUntilFailureAnnotationLoopsNbr(Method method) {
        return geRepeatUntilFailureAnnotationLoopsNbr(method.getAnnotation(RepeatUntilFailure.class));
    }

    /**
     * Will be <code>null</code> if there if no
     * {@link RepeatUntilFailure} annotation.
     */
    protected Integer geRepeatUntilFailureAnnotationLoopsNbr(RepeatUntilFailure repeatUntilFailureAnnotation) {

        if (repeatUntilFailureAnnotation == null) {
            return null;
        }

        int loopsNbr = 1;
        if (repeatUntilFailureAnnotation != null) {
            loopsNbr = repeatUntilFailureAnnotation.value();
            if (loopsNbr < 1) {
                loopsNbr = 1;
            }
        }
        return loopsNbr;
    }

    /**
     * Will be <code>null</code> if there if no
     * {@link RepeatUntilFailure} annotation.
     */
    protected Integer getTestClassRepeatUntilFailureAnnotationLoopsSleep() {
        return getRepeatUntilFailureAnnotationLoopsSleep(getTestClass().getAnnotation(RepeatUntilFailure.class));
    }

    /**
     * Will be <code>null</code> if there if no
     * {@link RepeatUntilFailure} annotation.
     */
    protected Integer getMethodRepeatUntilFailureAnnotationLoopsSleep(Method method) {
        return getRepeatUntilFailureAnnotationLoopsSleep(method.getAnnotation(RepeatUntilFailure.class));
    }

    /**
     * Will be <code>null</code> if there if no
     * {@link RepeatUntilFailure} annotation.
     */
    protected Integer getRepeatUntilFailureAnnotationLoopsSleep(RepeatUntilFailure repeatAnnotation) {

        if (repeatAnnotation == null) {
            return null;
        }

        return repeatAnnotation.sleep();
    }

    /**
     * Will be <code>null</code> if there if no
     * {@link RepeatUntilSuccess} annotation.
     */
    protected Integer getTestClassRepeatUntilSuccessAnnotationLoopsNbr() {
        return geRepeatUntilSuccessAnnotationLoopsNbr(getTestClass().getAnnotation(RepeatUntilSuccess.class));
    }

    /**
     * Will be <code>null</code> if there if no
     * {@link RepeatUntilSuccess} annotation.
     */
    protected Integer getMethodRepeatUntilSuccessAnnotationLoopsNbr(Method method) {
        return geRepeatUntilSuccessAnnotationLoopsNbr(method.getAnnotation(RepeatUntilSuccess.class));
    }

    /**
     * Will be <code>null</code> if there if no
     * {@link RepeatUntilSuccess} annotation.
     */
    protected Integer geRepeatUntilSuccessAnnotationLoopsNbr(RepeatUntilSuccess repeatUntilSuccessAnnotation) {

        if (repeatUntilSuccessAnnotation == null) {
            return null;
        }

        int loopsNbr = 1;
        if (repeatUntilSuccessAnnotation != null) {
            loopsNbr = repeatUntilSuccessAnnotation.value();
            if (loopsNbr < 1) {
                loopsNbr = 1;
            }
        }
        return loopsNbr;
    }

    /**
     * Will be <code>null</code> if there if no
     * {@link RepeatUntilSuccess} annotation.
     */
    protected Integer getTestClassRepeatUntilSuccessAnnotationLoopsSleep() {
        return getRepeatUntilSuccessAnnotationLoopsSleep(getTestClass().getAnnotation(RepeatUntilSuccess.class));
    }

    /**
     * Will be <code>null</code> if there if no
     * {@link RepeatUntilSuccess} annotation.
     */
    protected Integer getMethodRepeatUntilSuccessAnnotationLoopsSleep(Method method) {
        return getRepeatUntilSuccessAnnotationLoopsSleep(method.getAnnotation(RepeatUntilSuccess.class));
    }

    /**
     * Will be <code>null</code> if there if no
     * {@link RepeatUntilSuccess} annotation.
     */
    protected Integer getRepeatUntilSuccessAnnotationLoopsSleep(RepeatUntilSuccess repeatUntilSuccessAnnotation) {

        if (repeatUntilSuccessAnnotation == null) {
            return null;
        }

        return repeatUntilSuccessAnnotation.sleep();
    }

}
