package org.spincast.plugins.formsprotection.pebble;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.plugins.formsprotection.config.SpincastFormsProtectionConfig;
import org.spincast.plugins.formsprotection.csrf.SpincastCsrfToken;
import org.spincast.plugins.formsprotection.csrf.SpincastFormsCsrfProtectionFilter;
import org.spincast.plugins.formsprotection.doublesubmit.SpincastFormsDoubleSubmitProtectionFilter;
import org.spincast.plugins.formsprotection.doublesubmit.SpincastFormsDoubleSubmitProtectionRepository;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.Function;


public class SpincastFormsProtectionPebbleExtensionDefault extends AbstractExtension
                                                           implements
                                                           SpincastFormsProtectionPebbleExtension {

    protected final static Logger logger = LoggerFactory.getLogger(SpincastFormsProtectionPebbleExtensionDefault.class);

    public static final String FONCTION_NAME_FORM_DOUBLE_SUBMIT_PROTECTION_FIELD_NAME = "formDoubleSubmitProtectionFieldName";
    public static final String FONCTION_NAME_FORM_DOUBLE_SUBMIT_PROTECTION_FIELD_VALUE = "formDoubleSubmitProtectionFieldValue";
    public static final String FONCTION_NAME_FORM_DOUBLE_SUBMIT_DISABLE_PROTECTION_FIELD_NAME =
            "formDoubleSubmitDisableProtectionFieldName";
    public static final String FONCTION_NAME_FORM_CSRF_PROTECTTION_FIELD_NAME = "formCsrfProtectionFieldName";
    public static final String FONCTION_NAME_FORM_CSRF_PROTECTTION_FIELD_VALUE = "formCsrfProtectionFieldValue";

    private final Optional<SpincastFormsDoubleSubmitProtectionRepository> SpincastFormsDoubleSubmitProtectionRepositoryOptional;
    private final SpincastConfig spincastConfig;
    private final SpincastUtils spincastUtils;
    private final SpincastFormsProtectionConfig spincastFormsProtectionConfig;
    private final SpincastFormsDoubleSubmitProtectionFilter spincastFormsDoubleSubmitProtectionFilter;
    private final SpincastFormsCsrfProtectionFilter spincastFormsCsrfProtectionFilter;

    @Inject
    public SpincastFormsProtectionPebbleExtensionDefault(Optional<SpincastFormsDoubleSubmitProtectionRepository> SpincastFormsDoubleSubmitProtectionRepositoryOptional,
                                                         SpincastConfig spincastConfig,
                                                         SpincastUtils spincastUtils,
                                                         SpincastFormsProtectionConfig spincastFormsProtectionConfig,
                                                         SpincastFormsDoubleSubmitProtectionFilter spincastFormsDoubleSubmitProtectionFilter,
                                                         SpincastFormsCsrfProtectionFilter spincastFormsCsrfProtectionFilter) {
        this.SpincastFormsDoubleSubmitProtectionRepositoryOptional = SpincastFormsDoubleSubmitProtectionRepositoryOptional;
        this.spincastConfig = spincastConfig;
        this.spincastUtils = spincastUtils;
        this.spincastFormsProtectionConfig = spincastFormsProtectionConfig;
        this.spincastFormsDoubleSubmitProtectionFilter = spincastFormsDoubleSubmitProtectionFilter;
        this.spincastFormsCsrfProtectionFilter = spincastFormsCsrfProtectionFilter;
    }

    protected Optional<SpincastFormsDoubleSubmitProtectionRepository> getSpincastFormsDoubleSubmitProtectionRepositoryOptional() {
        return this.SpincastFormsDoubleSubmitProtectionRepositoryOptional;
    }

    protected SpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    protected SpincastFormsProtectionConfig getSpincastFormsProtectionConfig() {
        return this.spincastFormsProtectionConfig;
    }

    protected SpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

    protected SpincastFormsDoubleSubmitProtectionFilter getSpincastFormsDoubleSubmitProtectionFilter() {
        return this.spincastFormsDoubleSubmitProtectionFilter;
    }

    protected SpincastFormsCsrfProtectionFilter getSpincastFormsCsrfProtectionFilter() {
        return this.spincastFormsCsrfProtectionFilter;
    }

    @Override
    public Map<String, Function> getFunctions() {

        Map<String, Function> functions = new HashMap<>();

        //==========================================
        // Double Submit protection functions only
        // enabled if an implementation for 
        // SpincastFormsDoubleSubmitProtectionFilter
        // was bound.
        //==========================================
        if (getSpincastFormsDoubleSubmitProtectionRepositoryOptional().orNull() != null) {
            functions.put(FONCTION_NAME_FORM_DOUBLE_SUBMIT_PROTECTION_FIELD_VALUE, getFormDoubleSubmitProtectionFieldValueFunction());
            functions.put(FONCTION_NAME_FORM_DOUBLE_SUBMIT_PROTECTION_FIELD_NAME,
                          getFormDoubleSubmitProtectionFieldNameFunction());
            functions.put(FONCTION_NAME_FORM_DOUBLE_SUBMIT_DISABLE_PROTECTION_FIELD_NAME,
                          getFormDoubleSubmitDisableProtectionFieldNameFunction());
        } else {
            logger.warn("No implementation bound for " + SpincastFormsDoubleSubmitProtectionFilter.class.getName() + " ... " +
                        "The Pebble functions related to Double Submit protection won't be bound...");
        }

        functions.put(FONCTION_NAME_FORM_CSRF_PROTECTTION_FIELD_NAME, getFormCsrfProtectionIdFieldNameFunction());
        functions.put(FONCTION_NAME_FORM_CSRF_PROTECTTION_FIELD_VALUE, getFormCsrfProtectionFieldValueFunction());

        return functions;
    }

    protected Function getFormDoubleSubmitProtectionFieldValueFunction() {

        return new Function() {

            @Override
            public List<String> getArgumentNames() {
                return null;
            }

            @Override
            public Object execute(Map<String, Object> args) {
                return getSpincastFormsDoubleSubmitProtectionFilter().createNewFormDoubleSubmitProtectionId();
            }
        };
    }

    protected Function getFormDoubleSubmitProtectionFieldNameFunction() {

        return new Function() {

            @Override
            public List<String> getArgumentNames() {
                return null;
            }

            @Override
            public Object execute(Map<String, Object> args) {
                return getSpincastFormsProtectionConfig().getFormDoubleSubmitProtectionIdFieldName();
            }
        };
    }

    protected Function getFormDoubleSubmitDisableProtectionFieldNameFunction() {

        return new Function() {

            @Override
            public List<String> getArgumentNames() {
                return null;
            }

            @Override
            public Object execute(Map<String, Object> args) {
                return getSpincastFormsProtectionConfig().getFormDoubleSubmitDisableProtectionIdFieldName();
            }
        };
    }

    protected Function getFormCsrfProtectionIdFieldNameFunction() {

        return new Function() {

            @Override
            public List<String> getArgumentNames() {
                return null;
            }

            @Override
            public Object execute(Map<String, Object> args) {
                return getSpincastFormsProtectionConfig().getFormCsrfProtectionIdFieldName();
            }
        };
    }

    protected Function getFormCsrfProtectionFieldValueFunction() {

        return new Function() {

            @Override
            public List<String> getArgumentNames() {
                return null;
            }

            @Override
            public Object execute(Map<String, Object> args) {

                String csrf = "";
                SpincastCsrfToken currentCsrfToken = getSpincastFormsCsrfProtectionFilter().getCurrentCsrfToken();
                if (currentCsrfToken != null) {
                    csrf = currentCsrfToken.getId();
                }
                return csrf;
            }
        };
    }

}
