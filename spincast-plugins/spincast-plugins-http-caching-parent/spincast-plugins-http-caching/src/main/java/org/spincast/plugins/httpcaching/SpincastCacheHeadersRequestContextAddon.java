package org.spincast.plugins.httpcaching;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.exchange.ICacheHeadersRequestContextAddon;
import org.spincast.core.exchange.IRequestContext;
import org.spincast.core.routing.HttpMethod;
import org.spincast.core.routing.IETag;
import org.spincast.core.routing.IETagFactory;
import org.spincast.shaded.org.apache.http.HttpHeaders;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.shaded.org.apache.http.client.utils.DateUtils;

import com.google.inject.Inject;

public class SpincastCacheHeadersRequestContextAddon<R extends IRequestContext<?>>
                                                    implements ICacheHeadersRequestContextAddon<R> {

    protected final Logger logger = LoggerFactory.getLogger(SpincastCacheHeadersRequestContextAddon.class);

    private final R requestContext;
    private final IETagFactory etagFactory;

    private IETag eTag = null;
    private boolean eTagWeakComparison = false;
    private Date lastModificationDate;

    @Inject
    public SpincastCacheHeadersRequestContextAddon(R requestContext,
                                                   IETagFactory etagFactory) {
        this.requestContext = requestContext;
        this.etagFactory = etagFactory;
    }

    protected R getRequestContext() {
        return this.requestContext;
    }

    protected IETagFactory getEtagFactory() {
        return this.etagFactory;
    }

    protected Object getExchange() {
        return getRequestContext().exchange();
    }

    protected IETag getETag() {
        return this.eTag;
    }

    protected void setETag(IETag eTag) {
        this.eTag = eTag;
    }

    protected boolean isETagWeakComparison() {
        return this.eTagWeakComparison;
    }

    protected void setETagWeakComparison(boolean eTagWeakComparison) {
        this.eTagWeakComparison = eTagWeakComparison;
    }

    protected Date getLastModificationDate() {
        return this.lastModificationDate;
    }

    protected void setLastModificationDate(Date lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    @Override
    public ICacheHeadersRequestContextAddon<R> eTag(String currentTag) {
        return eTag(currentTag, false, false);
    }

    @Override
    public ICacheHeadersRequestContextAddon<R> eTag(String currentTag, boolean currentTagIsWeak) {
        return eTag(currentTag, currentTagIsWeak, false);
    }

    @Override
    public ICacheHeadersRequestContextAddon<R> eTag(String currentTag, boolean currentTagIsWeak, boolean weakComparison) {

        if(currentTag == null) {
            setETag(null);
            getRequestContext().response().removeHeader(HttpHeaders.ETAG);
        } else {
            // Wildcard would make no sense here.
            IETag eTag = getEtagFactory().create(currentTag, currentTagIsWeak, false);
            setETag(eTag);

            getRequestContext().response().setHeader(HttpHeaders.ETAG, eTag.getHeaderValue());
        }

        HttpMethod httpMethod = getRequestContext().request().getHttpMethod();
        if(weakComparison && (httpMethod != HttpMethod.GET && httpMethod != HttpMethod.HEAD)) {
            weakComparison = false;
            this.logger.warn("ETag weak comparison is only allowed for GET and HEAD methods. Current " +
                             "HTTP method is " + httpMethod + ". " +
                             "See https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.26");
        }

        setETagWeakComparison(weakComparison);

        return this;
    }

    @Override
    public ICacheHeadersRequestContextAddon<R> lastModified(Date lastModificationDate) {

        setLastModificationDate(lastModificationDate);

        if(lastModificationDate == null) {
            getRequestContext().response().removeHeader(HttpHeaders.LAST_MODIFIED);
        } else {
            getRequestContext().response().setHeader(HttpHeaders.LAST_MODIFIED,
                                                     formatDateForHttpHeader(lastModificationDate));
        }

        return this;
    }

    protected boolean isEtagsFromIfMatchHeaderContainsAWildcard() {

        for(IETag eTag : getRequestContext().request().getEtagsFromIfMatchHeader()) {
            if(eTag.isWildcard()) {
                return true;
            }
        }
        return false;
    }

    public List<IETag> getEtagsFromIfMatchHeader() {
        return getRequestContext().request().getEtagsFromIfMatchHeader();
    }

    public List<IETag> getEtagsFromIfNoneMatchHeader() {
        return getRequestContext().request().getEtagsFromIfNoneMatchHeader();
    }

    protected boolean isEtagsFromIfNoneMatchHeaderContainsAWildcard() {

        for(IETag eTag : getEtagsFromIfNoneMatchHeader()) {
            if(eTag.isWildcard()) {
                return true;
            }
        }
        return false;
    }

    protected boolean isIfMatchEtagMatches() {

        List<IETag> eTags = getEtagsFromIfMatchHeader();
        if(eTags == null || eTags.size() == 0) {
            return false;
        }

        for(IETag eTag : eTags) {

            //==========================================
            // "A server MUST use the strong comparison function (see section 13.3.3) to compare the entity tags in If-Match. "
            // @see https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.24
            //==========================================
            if(isEtagMatches(eTag, false)) {
                return true;
            }
        }

        return false;
    }

    protected boolean isIfNoneMatchEtagMatches() {

        List<IETag> eTags = getEtagsFromIfNoneMatchHeader();
        if(eTags == null || eTags.size() == 0) {
            return false;
        }

        for(IETag eTag : eTags) {
            if(isEtagMatches(eTag, isETagWeakComparison())) {
                return true;
            }
        }

        return false;
    }

    protected boolean isEtagMatches(IETag requestETag, boolean weakComparison) {

        IETag newETag = getETag();

        if(requestETag != null && requestETag.isWildcard()) {
            return true;
        }

        if(requestETag == null || newETag == null) {
            return false;
        }

        return newETag.matches(requestETag, weakComparison);
    }

    protected Date getDateFromIfModifiedSinceHeader() {
        return getRequestContext().request().getDateFromIfModifiedSinceHeader();
    }

    protected Date getDateFromIfUnmodifiedSinceHeader() {
        return getRequestContext().request().getDateFromIfUnmodifiedSinceHeader();
    }

    protected String formatDateForHttpHeader(Date date) {
        Objects.requireNonNull(date, "The date can't be NULL");
        return DateUtils.formatDate(date);
    }

    @Override
    public ICacheHeadersRequestContextAddon<R> cache(int seconds) {
        return cache(seconds, false, null);
    }

    @Override
    public ICacheHeadersRequestContextAddon<R> cache(int seconds, boolean isPrivate) {
        return cache(seconds, isPrivate, null);
    }

    @Override
    public ICacheHeadersRequestContextAddon<R> cache(int seconds, boolean isPrivate, Integer cdnSeconds) {

        if(seconds <= 0) {
            return noCache();
        }

        getRequestContext().response().removeHeader(HttpHeaders.PRAGMA);

        StringBuilder builder = new StringBuilder();
        if(isPrivate) {
            builder.append("private");
        } else {
            builder.append("public");
        }
        builder.append(", max-age=").append(seconds);

        if(cdnSeconds != null) {
            if(cdnSeconds < 0) {
                cdnSeconds = 0;
            }
            builder.append(", s-maxage=").append(cdnSeconds);
        }

        getRequestContext().response().setHeader(HttpHeaders.CACHE_CONTROL, builder.toString());

        Date date = org.spincast.shaded.org.apache.commons.lang3.time.DateUtils.addSeconds(new Date(), seconds);
        String dateStr = DateUtils.formatDate(date);
        getRequestContext().response().setHeader(HttpHeaders.EXPIRES, dateStr);

        return this;
    }

    @Override
    public SpincastCacheHeadersRequestContextAddon<R> noCache() {

        getRequestContext().response().removeHeader(HttpHeaders.ETAG);
        getRequestContext().response().removeHeader(HttpHeaders.LAST_MODIFIED);

        getRequestContext().response().setHeader(HttpHeaders.PRAGMA, "no-cache");
        getRequestContext().response().setHeader(HttpHeaders.EXPIRES, "Tue, 03 Jul 2001 06:00:00 GMT");
        getRequestContext().response().setHeader(HttpHeaders.CACHE_CONTROL,
                                                 "no-cache, no-store, max-age=0, must-revalidate, proxy-revalidate");
        return this;
    }

    @Override
    public boolean validate(boolean resourceCurrentlyExists) {

        //==========================================
        // The resource doesn't exist
        //==========================================
        if(!resourceCurrentlyExists) {

            //==========================================
            // The resource is expected to exist?
            //==========================================
            if(isEtagsFromIfMatchHeaderContainsAWildcard() ||
               (getEtagsFromIfMatchHeader().size() > 0) ||
               (getDateFromIfUnmodifiedSinceHeader() != null)) {
                getRequestContext().response().setStatusCode(HttpStatus.SC_PRECONDITION_FAILED);
                return true;
            } else {
                return false;
            }
        }

        //==========================================
        // Existing resource, ETag validation
        //==========================================
        boolean eTagHeadersAllowNotModifiedResponse = true;
        boolean eTagHeadersWantToSendNotModifiedResponse = false;
        if(getETag() != null) {

            //==========================================
            // The ETag should match
            //==========================================
            if(getEtagsFromIfMatchHeader().size() > 0) {
                if(!isIfMatchEtagMatches()) {
                    getRequestContext().response().setStatusCode(HttpStatus.SC_PRECONDITION_FAILED);
                    return true;
                } else {
                    //==========================================
                    // ETag says it wants the resource if it matches and it is,
                    // so we return it even if a "Not Modified" header
                    // says the resource is not modified.
                    //==========================================
                    eTagHeadersAllowNotModifiedResponse = false;
                }
            }

            //==========================================
            // The ETag should not match
            //==========================================
            if(getEtagsFromIfNoneMatchHeader().size() > 0) {

                //==========================================
                // The resource should not exist for this request to be
                // valid.
                //==========================================
                if(isEtagsFromIfNoneMatchHeaderContainsAWildcard()) {
                    getRequestContext().response().setStatusCode(HttpStatus.SC_PRECONDITION_FAILED);
                    return true;
                } else if(isIfNoneMatchEtagMatches()) {
                    eTagHeadersWantToSendNotModifiedResponse = true;
                } else {
                    eTagHeadersAllowNotModifiedResponse = false;
                }
            }
        }

        //==========================================
        // Existing resource, Last Modified validation
        //==========================================
        boolean lastModifiedDateHeadersAllowNotModifiedResponse = true;
        boolean lastModifiedDateHeadersWantToSendNotModifiedResponse = false;
        if(getLastModificationDate() != null) {

            Date ifUnmodifiedSinceHeader = getDateFromIfUnmodifiedSinceHeader();
            if(ifUnmodifiedSinceHeader != null) {
                if((getLastModificationDate().getTime() - ifUnmodifiedSinceHeader.getTime()) > 0) {
                    getRequestContext().response().setStatusCode(HttpStatus.SC_PRECONDITION_FAILED);
                    return true;
                } else {
                    //==========================================
                    // This header says it wants the resource,
                    // so we return it even if an ETag header
                    // says the resource is not modified.
                    //==========================================
                    lastModifiedDateHeadersAllowNotModifiedResponse = false;
                }
            }

            Date ifModifiedSinceDate = getDateFromIfModifiedSinceHeader();
            if(ifModifiedSinceDate != null) {
                if((getLastModificationDate().getTime() - ifModifiedSinceDate.getTime()) > 0) {
                    lastModifiedDateHeadersAllowNotModifiedResponse = false;
                } else {
                    lastModifiedDateHeadersWantToSendNotModifiedResponse = true;
                }
            }
        }

        //==========================================
        // An HTTP/1.1 origin server, upon receiving a conditional request that includes both a Last-Modified date 
        // (e.g., in an If-Modified-Since or If-Unmodified-Since header field) and one or more entity tags 
        // (e.g., in an If-Match, If-None-Match, or If-Range header field) as cache validators, 
        // MUST NOT return a response status of 304 (Not Modified) unless doing so is consistent 
        // with all of the conditional header fields in the request. 
        // @see https://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html#sec13.3.4
        //==========================================
        if((eTagHeadersAllowNotModifiedResponse && lastModifiedDateHeadersWantToSendNotModifiedResponse) ||
           (lastModifiedDateHeadersAllowNotModifiedResponse && eTagHeadersWantToSendNotModifiedResponse)) {
            getRequestContext().response().setStatusCode(HttpStatus.SC_NOT_MODIFIED);
            return true;
        }

        return false;
    }

}
