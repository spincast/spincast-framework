package org.spincast.plugins.formsprotection.doublesubmit;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.dictionary.Dictionary;
import org.spincast.core.exceptions.PublicException;
import org.spincast.core.exceptions.PublicExceptionDefault;
import org.spincast.core.exceptions.RedirectException;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.flash.FlashMessageFactory;
import org.spincast.core.routing.HttpMethod;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.plugins.crypto.SpincastCryptoUtils;
import org.spincast.plugins.formsprotection.config.SpincastFormsProtectionConfig;
import org.spincast.plugins.formsprotection.dictionary.SpincastFormsProtectionPluginDictionaryEntries;
import org.spincast.plugins.formsprotection.exceptions.FormAlreadySubmittedException;
import org.spincast.plugins.formsprotection.exceptions.FormTooOldException;
import org.spincast.shaded.org.apache.commons.lang3.tuple.Pair;
import org.spincast.shaded.org.apache.http.HttpStatus;

import com.google.inject.Inject;

public class SpincastFormsDoubleSubmitProtectionFilterDefault implements SpincastFormsDoubleSubmitProtectionFilter {

    protected final Logger logger = LoggerFactory.getLogger(SpincastFormsDoubleSubmitProtectionFilterDefault.class);

    private final SpincastFormsProtectionConfig spincastFormsProtectionConfig;
    private final FlashMessageFactory flashMessageFactory;
    private final Dictionary dictionary;
    private final SpincastCryptoUtils cryptoUtils;
    private final SpincastFormsDoubleSubmitProtectionRepository spincastFormsDoubleSubmitProtectionRepository;

    private final String formDoubleSubmitPrivateKey;

    @Inject
    public SpincastFormsDoubleSubmitProtectionFilterDefault(SpincastFormsProtectionConfig spincastFormsProtectionConfig,
                                                            FlashMessageFactory flashMessageFactory,
                                                            Dictionary dictionary,
                                                            SpincastCryptoUtils cryptoUtils,
                                                            SpincastFormsDoubleSubmitProtectionRepository spincastFormsDoubleSubmitProtectionRepository) {
        this.spincastFormsProtectionConfig = spincastFormsProtectionConfig;
        this.flashMessageFactory = flashMessageFactory;
        this.dictionary = dictionary;
        this.cryptoUtils = cryptoUtils;
        this.spincastFormsDoubleSubmitProtectionRepository = spincastFormsDoubleSubmitProtectionRepository;
        this.formDoubleSubmitPrivateKey = UUID.randomUUID().toString();
    }

    protected SpincastFormsProtectionConfig getSpincastFormsProtectionConfig() {
        return this.spincastFormsProtectionConfig;
    }

    protected FlashMessageFactory getFlashMessageFactory() {
        return this.flashMessageFactory;
    }

    protected Dictionary getDictionary() {
        return this.dictionary;
    }

    protected SpincastCryptoUtils getCryptoUtils() {
        return this.cryptoUtils;
    }

    protected SpincastFormsDoubleSubmitProtectionRepository getSpincastFormsDoubleSubmitProtectionRepository() {
        return this.spincastFormsDoubleSubmitProtectionRepository;
    }

    @Override
    public void handle(RequestContext<?> context) throws FormAlreadySubmittedException,
                                                  FormTooOldException {

        //==========================================
        // This filter should have been configured
        // to skip resources requests, but in case :
        //==========================================
        if (context.routing().getRoutingResult().getMainRouteHandlerMatch().getSourceRoute().isResourceRoute()) {
            return;
        }

        try {

            //==========================================
            // Safe HTTP methods
            //==========================================
            HttpMethod method = context.request().getHttpMethod();
            if (method == HttpMethod.GET || method == HttpMethod.HEAD || method == HttpMethod.OPTIONS ||
                method == HttpMethod.CONNECT) {
                return;
            }

            //==========================================
            // No form data submitted
            //==========================================
            Map<String, List<String>> formDatas = context.request().getFormBodyRaw();
            if (formDatas == null || formDatas.size() == 0) {
                return;
            }

            //==========================================
            // We may want to disable the double submit protection
            // on some forms...
            //==========================================
            String noDoubleSubmitProtection =
                    context.request().getFormBodyAsJsonObject()
                           .getString(getSpincastFormsProtectionConfig().getFormDoubleSubmitDisableProtectionIdFieldName());
            if (noDoubleSubmitProtection != null) {
                return;
            }

            //==========================================
            // Here, the submitted protection id must be valid!
            //==========================================
            String payload =
                    context.request().getFormBodyAsJsonObject()
                           .getString(getSpincastFormsProtectionConfig().getFormDoubleSubmitProtectionIdFieldName());
            if (payload == null) {
                this.logger.warn("Submitted form without a protection id: " + context.request().getFullUrl());
                throw new RedirectException("/");
            }

            Pair<Instant, String> info = getSubmittedFormInfo(payload);
            if (info == null) {
                this.logger.warn("Submitted form with an invalid form info payload'" + payload + "' : " +
                                 context.request().getFullUrl());
                invalidFormMatchAction(context,
                                       getDictionary().get(SpincastFormsProtectionPluginDictionaryEntries.MESSAGE_KEY_FORM_INVALID_PROTECTION_ID));
                return;
            }

            if (info.getKey()
                    .isBefore(Instant.now().minus(getSpincastFormsProtectionConfig().getFormDoubleSubmitFormValidForNbrMinutes(),
                                                  ChronoUnit.MINUTES))) {
                this.logger.warn("Form too old '" + info.getKey() + "' : " + context.request().getFullUrl());
                invalidFormMatchAction(context,
                                       getDictionary().get(SpincastFormsProtectionPluginDictionaryEntries.MESSAGE_KEY_FORM_TOO_OLD));
            }

            if (getSpincastFormsDoubleSubmitProtectionRepository().isFormAlreadySubmitted(info.getValue())) {
                this.logger.debug("Form submitted with an already used protection id : " + context.request().getFullUrl() +
                                  " => " + info.getValue());
                invalidFormMatchAction(context,
                                       getDictionary().get(SpincastFormsProtectionPluginDictionaryEntries.MESSAGE_KEY_FORM_ALREADY_SUBMITTED));
            }

            getSpincastFormsDoubleSubmitProtectionRepository().saveSubmittedFormProtectionId(info.getKey(), info.getValue());

        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public String createNewFormDoubleSubmitProtectionId() {
        String protectionId = Instant.now() + "|" + UUID.randomUUID().toString();
        protectionId = getCryptoUtils().encrypt(protectionId,
                                                getFormDoubleSubmitPrivateKey());
        return protectionId;
    }

    /**
     * Returns the Date/ProtectionId or <code>null</code>
     * if invalid.
     */
    protected Pair<Instant, String> getSubmittedFormInfo(String payloadEncrypted) {

        try {
            String payload =
                    getCryptoUtils().decrypt(payloadEncrypted,
                                             getFormDoubleSubmitPrivateKey());
            if (payload == null) {
                return null;
            }
            int pos = payload.indexOf("|");
            Instant date = Instant.parse(payload.substring(0, pos));
            String protectionId = payload.substring(pos + 1);

            return Pair.of(date, protectionId);

        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * What to do when the submitted form is not valid
     * (already submitted for example).
     * 
     * By default, throw a {@link PublicException} with
     * an HTTP status code of {@link HttpStatus#SC_BAD_REQUEST} and
     * a public message.
     */
    protected void invalidFormMatchAction(RequestContext<?> context, String message) throws Exception {
        throw new PublicExceptionDefault(message, HttpStatus.SC_BAD_REQUEST);
    }

    protected String getFormDoubleSubmitPrivateKey() {
        return this.formDoubleSubmitPrivateKey;
    }

}
