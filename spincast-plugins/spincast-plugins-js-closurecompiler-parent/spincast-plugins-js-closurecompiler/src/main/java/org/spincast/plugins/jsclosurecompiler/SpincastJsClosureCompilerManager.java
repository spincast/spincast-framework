package org.spincast.plugins.jsclosurecompiler;

import java.io.File;
import java.util.List;

public interface SpincastJsClosureCompilerManager {

    /**
     * Compile/Minify javascript content, as a String.
     * <p>
     * Read the <a href="https://github.com/google/closure-compiler/wiki/Flags-and-Options">options documentation</a>.
     * <p>
     * Note that you cannot use the "--js_output_file" option. The
     * method will take care of it by itself.
     *
     * @return the compiled/minified javascript
     * content.
     */
    public String compile(String jsContent, String... args);

    /**
     * Compile/Minify javascript content, as a String.
     * <p>
     * Read the <a href="https://github.com/google/closure-compiler/wiki/Flags-and-Options">options documentation</a>.
     * <p>
     * Note that you cannot use the "--js_output_file" option. The
     * method will take care of it by itself.
     *
     * @return the compiled/minified javascript
     * content.
     */
    public String compile(String jsContent, List<String> args);

    /**
     * Compile/Minify a js file.
     * <p>
     * Read the <a href="https://github.com/google/closure-compiler/wiki/Flags-and-Options">options documentation</a>.
     * <p>
     * Note that you cannot use the "--js_output_file" option. The
     * method will take care of it by itself.
     *
     * @return the compiled/minified javascript
     * content.
     */
    public String compile(File jsFile, String... args);

    /**
     * Compile/Minify a js file.
     * <p>
     * Read the <a href="https://github.com/google/closure-compiler/wiki/Flags-and-Options">options documentation</a>.
     * <p>
     * Note that you cannot use the "--js_output_file" option. The
     * method will take care of it by itself.
     *
     * @return the compiled/minified javascript
     * content.
     */
    public String compile(File jsFile, List<String> args);

    /**
     * Compile/minify multiple .js files
     * at a time and concatenate the result.
     * <p>
     * Read the <a href="https://github.com/google/closure-compiler/wiki/Flags-and-Options">options documentation</a>.
     * <p>
     * Note that you cannot use the "--js_output_file" option. The
     * method will take care of it by itself.
     *
     * @return the concatenated and compiled javascript
     * content.
     */
    public String compile(List<File> jsFiles, String... args);

    /**
     * Compile/minify multiple .js files
     * at a time and concatenate the result.
     * <p>
     * Read the <a href="https://github.com/google/closure-compiler/wiki/Flags-and-Options">options documentation</a>.
     * <p>
     * Note that you cannot use the "--js_output_file" option. The
     * method will take care of it by itself.
     *
     * @return the concatenated and compiled javascript
     * content.
     */
    public String compile(List<File> jsFiles, List<String> args);

    /**
     * Compile/Minify js/js files.
     *
     * Using this method, you are responsive to specify
     * all the arguments passed to the closure compile
     * .jar file (inlcuding the files to target).
     * This can be useful if you want to
     * compile files specified using
     * <a href="https://github.com/google/closure-compiler#compiling-multiple-scripts">globs patterns</a>
     * instead of having to specify them one by one.
     * <p>
     * In other words, the executed command will be:
     * <code>java -jar closure-compiler.jar [ARGS]</code>
     * and you have to specify all the "<code>[ARGS]</code>".
     * <p>
     * Read the <a href="https://github.com/google/closure-compiler/wiki/Flags-and-Options">options documentation</a>.
     * <p>
     * Note that you cannot use the "--js_output_file" option. The
     * method will take care of it by itself.
     *
     * @return the compiled/minified javascript
     * content.
     */
    public String compileCustom(String... args);

    /**
     * Compile/Minify js/js files.
     *
     * Using this method, you are responsive to specify
     * all the arguments passed to the closure compile
     * .jar file (inlcuding the files to target).
     * This can be useful if you want to
     * compile files specified using
     * <a href="https://github.com/google/closure-compiler#compiling-multiple-scripts">globs patterns</a>
     * instead of having to specify them one by one.
     * <p>
     * In other words, the executed command will be:
     * <code>java -jar closure-compiler.jar [ARGS]</code>
     * and you have to specify all the "<code>[ARGS]</code>".
     * <p>
     * Read the <a href="https://github.com/google/closure-compiler/wiki/Flags-and-Options">options documentation</a>.
     * <p>
     * Note that you cannot use the "--js_output_file" option. The
     * method will take care of it by itself.
     *
     * @return the compiled/minified javascript
     * content.
     */
    public String compileCustom(List<String> args);

}
