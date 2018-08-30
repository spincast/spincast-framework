package org.spincast.plugins.formsprotection.config;

/**
 * Default configurations for Spincast Forms Protection plugin.
 */
public class SpincastFormsProtectionConfigDefault implements SpincastFormsProtectionConfig {

    @Override
    public String getFormCsrfProtectionIdFieldName() {
        return "spincast_csrf_id";
    }

    @Override
    public String getFormDoubleSubmitProtectionIdFieldName() {
        return "spincast_ds_id"; // "ds" => "double submit"
    }

    @Override
    public String getFormDoubleSubmitDisableProtectionIdFieldName() {
        return "spincast_ds_disabled"; // "ds" => "double submit"
    }

    @Override
    public boolean autoRegisterDeleteOldDoubleSubmitProtectionIdsCronJob() {
        return true;
    }

    @Override
    public int getDeleteOldDoubleSubmitProtectionIdsCronRunEveryNbrMinutes() {
        return 15;
    }

    @Override
    public int getFormDoubleSubmitFormValidForNbrMinutes() {
        return 120;
    }

}

