package org.spincast.plugins.undertow;

/**
 * Factory to create identity managers.
 */
public interface SpincastHttpAuthIdentityManagerFactory {

    public SpincastHttpAuthIdentityManager create();
}
