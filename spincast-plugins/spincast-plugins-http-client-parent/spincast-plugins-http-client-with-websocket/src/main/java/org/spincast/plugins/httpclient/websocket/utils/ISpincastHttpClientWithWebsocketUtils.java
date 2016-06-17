package org.spincast.plugins.httpclient.websocket.utils;

import org.spincast.plugins.httpclient.utils.ISpincastHttpClientUtils;
import org.xnio.Xnio;

public interface ISpincastHttpClientWithWebsocketUtils extends ISpincastHttpClientUtils {

    public Xnio getXnioInstance();

}
