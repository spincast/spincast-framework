package org.spincast.plugins.processutils;


public enum MavenProjectGoal {
    COMPILE("compile"),
    PACKAGE("package"),
    INSTALL("install");

    private final String value;

    private MavenProjectGoal(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

}
