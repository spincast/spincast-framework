package org.spincast.plugins.formsprotection.config;

import org.spincast.plugins.formsprotection.doublesubmit.SpincastFormsDoubleSubmitProtectionFilter;
import org.spincast.plugins.formsprotection.doublesubmit.SpincastFormsDoubleSubmitProtectionRepository;

/**
 * Configurations for the Spincast Forms Protection plugin.
 */
public interface SpincastFormsProtectionConfig {

    /**
     * The name of the session variable used to store the
     * CSRF token id of the user.
     */
    public static final String SESSION_VARIABLE_NAME_CSRF_TOKEN =
            SpincastFormsProtectionConfig.class.getName() + "_csrfToken";

    /**
     * The "name" of the hidden field in which a generated
     * CSRF protection id will be saved in the HTML form.
     */
    public String getFormCsrfProtectionIdFieldName();

    /**
     * The "name" of the hidden field in which a generated
     * Double Submit protection id will be saved in the HTML form.
     */
    public String getFormDoubleSubmitProtectionIdFieldName();

    /**
     * The "name" of the hidden field to add to disable the Double Submit
     * protection on a specific form. (use any value such as "1" for
     * the field.)
     */
    public String getFormDoubleSubmitDisableProtectionIdFieldName();

    /**
     * Should this plugin automatically register a scheduled task
     * that will call {@link SpincastFormsDoubleSubmitProtectionRepository#deleteOldFormsProtectionIds(int)}
     * for cleanup?
     * <p>
     * Note that wathever the value, the scheduled task will only be registered if a proper
     * implementation of {@link SpincastFormsDoubleSubmitProtectionFilter} was
     * bound in the first place.
     * <p>
     * If disabled, you are responsible to register the scheduled task by
     * yourself, or at least delete those old ids
     * by yourself.
     * <p>
     * Defaults to <code>true</code>.
     */
    public boolean autoRegisterDeleteOldDoubleSubmitProtectionIdsScheduledTask();

    /**
     * If {@link #autoRegisterDeleteOldDoubleSubmitProtectionIdsScheduledTask()} is
     * enabled, the cleanup of old saved protection ids should runs 
     * every X minutes.
     */
    public int getDeleteOldDoubleSubmitProtectionIdsScheduledTaskRunEveryNbrMinutes();

    /**
     * If the {@link SpincastFormsDoubleSubmitProtectionFilter} filter is used,
     * this is the number of minutes maximum of form will be considered as
     * valid. Older than that, it will be refused.
     * <p>
     * If {@link #autoRegisterDeleteOldDoubleSubmitProtectionIdsScheduledTask()} is
     * enabled, the scheduled task will also use this value to decide when to delete old
     * protection ids.
     * <p>
     * Defaults to 120 minutes (2 hours).
     */
    public int getFormDoubleSubmitFormValidForNbrMinutes();


}
