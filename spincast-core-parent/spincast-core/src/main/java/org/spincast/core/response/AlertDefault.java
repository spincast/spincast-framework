package org.spincast.core.response;

import java.util.Objects;

public class AlertDefault implements Alert {

    private final AlertLevel alertType;
    private final String text;

    public AlertDefault(AlertLevel alertType, String text) {

        Objects.requireNonNull(alertType, "The alertType can't be NULL");

        this.alertType = alertType;
        this.text = text;
    }

    @Override
    public AlertLevel getAlertType() {
        return this.alertType;
    }

    @Override
    public String getText() {
        return this.text;
    }

}
