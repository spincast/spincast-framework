package org.spincast.testing.utils;

import java.util.UUID;

import org.junit.runner.Description;
import org.junit.runners.model.TestClass;

public class SpincastTest {

    private final String name;
    private final Description description;
    private final Runnable runnable;

    public SpincastTest(TestClass testClass, String name) {
        this(testClass, name, null);
    }

    public SpincastTest(TestClass testClass, String name, Runnable runnable) {

        //==========================================
        // The "Test Class" must be the right one or a click
        // on the Spincast test won't lead to the class
        // in an IDE.
        //==========================================
        Description description = Description.createTestDescription(testClass.getName(),
                                                                    name,
                                                                    UUID.randomUUID().toString());
        this.name = name;
        this.description = description;
        this.runnable = runnable;
    }

    public String getName() {
        return this.name;
    }

    public Description getDescriptionObj() {
        return this.description;
    }

    protected Runnable getRunnable() {
        return this.runnable;
    }

    public boolean isRunnable() {
        return getRunnable() != null;
    }

    public void run() throws Throwable {
        if(isRunnable()) {
            getRunnable().run();
        }
    }

    @Override
    public String toString() {
        return getName();
    }

}
