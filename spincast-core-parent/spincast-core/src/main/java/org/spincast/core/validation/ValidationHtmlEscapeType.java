package org.spincast.core.validation;


public enum ValidationHtmlEscapeType {
    ESCAPE,
    NO_ESCAPE,

    /**
     * Convert newlines to &lt;br /&gt; and
     * maybe some other small tweaks.
     */
    PRE

}
