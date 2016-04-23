package org.spincast.core.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Some very few static methods.
 * 
 * You can still change the underlying instance though, in case
 * you need to change/fix something.
 */
public class SpincastStatics {

    protected final static Logger logger = LoggerFactory.getLogger(SpincastStatics.class);

    private static SpincastStatics instance;

    protected static SpincastStatics getInstance() {
        if(instance == null) {
            instance = new SpincastStatics();
        }
        return instance;
    }

    public static void setInstance(SpincastStatics instance) {
        SpincastStatics.instance = instance;
    }

    public static RuntimeException runtimize(Exception ex) {
        return getInstance().runtimizePrivate(ex);
    }

    protected RuntimeException runtimizePrivate(Exception ex) {

        Objects.requireNonNull(ex, "NULL exception");

        // Use the wrapped exception.
        if(ex instanceof InvocationTargetException) {
            Throwable wrappedException = ((InvocationTargetException)ex).getTargetException();
            if(wrappedException != null && wrappedException instanceof Exception) {
                ex = (Exception)wrappedException;
            }
        }

        if(ex instanceof InterruptedException) {
            RuntimeException exceptionToReturn = manageInterruptedException(ex);
            if(exceptionToReturn != null) {
                return exceptionToReturn;
            }
        }

        if(ex instanceof RuntimeException) {
            return (RuntimeException)ex;
        }

        return new RuntimeException(ex.getMessage(), ex);
    }

    /**
     * Return <code>null</code> to continue the exception processing or
     * an exception to be returned immediatly.
     * 
     * @see <a href="http://www.ibm.com/developerworks/java/library/j-jtp05236/index.html ">http://www.ibm.com/developerworks/java/library/j-jtp05236/index.html</a>
     */
    protected RuntimeException manageInterruptedException(Exception ex) {
        Thread.currentThread().interrupt();
        return new RuntimeException(ex.getMessage(), ex);
    }

    public static String getStackTrace(Throwable ex) {
        return getInstance().getStackTraceInstance(ex);
    }

    protected String getStackTraceInstance(Throwable ex) {

        if(ex == null) {
            return "";
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        return sw.toString();
    }

}
