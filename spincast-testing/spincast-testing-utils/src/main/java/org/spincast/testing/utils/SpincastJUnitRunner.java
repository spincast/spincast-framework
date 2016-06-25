package org.spincast.testing.utils;

import static org.junit.Assert.fail;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Spincast JUnit Runner.
 */
public class SpincastJUnitRunner extends BlockJUnit4ClassRunner {

    protected final Logger logger = LoggerFactory.getLogger(SpincastJUnitRunner.class);

    private Object testClassInstance = null;
    private Boolean isExpectingBeforeClassException = null;
    private boolean exceptionInBeforeClass = false;
    private boolean testFailureListenerAdded = false;
    private Object testFailureListenerLock = new Object();

    private boolean atLeastOneTestFailed = false;
    private RunNotifier runNotifier = null;
    private int currentClassLoopPosition = 1;

    private List<SpincastTest> spincastTests;

    private List<FrameworkMethod> testMethods = null;
    private SpincastTestsSuiteFrameworkMethod spincastSuiteFrameworkMethod = null;

    private Set<String> spincastTestRan = new HashSet<String>();

    private boolean ignoreRemainingTests = false;

    protected final static String SPINCAST_TEST_NAME_EXPECTING_BEFORE_CLASS_EXCEPTION_VALIDATION =
            "@ExpectingBeforeClassException validation";

    protected final static String SPINCAST_TEST_NAME_BEFORE_CLASS_ANNOTATIONS_VALIDATION =
            "@BeforeClass annotations validation";

    protected final static String SPINCAST_TEST_NAME_AFTER_CLASS_ANNOTATIONS_VALIDATION =
            "@AfterClass annotations validation";

    protected final static String SPINCAST_TEST_NAME_AFTER_CLASS_EXCEPTION =
            "afterClass() validation";

    protected final static String SPINCAST_TEST_NAME_NO_TESTS_AND_NO_EXPECTION_EXCEPTION_ANNOTATION =
            "No tests and no @ExpectingBeforeClassException validation";

    protected final static String SPINCAST_TEST_NAME_AFTER_CLASS_LOOPS_EXCEPTION =
            "afterClassLoops() validation";

    public SpincastJUnitRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    protected RunNotifier getRunNotifier() {
        return this.runNotifier;
    }

    protected List<FrameworkMethod> getTestMethods() {
        return this.testMethods;
    }

    protected Set<String> getSpincastTestRan() {
        return this.spincastTestRan;
    }

    protected void setIgnoreRemainingTests() {
        this.ignoreRemainingTests = true;
    }

    protected boolean isIgnoreRemainingTests() {
        return this.ignoreRemainingTests || isExceptionInBeforeClass() || isExpectingBeforeClassException();
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
     * Compute the tests that are going to be rooted into
     * the Test class.
     */
    @Override
    protected final List<FrameworkMethod> computeTestMethods() {

        if(this.testMethods == null) {
            this.testMethods = new ArrayList<FrameworkMethod>();

            //==========================================
            // Spincast suite
            //==========================================
            List<SpincastTest> spincastTests = getSpincastTests();
            if((spincastTests != null && spincastTests.size() > 0)) {
                this.testMethods.add(getSpincastSuiteFrameworkMethod());
            }

            //==========================================
            // Add regular tests, annotated with @Test
            //==========================================
            List<FrameworkMethod> regularTests = super.computeTestMethods();
            this.testMethods.addAll(regularTests);

        }
        return this.testMethods;
    }

    /**
     * The FrameworkMethod for the Spincast Suite.
     */
    protected SpincastTestsSuiteFrameworkMethod getSpincastSuiteFrameworkMethod() {
        if(this.spincastSuiteFrameworkMethod == null) {
            this.spincastSuiteFrameworkMethod = new SpincastTestsSuiteFrameworkMethod(getTestClass());
        }
        return this.spincastSuiteFrameworkMethod;
    }

    protected SpincastTest getSpincastTest(String testName) {
        for(SpincastTest spincastTest : getSpincastTests()) {
            if(spincastTest.getName().equals(testName)) {
                return spincastTest;
            }
        }
        return null;
    }

    /**
     * The Spincast tests.
     */
    protected List<SpincastTest> getSpincastTests() {
        if(this.spincastTests == null) {
            this.spincastTests = createSpincastTests();
        }
        return this.spincastTests;
    }

    /**
     * The Spincast tests.
     */
    protected List<SpincastTest> createSpincastTests() {
        List<SpincastTest> spincastTests = new ArrayList<SpincastTest>();

        spincastTests.add(createExpectingBeforeClassExceptionValidation());
        spincastTests.add(createBeforeClassAnnotationsValidation());
        spincastTests.add(createAfterClassAnnotationsValidation());
        spincastTests.add(createAfterClassException());
        spincastTests.add(createNoTestsAndNotExpectingBeforeClassExceptionValidation());
        spincastTests.add(createAfterClassLoopsException());

        return spincastTests;
    }

    protected SpincastTest createExpectingBeforeClassExceptionValidation() {

        SpincastTest spincastTest =
                new SpincastTest(getTestClass(), SPINCAST_TEST_NAME_EXPECTING_BEFORE_CLASS_EXCEPTION_VALIDATION);

        return spincastTest;
    }

    protected SpincastTest createBeforeClassAnnotationsValidation() {

        // @formatter:off
        SpincastTest spincastTest =
                new SpincastTest(getTestClass(), SPINCAST_TEST_NAME_BEFORE_CLASS_ANNOTATIONS_VALIDATION, new Runnable() {
                    
                    @Override
                    public void run() {
                        List<FrameworkMethod> befores = getTestClass().getAnnotatedMethods(BeforeClass.class);
                        if(befores != null && befores.size() > 0) {
                            String msg = "The @BeforeClass JUnit annotation can't be used with the " +
                                    SpincastJUnitRunner.class.getSimpleName() + " " +
                                    "custom runner. Use the beforeClass() method instead by implementing the " +
                                    IBeforeAfterClassMethodsProvider.class.getSimpleName() + " interface.";
                            fail(msg);
                        }  
                    }
                });
        // @formatter:on

        return spincastTest;
    }

    protected SpincastTest createAfterClassAnnotationsValidation() {

        // @formatter:off
        SpincastTest spincastTest =
                new SpincastTest(getTestClass(), SPINCAST_TEST_NAME_AFTER_CLASS_ANNOTATIONS_VALIDATION, new Runnable() {
                    
                    @Override
                    public void run() {
                        List<FrameworkMethod> afters = getTestClass().getAnnotatedMethods(AfterClass.class);
                        if(afters != null && afters.size() > 0) {
                            String msg = "The @AfterClass JUnit annotation can't be used with the " +
                                    SpincastJUnitRunner.class.getSimpleName() + " " +
                                    "custom runner. Use the afterClass() method instead by implementing the " +
                                    IBeforeAfterClassMethodsProvider.class.getSimpleName() + " interface.";
                            fail(msg);
                        }  
                    }
                });
        // @formatter:on

        return spincastTest;
    }

    protected SpincastTest createAfterClassException() {

        SpincastTest spincastTest =
                new SpincastTest(getTestClass(), SPINCAST_TEST_NAME_AFTER_CLASS_EXCEPTION);

        return spincastTest;
    }

    protected SpincastTest createNoTestsAndNotExpectingBeforeClassExceptionValidation() {

        // @formatter:off
        SpincastTest spincastTest =
                new SpincastTest(getTestClass(), SPINCAST_TEST_NAME_NO_TESTS_AND_NO_EXPECTION_EXCEPTION_ANNOTATION, new Runnable() {
                    
                    @Override
                    public void run() {
                        
                        List<FrameworkMethod> regularTests = SpincastJUnitRunner.super.computeTestMethods();
                        if((regularTests == null || regularTests.size() == 0) && !isExpectingBeforeClassException()) {
                            fail("There must be at least one @Test or the @" + ExpectingBeforeClassException.class.getSimpleName() + " must " +
                                    "be present.");
                        }
                    }
                });
        // @formatter:on

        return spincastTest;
    }

    protected SpincastTest createAfterClassLoopsException() {
        SpincastTest spincastTest =
                new SpincastTest(getTestClass(), SPINCAST_TEST_NAME_AFTER_CLASS_LOOPS_EXCEPTION);

        return spincastTest;
    }

    /**
     * Custom Description object of a test.
     * 
     * If the test is annotated with @Repeat, we create children tests
     * for the number of loops to run for that test.
     */
    @Override
    protected Description describeChild(FrameworkMethod method) {

        //==========================================
        // Spincast test
        //==========================================
        if(method instanceof SpincastTestsSuiteFrameworkMethod) {

            //==========================================
            // Spincast Suite
            //==========================================
            if(method.getName().equals(getSpincastSuiteFrameworkMethod().getName())) {

                Description description = Description.createSuiteDescription(getSpincastSuiteFrameworkMethod().getName());

                //==========================================
                // We add the Spincast tests to the Spincast suite.
                //==========================================
                List<SpincastTest> spincastTests = getSpincastTests();
                if(spincastTests != null) {
                    for(SpincastTest spincastTest : spincastTests) {
                        description.addChild(spincastTest.getDescriptionObj());
                    }
                }
                return description;
            } else {
                //==========================================
                // Spincast test
                //==========================================
                return Description.createTestDescription(getTestClass().getName(), method.getName());
            }
        } else {
            //==========================================
            // Regular test
            //==========================================
            return super.describeChild(method);
        }
    }

    /**
     * JUnit calls that method to create an instance of the 
     * test class for each individual test. We change this
     * behavior and always return the *same* instance of the
     * test class: 'testClassInstance'.
     */
    @Override
    public Object createTest() throws Exception {

        if(this.testClassInstance == null) {
            this.testClassInstance = getTestClass().getOnlyConstructor().newInstance();
        }
        return this.testClassInstance;
    }

    protected Object getTestClassInstance() {
        try {
            return createTest();
        } catch(Exception ex) {
            throw(ex instanceof RuntimeException) ? (RuntimeException)ex : new RuntimeException(ex);
        }
    }

    protected int getTestClassLoopsNbr() {
        return getLoopsNbr(getTestClass().getAnnotation(Repeat.class));
    }

    protected int getMethodLoopsNbr(Method method) {
        return getLoopsNbr(method.getAnnotation(Repeat.class));
    }

    protected int getLoopsNbr(Repeat repeatAnnotation) {

        int loopsNbr = 1;
        if(repeatAnnotation != null) {
            loopsNbr = repeatAnnotation.value();
            if(loopsNbr < 1) {
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
        if(repeatAnnotation != null) {
            sleep = repeatAnnotation.sleep();
        }
        return sleep;
    }

    /**
     * Runs the test class.
     */
    @Override
    public void run(final RunNotifier notifier) {

        try {

            this.runNotifier = notifier;

            //==========================================
            // How many loops of the class should we make?
            //==========================================
            int loopsNbr = getTestClassLoopsNbr();
            int sleep = getTestClassLoopsSleep();

            try {

                //==========================================
                // Executes the loops
                //==========================================
                for(int i = 0; i < loopsNbr; i++) {

                    setCurrentClassLoopPosition(i + 1);

                    //==========================================
                    // We set the test class instance to null so 
                    // a new instance is created for each loop.
                    //==========================================
                    this.testClassInstance = null;

                    //==========================================
                    // Calls the "beforeClass()" method
                    //==========================================
                    if(getTestClassInstance() instanceof IBeforeAfterClassMethodsProvider) {

                        try {

                            ((IBeforeAfterClassMethodsProvider)getTestClassInstance()).beforeClass();

                            if(isExpectingBeforeClassException()) {
                                spincastTestSimulateRunError(SPINCAST_TEST_NAME_EXPECTING_BEFORE_CLASS_EXCEPTION_VALIDATION);
                            } else {
                                spincastTestSimulateRunSuccess(SPINCAST_TEST_NAME_EXPECTING_BEFORE_CLASS_EXCEPTION_VALIDATION);
                            }

                        } catch(Throwable ex) { // assertions are Errors, not Exceptions

                            setExceptionInBeforeClass();

                            if(!isExpectingBeforeClassException()) {
                                spincastTestSimulateRunError(SPINCAST_TEST_NAME_EXPECTING_BEFORE_CLASS_EXCEPTION_VALIDATION,
                                                             "An exception occured in the afterClass() method: " + ex.getMessage());
                            } else {
                                spincastTestSimulateRunSuccess(SPINCAST_TEST_NAME_EXPECTING_BEFORE_CLASS_EXCEPTION_VALIDATION);
                            }
                        }
                    } else {

                        if(isExpectingBeforeClassException()) {
                            spincastTestSimulateRunError(SPINCAST_TEST_NAME_EXPECTING_BEFORE_CLASS_EXCEPTION_VALIDATION);
                        } else {
                            spincastTestSimulateRunSuccess(SPINCAST_TEST_NAME_EXPECTING_BEFORE_CLASS_EXCEPTION_VALIDATION);
                        }
                    }

                    //==========================================
                    // Runs the tests of the class.
                    //==========================================
                    super.run(notifier);

                    //==========================================
                    // Calls the "afterClass()" method, if required
                    //==========================================
                    if(!isIgnoreRemainingTests() && getTestClassInstance() instanceof IBeforeAfterClassMethodsProvider) {

                        try {
                            ((IBeforeAfterClassMethodsProvider)this.testClassInstance).afterClass();
                            spincastTestSimulateRunSuccess(SPINCAST_TEST_NAME_AFTER_CLASS_EXCEPTION);
                        } catch(Throwable ex) { // assertions are Errors, not Exceptions
                            spincastTestSimulateRunError(SPINCAST_TEST_NAME_AFTER_CLASS_EXCEPTION,
                                                         "An exception occured in the afterClass() method: " + ex.getMessage());
                            break;
                        }
                    }

                    if(this.atLeastOneTestFailed || i >= (loopsNbr - 1)) {
                        break;
                    }

                    //==========================================
                    // Sleep
                    //==========================================
                    if(sleep > 0) {
                        try {
                            Thread.sleep(sleep);
                        } catch(Exception ex) {
                            //....
                        }
                    }

                    //==========================================
                    // Clear all tests
                    //==========================================
                    clearAllTests(notifier);
                }
            } finally {

                if(getTestClassInstance() instanceof IRepeatedClassAfterMethodProvider) {
                    if(!isExceptionInBeforeClass()) {
                        try {

                            ((IRepeatedClassAfterMethodProvider)this.testClassInstance).afterClassLoops();
                            spincastTestSimulateRunSuccess(SPINCAST_TEST_NAME_AFTER_CLASS_LOOPS_EXCEPTION);

                        } catch(Throwable ex) { // assertions are Errors, not Exceptions
                            spincastTestSimulateRunError(SPINCAST_TEST_NAME_AFTER_CLASS_LOOPS_EXCEPTION, ex);
                        }
                    } else {
                        this.logger.info("An exception occured in the 'beforeClass()' method, " +
                                         "so the 'afterClassLoops()' method won't be called.");
                    }
                }
            }

        } catch(Exception ex) {
            throw(ex instanceof RuntimeException) ? (RuntimeException)ex : new RuntimeException(ex);
        } finally {

            int classLoopsNbr = getTestClassLoopsNbr();
            if(this.atLeastOneTestFailed && classLoopsNbr > 1) {
                this.logger.error("The test failure occured during the class loop #" + getCurrentClassLoopPosition());
            }

            for(SpincastTest spincastTest : getSpincastTests()) {
                if(!getSpincastTestRan().contains(spincastTest.getName())) {
                    spincastTestIgnore(spincastTest.getDescriptionObj());
                }
            }

        }
    }

    protected void clearAllTests(RunNotifier notifier) {
        for(FrameworkMethod frameworkMethod : getTestMethods()) {
            Description description = describeChild(frameworkMethod);
            notifier.fireTestStarted(description);

            if(description.getChildren() != null) {
                for(Description child : description.getChildren()) {
                    notifier.fireTestStarted(child);
                }
            }
        }
        getSpincastTestRan().clear();
    }

    protected void clearTest(RunNotifier notifier, Description description) {

        notifier.fireTestStarted(description);

        if(description.getChildren() != null) {
            for(Description child : description.getChildren()) {
                notifier.fireTestStarted(child);
            }
        }
    }

    protected void clearSpincastTest(RunNotifier notifier, Description description) {

        notifier.fireTestStarted(description);

        if(description.getChildren() != null) {
            for(Description child : description.getChildren()) {
                notifier.fireTestStarted(child);
                getSpincastTestRan().remove(child.getMethodName());
            }
        }

        getSpincastTestRan().remove(description.getMethodName());
    }

    protected void spincastTestSimulateRunSuccess(String testName) {

        SpincastTest spincastTest = getSpincastTest(testName);
        Description description = spincastTest.getDescriptionObj();

        getRunNotifier().fireTestStarted(description);
        spincastTestSuccess(description);
    }

    protected void spincastTestSuccess(Description description) {
        getSpincastTestRan().add(description.getMethodName());
        getRunNotifier().fireTestFinished(description);
    }

    protected void spincastTestSimulateRunError(String testName) {
        spincastTestSimulateRunError(testName, testName);
    }

    protected void spincastTestSimulateRunError(String testName, String errorMessage) {
        spincastTestSimulateRunError(testName, new RuntimeException(errorMessage));
    }

    protected void spincastTestSimulateRunError(String testName, Throwable error) {

        SpincastTest spincastTest = getSpincastTest(testName);
        Description description = spincastTest.getDescriptionObj();

        getRunNotifier().fireTestStarted(description);
        spincastTestError(description, error);
    }

    protected void spincastTestError(Description description) {
        spincastTestError(description, new RuntimeException(description.getMethodName()));
    }

    protected void spincastTestError(Description description, String errorMessage) {
        spincastTestError(description, new RuntimeException(errorMessage));
    }

    protected void spincastTestError(Description description, Throwable exception) {

        getSpincastTestRan().add(description.getMethodName());
        setIgnoreRemainingTests();

        getRunNotifier().fireTestFailure(new Failure(description, exception));
        getRunNotifier().fireTestFinished(description);
    }

    protected void spincastTestIgnore(Description description) {
        getSpincastTestRan().add(description.getMethodName());
        getRunNotifier().fireTestIgnored(description);
    }

    /**
     * Runs a specific test
     */
    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {

        //==========================================
        // We add a testFailure listener in case the 
        // Test class implements ITestFailureListener
        // to be informed of any failing test.
        //==========================================
        if(!this.testFailureListenerAdded) {
            synchronized(this.testFailureListenerLock) {
                if(!this.testFailureListenerAdded) {
                    this.testFailureListenerAdded = true;

                    notifier.addListener(new RunListener() {

                        @Override
                        public void testFailure(Failure failure) throws Exception {
                            SpincastJUnitRunner.this.atLeastOneTestFailed = true;
                            if(SpincastJUnitRunner.this.testClassInstance instanceof ITestFailureListener) {
                                ((ITestFailureListener)SpincastJUnitRunner.this.testClassInstance).testFailure(failure);
                            }
                        }
                    });
                }
            }
        }

        //==========================================
        // Spincast Suite
        // We run the runnable Spincast tests.
        //==========================================
        if(method.getName().equals(getSpincastSuiteFrameworkMethod().getName())) {

            for(SpincastTest spincastTest : getSpincastTests()) {

                if(spincastTest.isRunnable()) {
                    getSpincastTestRan().add(spincastTest.getName());

                    if(isIgnoreRemainingTests()) {
                        spincastTestIgnore(spincastTest.getDescriptionObj());
                    } else {

                        notifier.fireTestStarted(spincastTest.getDescriptionObj());
                        try {
                            spincastTest.run();
                            spincastTestSuccess(spincastTest.getDescriptionObj());
                        } catch(Throwable ex) {
                            spincastTestError(spincastTest.getDescriptionObj(), ex);
                        }
                    }
                }
            }

        } else {

            Description description = describeChild(method);

            if(isIgnoreRemainingTests()) {
                notifier.fireTestIgnored(description);
                return;
            }

            //==========================================
            // How many times should we run the test?
            //==========================================
            int loopsNbr = getMethodLoopsNbr(method.getMethod());
            for(int i = 0; i < loopsNbr; i++) {

                super.runChild(method, notifier);

                if(loopsNbr > 1 && this.atLeastOneTestFailed) {
                    this.logger.error("The test \"" + method.getMethod().getName() + "\" failed during the loop #" + (i + 1));
                    break;
                }

                if(i >= (loopsNbr - 1)) {
                    break;
                }

                int sleep = getMethodLoopsSleep(method.getMethod());
                if(sleep > 0) {
                    try {
                        Thread.sleep(sleep);
                    } catch(Exception ex) {
                        //....
                    }
                }

                clearTest(notifier, description);
            }
        }
    }

    /**
     * Are we expecting "beforeClass()" method to fail?
     * If so, it is an error if no exception is thrown!
     */
    public boolean isExpectingBeforeClassException() {
        if(this.isExpectingBeforeClassException == null) {
            this.isExpectingBeforeClassException = getTestClass().getAnnotation(ExpectingBeforeClassException.class) != null;
        }
        return this.isExpectingBeforeClassException;
    }

    /**
     * No @BeforeClass JUnit annotation is allowed.
     */
    @Override
    protected Statement withBeforeClasses(Statement statement) {
        return statement;
    }

    /**
     * No @AfterClass JUnit annotation is allowed.
     */
    @Override
    protected Statement withAfterClasses(Statement statement) {
        return statement;
    }

    /**
     * Test class display name.
     */
    @Override
    protected String getName() {

        String name = super.getName();

        int loopsNbr = getTestClassLoopsNbr();
        if(loopsNbr > 1) {
            name = name + " [" + loopsNbr + " loops]";
        }

        return name;
    }

}
