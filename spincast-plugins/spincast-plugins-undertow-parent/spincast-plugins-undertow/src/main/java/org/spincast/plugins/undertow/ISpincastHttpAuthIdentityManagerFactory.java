package org.spincast.plugins.undertow;

/**
 * Factory to create identity managers.
 */
public interface ISpincastHttpAuthIdentityManagerFactory {

    public ISpincastHttpAuthIdentityManager create();
}
