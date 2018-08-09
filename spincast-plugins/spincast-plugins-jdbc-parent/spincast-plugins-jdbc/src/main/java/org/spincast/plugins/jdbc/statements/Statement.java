package org.spincast.plugins.jdbc.statements;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

import org.spincast.plugins.jdbc.JdbcScope;

/**
 * Note that the connection used by a Stement is
 * <strong>not</strong> closed after the statement has
 * been executed.
 * <p>
 * Have a look at {@link JdbcScope} to manage the 
 * closing of connections properly.
 */
public interface Statement {

    /**
     * Appends some SQL to the query to launch.
     * If you want to restart the query <em>from scratch</em>,
     * you need to call {@link #clearSql()} first!
     */
    public void sql(String sql);

    /**
     * Clears the current SQL query being built.
     * Also removes any bound parameters.
     */
    public void clearSql();

    /**
     * Clears the current SQL query being built.
     * @param keepCurrentBoundParams if <code>true</code>,
     * the currently bound parameters won't be cleared, only the
     * query.
     */
    public void clearSql(boolean keepCurrentBoundParams);

    /**
     * Returns the current SQL query.
     */
    public String getSql(boolean friendly);

    /**
     * This replaces setTimestamp()!
     */
    public void setInstant(String string, Instant instant);

    public void setDate(String paramName, LocalDate date);

    public void setBoolean(String paramName, Boolean value);

    public void setString(String paramName, String value);

    public void setInteger(String paramName, Integer value);

    public void setLong(String paramName, Long value);

    public void setFloat(String paramName, Float value);

    public void setDouble(String paramName, Double value);

    /**
     * Explodes a collection of Long and replaces the specified param with them, 
     * so it can be used inside a
     * IN(:ids) section
     */
    public void setInLong(String paramName, Set<Long> items);

    /**
     * Explodes a collection of Integer and replaces the specified param with them, 
     * so it can be used inside a
     * IN(:ids) section
     */
    public void setInInteger(String paramName, Set<Integer> items);

    /**
     * Explodes a collection of String and replaces the specified param with them, 
     * so it can be used inside a IN(:ids) section.
     */
    public void setInString(String paramName, Set<String> items);

    /**
     * Explodes a collection of enum names and replaces the specified param with them, 
     * so it can be used inside a IN(:ids) section.
     */
    public void setInStringFromEnumNames(String paramName, Set<? extends Enum<?>> enumItems);

}
