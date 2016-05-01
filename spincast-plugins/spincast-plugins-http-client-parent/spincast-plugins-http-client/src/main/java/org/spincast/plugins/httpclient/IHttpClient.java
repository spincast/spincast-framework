package org.spincast.plugins.httpclient;

import org.spincast.plugins.httpclient.builders.IConnectRequestBuilder;
import org.spincast.plugins.httpclient.builders.IDeleteRequestBuilder;
import org.spincast.plugins.httpclient.builders.IGetRequestBuilder;
import org.spincast.plugins.httpclient.builders.IHeadRequestBuilder;
import org.spincast.plugins.httpclient.builders.IOptionsRequestBuilder;
import org.spincast.plugins.httpclient.builders.IPatchRequestBuilder;
import org.spincast.plugins.httpclient.builders.IPostRequestBuilder;
import org.spincast.plugins.httpclient.builders.IPutRequestBuilder;
import org.spincast.plugins.httpclient.builders.ITraceRequestBuilder;

/**
 * Factory to create Http request builders.
 */
public interface IHttpClient {

    /**
     * Starts a builder for a <code>GET</code> request.
     */
    public IGetRequestBuilder GET(String url);

    /**
     * Starts a builder for a <code>POST</code> request.
     */
    public IPostRequestBuilder POST(String url);

    /**
     * Starts a builder for a <code>PUT</code> request.
     */
    public IPutRequestBuilder PUT(String url);

    /**
     * Starts a builder for a <code>DELETE</code> request.
     */
    public IDeleteRequestBuilder DELETE(String url);

    /**
     * Starts a builder for a <code>OPTIONS</code> request.
     */
    public IOptionsRequestBuilder OPTIONS(String url);

    /**
     * Starts a builder for a <code>HEAD</code> request.
     */
    public IHeadRequestBuilder HEAD(String url);

    /**
     * Starts a builder for a <code>TRACE</code> request.
     */
    public ITraceRequestBuilder TRACE(String url);

    /**
     * Starts a builder for a <code>CONNECT</code> request.
     */
    public IConnectRequestBuilder CONNECT(String url);

    /**
     * Starts a builder for a <code>PATCH</code> request.
     */
    public IPatchRequestBuilder PATCH(String url);

}
