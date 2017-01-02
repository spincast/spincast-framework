package org.spincast.plugins.httpclient.websocket;

import org.spincast.plugins.httpclient.SpincastHttpClientPlugin;

import com.google.inject.Module;

public class SpincastHttpClientWithWebsocketPlugin extends SpincastHttpClientPlugin {

    public static final String PLUGIN_ID = SpincastHttpClientWithWebsocketPlugin.class.getName();

    @Override
    public String getId() {
        return PLUGIN_ID;
    }

    @Override
    protected Module getPluginModule() {
        return new SpincastHttpClientWithWebsocketPluginModule();
    }
}
