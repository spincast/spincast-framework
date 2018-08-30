package org.spincast.plugins.formsprotection;

import java.util.Set;

import org.spincast.core.dictionary.DictionaryEntries;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.plugins.crons.SpincastCronJob;
import org.spincast.plugins.formsprotection.config.SpincastFormsProtectionConfig;
import org.spincast.plugins.formsprotection.config.SpincastFormsProtectionConfigDefault;
import org.spincast.plugins.formsprotection.config.SpincastFormsProtectionPluginCronJobProvider;
import org.spincast.plugins.formsprotection.config.SpincastFormsProtectionPluginCronJobProviderDefault;
import org.spincast.plugins.formsprotection.csrf.SpincastFormsCsrfProtectionFilter;
import org.spincast.plugins.formsprotection.csrf.SpincastFormsCsrfProtectionFilterDefault;
import org.spincast.plugins.formsprotection.dictionary.SpincastFormsProtectionPluginDictionaryEntries;
import org.spincast.plugins.formsprotection.doublesubmit.SpincastFormsDoubleSubmitProtectionFilter;
import org.spincast.plugins.formsprotection.doublesubmit.SpincastFormsDoubleSubmitProtectionFilterDefault;
import org.spincast.plugins.formsprotection.doublesubmit.SpincastFormsDoubleSubmitProtectionRepository;
import org.spincast.plugins.formsprotection.pebble.SpincastFormsProtectionPebbleExtension;
import org.spincast.plugins.formsprotection.pebble.SpincastFormsProtectionPebbleExtensionDefault;

import com.google.inject.Key;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.multibindings.OptionalBinder;
import com.mitchellbosecke.pebble.extension.Extension;

/**
 * Spincast Forms Protection plugin module.
 */
public class SpincastFormsProtectionPluginModule extends SpincastGuiceModuleBase {

    @Override
    protected void configure() {

        bind(SpincastFormsProtectionConfig.class).to(getSpincastFormsProtectionConfigImplClass()).in(Scopes.SINGLETON);

        //==========================================
        // The SpincastFormsDoubleSubmitProtectionRepository
        // must be provided if the Double Submit Protection
        // is enabled, but is optional otherwise.
        //==========================================
        OptionalBinder.newOptionalBinder(binder(), SpincastFormsDoubleSubmitProtectionRepository.class);

        //==========================================
        // The Double Submit filter
        //==========================================
        bind(SpincastFormsDoubleSubmitProtectionFilter.class).to(getSpincastFormsDoubleSubmitProtectionFilterImplClass())
                                                             .in(Scopes.SINGLETON);

        //==========================================
        // The CSRF filter
        //==========================================
        bind(SpincastFormsCsrfProtectionFilter.class).to(getSpincastFormsCsrfProtectionFilterImplClass())
                                                     .in(Scopes.SINGLETON);

        //==========================================
        // Pebble is set as a *provided* dependency for this
        // plugin. If it is not available, we do not bind
        // the extension...
        //==========================================
        if (isPebbleAvailable()) {
            bindPebbleExtension();
        }

        //==========================================
        // Binds Dictionary entries
        //==========================================
        Multibinder<DictionaryEntries> dictionaryMultibinder = Multibinder.newSetBinder(binder(), DictionaryEntries.class);
        dictionaryMultibinder.addBinding().to(SpincastFormsProtectionPluginDictionaryEntries.class).asEagerSingleton();

        //==========================================
        // Binds cron jobs
        //==========================================
        bind(SpincastFormsProtectionPluginCronJobProvider.class).to(getSpincastFormsProtectionPluginCronJobProviderImplClass())
                                                                .in(Scopes.SINGLETON);
        Multibinder<Set<SpincastCronJob>> cronsSetsMultibinder =
                Multibinder.newSetBinder(binder(), Key.get(new TypeLiteral<Set<SpincastCronJob>>() {}));
        cronsSetsMultibinder.addBinding().toProvider(SpincastFormsProtectionPluginCronJobProvider.class).in(Scopes.SINGLETON);
    }

    protected Class<? extends SpincastFormsProtectionConfig> getSpincastFormsProtectionConfigImplClass() {
        return SpincastFormsProtectionConfigDefault.class;
    }

    protected boolean isPebbleAvailable() {
        try {
            Class.forName("com.mitchellbosecke.pebble.extension.Extension");
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    protected void bindPebbleExtension() {

        bind(SpincastFormsProtectionPebbleExtension.class).to(getPebbleExtensionImplClass()).in(Scopes.SINGLETON);

        Multibinder<Extension> pebbleExtensionsMultibinder = Multibinder.newSetBinder(binder(), Extension.class);
        pebbleExtensionsMultibinder.addBinding().to(SpincastFormsProtectionPebbleExtension.class)
                                   .in(Scopes.SINGLETON);
    }

    protected Class<? extends SpincastFormsProtectionPebbleExtension> getPebbleExtensionImplClass() {
        return SpincastFormsProtectionPebbleExtensionDefault.class;
    }

    protected Class<? extends SpincastFormsProtectionPluginCronJobProvider> getSpincastFormsProtectionPluginCronJobProviderImplClass() {
        return SpincastFormsProtectionPluginCronJobProviderDefault.class;
    }

    protected Class<? extends SpincastFormsCsrfProtectionFilter> getSpincastFormsCsrfProtectionFilterImplClass() {
        return SpincastFormsCsrfProtectionFilterDefault.class;
    }

    protected Class<? extends SpincastFormsDoubleSubmitProtectionFilter> getSpincastFormsDoubleSubmitProtectionFilterImplClass() {
        return SpincastFormsDoubleSubmitProtectionFilterDefault.class;
    }

}
