package org.spincast.plugins.httpclient.websocket.utils;

import org.spincast.plugins.httpclient.utils.SpincastHttpClientUtils;
import org.xnio.Xnio;

public interface SpincastHttpClientWithWebsocketUtils extends SpincastHttpClientUtils {

    public Xnio getXnioInstance();

}
