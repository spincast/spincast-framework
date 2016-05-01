package org.spincast.plugins.httpclient;

import java.util.List;
import java.util.Map;

import org.spincast.core.cookies.ICookie;

import com.google.inject.assistedinject.Assisted;

/**
 * Factory to create Spincast Http Client's reponses.
 */
public interface IHttpResponseFactory {

    public IHttpResponse create(@Assisted("status") int status,
                                @Assisted("contentType") String contentType,
                                @Assisted("content") byte[] content,
                                @Assisted("headers") Map<String, List<String>> headers,
                                @Assisted("cookies") Map<String, ICookie> cookies,
                                @Assisted("wasZipped") boolean wasZipped);
}
