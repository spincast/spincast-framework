package org.spincast.plugins.formsprotection.doublesubmit;

import java.time.Instant;

public interface SpincastFormsDoubleSubmitProtectionRepository {

    /**
     * Saves the protectionId associated with a submitted form.
     */
    public void saveSubmittedFormProtectionId(Instant creationDate, String protectionId);

    /**
     * Is the specified protectionId one of an already submitted
     * form?
     */
    public boolean isFormAlreadySubmitted(String protectionId);

    /**
     * Deletes the saved protection ids that are older than the specified
     * number of minuets.
     */
    public void deleteOldFormsProtectionIds(int maxAgeMinutes);
}
