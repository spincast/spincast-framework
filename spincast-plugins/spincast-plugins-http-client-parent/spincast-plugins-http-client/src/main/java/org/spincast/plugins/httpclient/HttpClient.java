package org.spincast.plugins.httpclient;

import org.spincast.plugins.httpclient.builders.ConnectRequestBuilder;
import org.spincast.plugins.httpclient.builders.DeleteRequestBuilder;
import org.spincast.plugins.httpclient.builders.GetRequestBuilder;
import org.spincast.plugins.httpclient.builders.HeadRequestBuilder;
import org.spincast.plugins.httpclient.builders.OptionsRequestBuilder;
import org.spincast.plugins.httpclient.builders.PatchRequestBuilder;
import org.spincast.plugins.httpclient.builders.PostRequestBuilder;
import org.spincast.plugins.httpclient.builders.PutRequestBuilder;
import org.spincast.plugins.httpclient.builders.TraceRequestBuilder;

/**
 * Factory to create Http request builders.
 */
public interface HttpClient {

    /**
     * Starts a builder for a <code>GET</code> request.
     */
    public GetRequestBuilder GET(String url);

    /**
     * Starts a builder for a <code>POST</code> request.
     */
    public PostRequestBuilder POST(String url);

    /**
     * Starts a builder for a <code>PUT</code> request.
     */
    public PutRequestBuilder PUT(String url);

    /**
     * Starts a builder for a <code>DELETE</code> request.
     */
    public DeleteRequestBuilder DELETE(String url);

    /**
     * Starts a builder for a <code>OPTIONS</code> request.
     */
    public OptionsRequestBuilder OPTIONS(String url);

    /**
     * Starts a builder for a <code>HEAD</code> request.
     */
    public HeadRequestBuilder HEAD(String url);

    /**
     * Starts a builder for a <code>TRACE</code> request.
     */
    public TraceRequestBuilder TRACE(String url);

    /**
     * Starts a builder for a <code>CONNECT</code> request.
     */
    public ConnectRequestBuilder CONNECT(String url);

    /**
     * Starts a builder for a <code>PATCH</code> request.
     */
    public PatchRequestBuilder PATCH(String url);

}
