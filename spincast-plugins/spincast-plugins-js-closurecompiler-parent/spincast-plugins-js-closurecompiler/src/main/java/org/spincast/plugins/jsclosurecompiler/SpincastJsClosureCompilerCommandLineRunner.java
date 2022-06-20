package org.spincast.plugins.jsclosurecompiler;

import com.google.javascript.jscomp.CommandLineRunner;
import com.google.javascript.jscomp.jarjar.com.google.common.base.Function;

/**
 * Extending {@link CommandLineRunner} allows us to
 * declare a public constructor and set an
 * "Exit Code Receiver" that never call
 * <code>System.exit()</code>.
 */
public class SpincastJsClosureCompilerCommandLineRunner extends CommandLineRunner {

    public SpincastJsClosureCompilerCommandLineRunner(String[] args) {
        super(args);
        setExitCodeReceiver(new Function<Integer, Void>() {

            @Override
            public Void apply(Integer exitValue) {
                if (exitValue == null || exitValue != 0) {
                    throw new RuntimeException("Errors running the Closure Compiler. Exit value: " + exitValue);
                }
                return null;
            }
        });
    }
}
