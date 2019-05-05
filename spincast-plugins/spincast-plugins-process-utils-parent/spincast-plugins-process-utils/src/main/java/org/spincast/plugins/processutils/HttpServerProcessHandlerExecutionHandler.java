package org.spincast.plugins.processutils;

import org.spincast.plugins.processutils.exceptions.PortNotOpenException;

public interface HttpServerProcessHandlerExecutionHandler extends ProcessExecutionHandler {

    /**
     * Wait for the specified host:port to be connectable.
     *
     * @throws A {@link PortNotOpenException} exception, if the
     * port is still not connectbale after the specified amount of time.
     */
    public void waitForPortOpen(String host, int port, int nbrTry, int sleepMilliseconds) throws PortNotOpenException;
}
