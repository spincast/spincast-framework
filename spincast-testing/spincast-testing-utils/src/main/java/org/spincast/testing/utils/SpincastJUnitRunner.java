package org.spincast.testing.utils;

import java.util.ArrayList;
import java.util.List;

import org.junit.internal.runners.statements.Fail;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A JUnit Runner that will create a single instance *per class*. By default, 
 * JUnit creates a new instance of the Test class for every @Test.
 * 
 * <p>
 * If the test class implements {@link org.spincast.testing.utils.IBeforeAfterClassMethodsProvider IBeforeAfterClassMethodsProvider}, 
 * "beforeClass()" and "afterClass()" will be called on it.
 * </p>
 * 
 * <p>
 * You can also annotate the test class with {@link org.spincast.testing.utils.ExpectingInstanciationException ExpectingInstanciationException}:
 * then the instantiation of the class will be expected to fail. If such an exception is
 * expected and does occure, the tests won't be run. Note that Junit requires at
 * least one test method to be present, so if <code>@ExpectingInstanciationException</code>
 * is used, we dynamically add the "hashcode()" method, always available on each object, as a
 * dummy "test" method.
 * </p>
 */
public class SpincastJUnitRunner extends BlockJUnit4ClassRunner {

    protected final Logger logger = LoggerFactory.getLogger(SpincastJUnitRunner.class);

    private Object spincastTestInstance = null;
    private Exception exceptionInBeforeClass = null;

    private Boolean isExpectingInstanciationException = null;

    public SpincastJUnitRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    public boolean isExpectingInstanciationException() {
        if(this.isExpectingInstanciationException == null) {
            this.isExpectingInstanciationException = getTestClass().getAnnotation(ExpectingInstanciationException.class) != null;
        }
        return this.isExpectingInstanciationException;
    }

    @Override
    public Object createTest() throws Exception {

        //==========================================
        // Throw the original exception for all tests.
        //==========================================
        if(this.exceptionInBeforeClass != null) {
            throw this.exceptionInBeforeClass;
        }

        if(this.spincastTestInstance == null) {
            this.spincastTestInstance = super.createTest();
            if(this.spincastTestInstance instanceof IBeforeAfterClassMethodsProvider) {

                try {
                    ((IBeforeAfterClassMethodsProvider)this.spincastTestInstance).beforeClass();
                } catch(Exception ex) {
                    this.exceptionInBeforeClass = ex;
                    if(!isExpectingInstanciationException()) {
                        throw ex;
                    }
                }
            }
        }
        return this.spincastTestInstance;
    }

    /**
     * If the class is annotated with @ExpectingInstanciationException
     * no @Test method are required. But Junit wants at least one 
     * method, so we add the always present "hashCode" as a test method.
     */
    @Override
    protected List<FrameworkMethod> computeTestMethods() {

        try {
            List<FrameworkMethod> testMethods = super.computeTestMethods();

            if(isExpectingInstanciationException() && testMethods.size() == 0) {

                testMethods = new ArrayList<FrameworkMethod>(testMethods);
                FrameworkMethod method = new FrameworkMethod(getTestClass().getJavaClass().getMethod("hashCode"));
                testMethods.add(method);
            }

            return testMethods;
        } catch(Exception ex) {
            throw(ex instanceof RuntimeException) ? (RuntimeException)ex : new RuntimeException(ex);
        }
    }

    /**
     * If an instanciation exception occured, we don't run the
     * actual tests!
     */
    @Override
    protected Statement methodBlock(FrameworkMethod method) {
        Statement statement = super.methodBlock(method);

        if(statement instanceof Fail) {

            try {
                ((Fail)statement).evaluate();
            } catch(Throwable ex) {
                this.exceptionInBeforeClass = new Exception(ex);
            }
        }

        if(isExpectingInstanciationException() && this.exceptionInBeforeClass != null) {
            statement = new Statement() {

                @Override
                public void evaluate() throws Throwable {
                    // ok
                }
            };
        }
        return statement;
    }

    @Override
    public void run(final RunNotifier notifier) {

        super.run(notifier);

        if(this.exceptionInBeforeClass == null &&
           this.spincastTestInstance != null &&
           this.spincastTestInstance instanceof IBeforeAfterClassMethodsProvider) {
            ((IBeforeAfterClassMethodsProvider)this.spincastTestInstance).afterClass();
        }
    }

}
