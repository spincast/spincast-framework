package org.spincast.plugins.httpclient.websocket.utils;

import org.spincast.plugins.httpclient.utils.SpincastHttpClientUtilsDefault;
import org.xnio.Xnio;

public class SpincastHttpClientWithWebsocketUtilsDefault extends SpincastHttpClientUtilsDefault
                                                         implements SpincastHttpClientWithWebsocketUtils {

    private Xnio xnioInstance = null;

    @Override
    public Xnio getXnioInstance() {

        if(this.xnioInstance == null) {
            this.xnioInstance = Xnio.getInstance("nio", SpincastHttpClientWithWebsocketUtilsDefault.class.getClassLoader());
        }
        return this.xnioInstance;
    }
}
