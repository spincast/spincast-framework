package org.spincast.plugins.gson.tests.utils;

import java.time.Instant;

public class Widget {

    private Instant instant;
    private String str;
    private Widget widget;

    public Instant getInstant() {
        return this.instant;
    }

    public void setInstant(Instant instant) {
        this.instant = instant;
    }

    public String getStr() {
        return this.str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public Widget getWidget() {
        return this.widget;
    }

    public void setWidget(Widget widget) {
        this.widget = widget;
    }
}
