package org.spincast.core.routing;

/**
 * Represents an HTTP ETag.
 */
public interface IETag {

    /**
     * Gets the tag text.
     */
    public String getTag();

    /**
     * Is it weak?
     */
    public boolean isWeak();

    /**
     * Is it a wildcard ETag?
     */
    public boolean isWildcard();

    /**
     * Gets the HTTP header value to use for this ETag.
     */
    public String getHeaderValue();

    /**
     * Does the ETag strongly matches the other specified
     * ETag?
     * <p>
     * If one (or both) ETag is a wildcard, it matches.
     * </p>
     * <p>
     * <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html#sec13.3.3">more info</a>
     * </p>
     */
    public boolean matches(IETag other);

    /**
     * Does the ETag strongly or weakly matches the other specified
     * ETag?
     * <p>
     * If one (or both) ETag is a wildcard, it matches.
     * </p>
     * <p>
     * <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html#sec13.3.3">more info</a>
     * </p>
     */
    public boolean matches(IETag other, boolean weakComparison);

}
