package org.spincast.core.routing;

/**
 * Factory for ETags
 */
public interface IETagFactory {

    /**
     * Creates a strong ETag, using a specific tag. THis tag will be used
     * as is to generate the ETag header value.
     * 
     * @param eTag the tag to use.
     */
    public IETag create(String tag);

    /**
     * Creates a strong or weak ETag, using a specific tag. THis tag will be used
     * as is to generate the ETag header value.
     * 
     * @param eTag the tag to use.
     * @param isWeak is the ETag weak?
     */
    public IETag create(String tag, boolean isWeak);

    /**
     * Creates a strong or weak ETag, using a specific tag. THis tag will be used
     * as is to generate the ETag header value.
     * 
     * @param eTag the tag to use.
     * @param isWeak is the ETag weak?
     * @param isWildcard is the ETag a wildcard? If so, the 'tag' must be
     * "*" or be empty.
     */
    public IETag create(String tag, boolean isWeak, boolean isWildcard);

    /**
     * Creates an ETag object from an already formatted ETag header.
     * 
     * @param etagHeader the value of the ETag header. Or an already formatted
     * ETag String.
     * 
     * @throws an exception is thrown if the value is invalid.
     */
    public IETag deserializeHeaderValue(String etagHeader);

}
