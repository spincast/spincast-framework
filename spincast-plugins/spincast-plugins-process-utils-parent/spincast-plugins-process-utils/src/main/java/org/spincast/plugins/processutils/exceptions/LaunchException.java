package org.spincast.plugins.processutils.exceptions;


public class LaunchException extends Exception {

    private static final long serialVersionUID = 1L;

    public LaunchException(Exception ex) {
        super(ex);
    }
}
