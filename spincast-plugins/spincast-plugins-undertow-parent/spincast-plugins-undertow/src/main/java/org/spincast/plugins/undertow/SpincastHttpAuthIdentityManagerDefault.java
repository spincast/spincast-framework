package org.spincast.plugins.undertow;

import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.shaded.org.apache.http.auth.BasicUserPrincipal;

import com.google.inject.assistedinject.AssistedInject;

import io.undertow.security.idm.Account;
import io.undertow.security.idm.Credential;
import io.undertow.security.idm.PasswordCredential;

/**
 * Implementation of the SpincastHttpAuthIdentityManager
 * based on a simple Map.
 */
public class SpincastHttpAuthIdentityManagerDefault implements SpincastHttpAuthIdentityManager {

    private final Map<String, PasswordCredential> credentials = new HashMap<String, PasswordCredential>();
    private final Map<PasswordCredential, Account> accounts = new HashMap<PasswordCredential, Account>();

    /**
     * Constructor.
     */
    @AssistedInject
    public SpincastHttpAuthIdentityManagerDefault() {
    }

    protected Map<String, PasswordCredential> getCredentials() {
        return this.credentials;
    }

    protected Map<PasswordCredential, Account> getAccounts() {
        return this.accounts;
    }

    @Override
    public void addUser(String username, String password) {

        if (StringUtils.isBlank(username)) {
            throw new RuntimeException("The username can't be empty");
        }
        if (StringUtils.isBlank(password)) {
            throw new RuntimeException("The password can't be empty");
        }

        PasswordCredential passwordCredential = new PasswordCredential(password.toCharArray());

        getCredentials().put(username, passwordCredential);

        getAccounts().put(passwordCredential, createAccount(username));

    }

    protected Account createAccount(final String username) {
        return new Account() {

            private static final long serialVersionUID = 1L;

            @Override
            public Set<String> getRoles() {
                //==========================================
                // Not used yet.
                //==========================================
                return Collections.emptySet();
            }

            @Override
            public Principal getPrincipal() {
                return new BasicUserPrincipal(username);
            }
        };
    }

    @Override
    public void removeUser(String username) {

        PasswordCredential credential = getCredentials().get(username);
        if (credential == null) {
            return;
        }

        getCredentials().remove(username);
        getAccounts().remove(credential);
    }

    @Override
    public void removeAllUsers() {
        getCredentials().clear();
        getAccounts().clear();
    }

    @Override
    public Account verify(Account account) {

        //==========================================
        // Account always valid for now.
        //==========================================
        return account;
    }

    @Override
    public Account verify(Credential credential) {

        if (!(credential instanceof PasswordCredential)) {
            return null;
        }

        Account account = getAccounts().get(credential);
        return account;
    }

    @Override
    public Account verify(String username, Credential credential) {

        if (!getCredentials().containsKey(username)) {
            return null;
        }

        if (!(credential instanceof PasswordCredential)) {
            return null;
        }

        PasswordCredential savedCredential = getCredentials().get(username);

        if (!Arrays.equals(savedCredential.getPassword(), ((PasswordCredential)credential).getPassword())) {
            return null;
        }

        return getAccounts().get(savedCredential);

    }

}
