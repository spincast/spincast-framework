package org.spincast.plugins.httpclient.websocket.utils;

import org.spincast.plugins.httpclient.utils.SpincastHttpClientUtils;
import org.xnio.Xnio;

public class SpincastHttpClientWithWebsocketUtils extends SpincastHttpClientUtils
                                                  implements ISpincastHttpClientWithWebsocketUtils {

    private Xnio xnioInstance = null;

    @Override
    public Xnio getXnioInstance() {

        if(this.xnioInstance == null) {
            this.xnioInstance = Xnio.getInstance("nio", SpincastHttpClientWithWebsocketUtils.class.getClassLoader());
        }
        return this.xnioInstance;
    }
}
