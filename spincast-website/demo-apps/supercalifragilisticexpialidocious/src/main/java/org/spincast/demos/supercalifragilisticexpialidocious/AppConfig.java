package org.spincast.demos.supercalifragilisticexpialidocious;

import org.spincast.plugins.config.SpincastConfigDefault;

public class AppConfig extends SpincastConfigDefault {

    // We change the port the Server will be started on
    @Override
    public int getHttpServerPort() {
        return 4242;
    }
}
