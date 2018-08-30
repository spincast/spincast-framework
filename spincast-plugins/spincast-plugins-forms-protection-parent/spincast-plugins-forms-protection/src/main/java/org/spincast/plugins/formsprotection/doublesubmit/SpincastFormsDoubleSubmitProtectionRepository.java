package org.spincast.plugins.formsprotection.doublesubmit;

import java.time.Instant;

public interface SpincastFormsDoubleSubmitProtectionRepository {

    /**
     * Saves the protectedId associated with a submitted form.
     */
    public void saveSubmittedFormProtectionId(Instant date, String protectedId);

    /**
     * Is the specified protectedId one of an already submitted
     * form?
     */
    public boolean isFormAlreadySubmitted(String protectedId);

    /**
     * Deletes the saved protection ids that are older than the specified
     * number of minuets.
     */
    public void deleteOldFormsProtectionIds(int maxAgeMinutes);
}
