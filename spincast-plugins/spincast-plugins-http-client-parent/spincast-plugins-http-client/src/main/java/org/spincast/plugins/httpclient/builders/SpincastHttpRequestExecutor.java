package org.spincast.plugins.httpclient.builders;

import java.io.IOException;

import org.spincast.shaded.org.apache.http.Header;
import org.spincast.shaded.org.apache.http.HttpClientConnection;
import org.spincast.shaded.org.apache.http.HttpException;
import org.spincast.shaded.org.apache.http.HttpRequest;
import org.spincast.shaded.org.apache.http.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.shaded.org.apache.http.protocol.HttpContext;
import org.spincast.shaded.org.apache.http.protocol.HttpRequestExecutor;
import org.spincast.shaded.org.apache.http.util.Args;

public class SpincastHttpRequestExecutor extends HttpRequestExecutor {

    /**
     * We have to override thhis method since we want a
     * Websocket upgrade response to be returned as is.
     * The Http Client doesn't manage Websocket upgrades
     * by itself.
     */
    @Override
    protected HttpResponse doReceiveResponse(final HttpRequest request,
                                             final HttpClientConnection conn,
                                             final HttpContext context) throws HttpException, IOException {
        Args.notNull(request, "HTTP request");
        Args.notNull(conn, "Client connection");
        Args.notNull(context, "HTTP context");
        HttpResponse response = null;
        int statusCode = 0;

        while(response == null || statusCode < HttpStatus.SC_OK) {

            response = conn.receiveResponseHeader();
            if(canResponseHaveBody(request, response)) {
                conn.receiveResponseEntity(response);
            }
            statusCode = response.getStatusLine().getStatusCode();

            //==========================================
            // Is it a Websocket upgrade response?
            //==========================================
            if(isWebsocketUpgrade(response)) {
                break;
            }
        } // while intermediate response

        return response;
    }

    protected boolean isWebsocketUpgrade(HttpResponse response) {

        if(response != null) {
            int statusCode = response.getStatusLine().getStatusCode();
            if(statusCode == HttpStatus.SC_SWITCHING_PROTOCOLS) {
                Header upgradeHeader = response.getFirstHeader("Upgrade");
                if(upgradeHeader != null && "WebSocket".equalsIgnoreCase(upgradeHeader.getValue())) {
                    return true;
                }
            }
        }
        return false;
    }

}
