package org.spincast.core.cookies;

/**
 * Possible values for a ccokie's
 * "SameSite" attribute.
 */
public enum CookieSameSite {

    STRICT("Strict"),
    LAX("Lax");

    private final String value;

    private CookieSameSite(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
