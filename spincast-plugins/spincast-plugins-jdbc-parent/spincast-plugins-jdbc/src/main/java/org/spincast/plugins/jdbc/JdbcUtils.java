package org.spincast.plugins.jdbc;

import java.util.Calendar;
import java.util.TimeZone;

public interface JdbcUtils {

    public static final Calendar UTC_CALENDAR = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

    public JdbcScope scopes();

    public JdbcStatementFactory statements();

}
