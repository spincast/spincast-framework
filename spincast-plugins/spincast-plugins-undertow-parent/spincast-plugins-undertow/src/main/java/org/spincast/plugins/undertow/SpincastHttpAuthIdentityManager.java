package org.spincast.plugins.undertow;

import io.undertow.security.idm.IdentityManager;

/**
 * Custom identity manager for Undertow's HTTP auth protection.
 */
public interface SpincastHttpAuthIdentityManager extends IdentityManager {

    /**
     * Adds a user to this identity manager.
     */
    public void addUser(String username, String password);

    /**
     * Removes a user from this identity manager.
     */
    public void removeUser(String username);

    /**
     * Removes all users from this identity manager.
     */
    public void removeAllUsers();

}
