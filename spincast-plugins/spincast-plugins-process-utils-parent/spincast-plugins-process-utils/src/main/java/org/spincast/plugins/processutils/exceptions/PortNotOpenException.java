package org.spincast.plugins.processutils.exceptions;


public class PortNotOpenException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PortNotOpenException(String message) {
        super(message);
    }

}
