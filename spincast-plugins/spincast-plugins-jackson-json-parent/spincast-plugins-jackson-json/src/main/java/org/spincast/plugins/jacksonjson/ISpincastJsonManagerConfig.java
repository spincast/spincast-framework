package org.spincast.plugins.jacksonjson;

import com.google.inject.ImplementedBy;

/**
 * Configurations for the Spincast Json Manager plugin.
 * 
 * We use "@ImplementedBy" to specify the default configurations
 * to use if none is specified in a Guice module.
 */
@ImplementedBy(SpincastJsonManagerConfigDefault.class)
public interface ISpincastJsonManagerConfig {

    /**
     * The number of space to use as indentation for pretty print.
     * Defaults to 4.
     */
    public int getPrettyPrinterIndentationSpaceNumber();

    /**
     * The newline characters to use for pretty print.
     * Defaults to "\n".
     */
    public String getPrettyPrinterNewlineChars();

    /**
     * Gets the maximum number of <code>keys</code> that
     * can be parsed as <code>FieldPaths</code> using the 
     * {@link org.spincast.core.json.IJsonManager#create(java.util.Map, boolean) create}
     * method.
     * <p>
     * This maximum is to prevent malicious user to POST
     * a very big number of <code>keys</code> and kill
     * the CPU/memory.
     * </p>
     */
    public int getMaxNumberOfFieldPathKeys();

    /**
     * Gets the maximum length of a <code>key</code> that
     * can be parsed as <code>FieldPaths</code> using the 
     * {@link org.spincast.core.json.IJsonManager#create(java.util.Map, boolean) create}
     * method.
     * <p>
     * This maximum is to prevent malicious user to POST
     * very long and complex <code>keys</code> and kill
     * the CPU/memory.
     * </p>
     */
    public int getFieldPathKeyMaxLength();

}
