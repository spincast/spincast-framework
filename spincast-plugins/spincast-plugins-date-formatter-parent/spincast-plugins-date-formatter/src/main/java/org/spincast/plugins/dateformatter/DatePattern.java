package org.spincast.plugins.dateformatter;

import java.text.DateFormat;

/**
 * The default format patterns + ISO8601
 * 
 * @see https://docs.oracle.com/javase/tutorial/i18n/format/dateFormat.html
 */
public enum DatePattern {
    SHORT(DateFormat.SHORT),
    MEDIUM(DateFormat.MEDIUM),
    LONG(DateFormat.LONG),
    FULL(DateFormat.FULL),
    ISO(-1);

    private final int patternNbr;

    private DatePattern(int patternNbr) {
        this.patternNbr = patternNbr;
    }

    public int getPatternNbr() {
        return this.patternNbr;
    }
}
