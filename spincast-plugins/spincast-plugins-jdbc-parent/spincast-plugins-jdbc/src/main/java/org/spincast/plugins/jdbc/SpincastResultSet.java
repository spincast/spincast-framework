package org.spincast.plugins.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

/**
 * Resultset that adds a "getXXXOrNull()"
 * method for every primitive types.
 * By default, Jdbc will return 0 when a value is
 * null and "getInt()" or "getLong()" is used, for example.
 * <p>
 * Also add some new getters.
 */
public interface SpincastResultSet extends ResultSet {

    /**
     * Returns <code>null</code> when null, not <code>false</code> as
     * JDBC does by default!
     */
    public Boolean getBooleanOrNull(int columnIndex) throws SQLException;

    /**
     * Returns <code>null</code> when null, not <code>false</code> as
     * JDBC does by default!
     */
    public Boolean getBooleanOrNull(String columnLabel) throws SQLException;

    /**
     * Returns <code>false</code> when <code>null</code>.
     */
    public boolean getBooleanOrFalse(int columnIndex) throws SQLException;

    /**
     * Returns <code>false</code> when <code>null</code>.
     */
    public boolean getBooleanOrFalse(String columnLabel) throws SQLException;

    /**
     * Returns <code>null</code> when null, not <code>0</code> as
     * JDBC does by default!
     */
    public Byte getByteOrNull(int columnIndex) throws SQLException;

    /**
     * Returns <code>null</code> when null, not <code>0</code> as
     * JDBC does by default!
     */
    public Byte getByteOrNull(String columnLabel) throws SQLException;

    /**
     * Returns <code>0</code> when null.
     */
    public Byte getByteOrZero(int columnIndex) throws SQLException;

    /**
     * Returns <code>0</code> when null.
     */
    public Byte getByteOrZero(String columnLabel) throws SQLException;

    /**
     * Returns <code>null</code> when null, not <code>0</code> as
     * JDBC does by default!
     */
    public Short getShortOrNull(int columnIndex) throws SQLException;

    /**
     * Returns <code>null</code> when null, not <code>0</code> as
     * JDBC does by default!
     */
    public Short getShortOrNull(String columnLabel) throws SQLException;

    /**
     * Returns <code>0</code> when null.
     */
    public Short getShortOrZero(int columnIndex) throws SQLException;

    /**
     * Returns <code>0</code> when null.
     */
    public Short getShortOrZero(String columnLabel) throws SQLException;

    /**
     * Returns <code>null</code> when null, not <code>0</code> as
     * JDBC does by default!
     */
    public Integer getIntegerOrNull(int columnIndex) throws SQLException;

    /**
     * Returns <code>null</code> when null, not <code>0</code> as
     * JDBC does by default!
     */
    public Integer getIntegerOrNull(String columnLabel) throws SQLException;

    /**
     * Returns <code>0</code> when null.
     */
    public Integer getIntegerOrZero(int columnIndex) throws SQLException;

    /**
     * Returns <code>0</code> when null.
     */
    public Integer getIntegerOrZero(String columnLabel) throws SQLException;

    /**
     * Returns <code>null</code> when null, not <code>0</code> as
     * JDBC does by default!
     */
    public Long getLongOrNull(int columnIndex) throws SQLException;

    /**
     * Returns <code>null</code> when null, not <code>0</code> as
     * JDBC does by default!
     */
    public Long getLongOrNull(String columnLabel) throws SQLException;

    /**
     * Returns <code>0</code> when null.
     */
    public Long getLongOrZero(int columnIndex) throws SQLException;

    /**
     * Returns <code>0</code> when null.
     */
    public Long getLongOrZero(String columnLabel) throws SQLException;

    /**
     * Returns <code>null</code> when null, not <code>0</code> as
     * JDBC does by default!
     */
    public Float getFloatOrNull(int columnIndex) throws SQLException;

    /**
     * Returns <code>null</code> when null, not <code>0</code> as
     * JDBC does by default!
     */
    public Float getFloatOrNull(String columnLabel) throws SQLException;

    /**
     * Returns <code>0</code> when null.
     */
    public Float getFloatOrZero(int columnIndex) throws SQLException;

    /**
     * Returns <code>0</code> when null.
     */
    public Float getFloatOrZero(String columnLabel) throws SQLException;

    /**
     * Returns <code>null</code> when null, not <code>0</code> as
     * JDBC does by default!
     */
    public Double getDoubleOrNull(int columnIndex) throws SQLException;

    /**
     * Returns <code>null</code> when null, not <code>0</code> as
     * JDBC does by default!
     */
    public Double getDoubleOrNull(String columnLabel) throws SQLException;

    /**
     * Returns <code>0</code> when null.
     */
    public Double getDoubleOrZero(int columnIndex) throws SQLException;

    /**
     * Returns <code>0</code> when null.
     */
    public Double getDoubleOrZero(String columnLabel) throws SQLException;

    /**
     * Use {@link #getBooleanOrNull(int)} or
     * {@link #getBooleanOrFalse(int)} instead.
     */
    @Deprecated
    @Override
    boolean getBoolean(int columnIndex) throws SQLException;

    /**
     * Use {@link #getBooleanOrNull(String)} or
     * {@link #getBooleanOrFalse(String)} instead.
     */
    @Deprecated
    @Override
    boolean getBoolean(String columnLabel) throws SQLException;

    /**
     * Use {@link #getByteOrNull(int)} or {@link #getByteOrZero(int)} instead.
     */
    @Deprecated
    @Override
    byte getByte(int columnIndex) throws SQLException;

    /**
     * Use {@link #getByteOrNull(String)}or {@link #getByteOrZero(String)} instead.
     */
    @Deprecated
    @Override
    byte getByte(String columnLabel) throws SQLException;

    /**
     * Use {@link #getShortOrNull(int)} or {@link #getShortOrZero(int)}} instead.
     */
    @Deprecated
    @Override
    short getShort(int columnIndex) throws SQLException;

    /**
     * Use {@link #getShortOrNull(String)}  or {@link #getShortOrZero(String)}instead.
     */
    @Deprecated
    @Override
    short getShort(String columnLabel) throws SQLException;

    /**
     * Use {@link #getIntegerOrNull(int)} or {@link #getIntegerOrZero(int)} instead.
     */
    @Deprecated
    @Override
    int getInt(int columnIndex) throws SQLException;

    /**
     * Use {@link #getIntegerOrNull(String)} or {@link #getIntegerOrZero(String)} instead.
     */
    @Deprecated
    @Override
    int getInt(String columnLabel) throws SQLException;

    /**
     * Use {@link #getLongOrNull(int)}  or {@link #getLongOrLong(int)} instead.
     */
    @Deprecated
    @Override
    long getLong(int columnIndex) throws SQLException;

    /**
     * Use {@link #getLongOrNull(String)} or {@link #getLongOrZero(String)}  instead.
     */
    @Deprecated
    @Override
    long getLong(String columnLabel) throws SQLException;

    /**
     * Use {@link #getFloatOrNull(int)} or {@link #getFloatOrZero(int)} instead.
     */
    @Deprecated
    @Override
    float getFloat(int columnIndex) throws SQLException;

    /**
     * Use {@link #getFloatOrNull(String)} or {@link #getFloatOrZero(String)} instead.
     */
    @Deprecated
    @Override
    float getFloat(String columnLabel) throws SQLException;

    /**
     * Use {@link #getDoubleOrNull(int)} or {@link #getDoubleOrZero(int)} instead.
     */
    @Deprecated
    @Override
    double getDouble(int columnIndex) throws SQLException;

    /**
     * Use {@link #getDoubleOrNull(String)} or {@link #getDoubleOrZero(String)} instead.
     */
    @Deprecated
    @Override
    double getDouble(String columnLabel) throws SQLException;

    /**
     * Returns an {@link Instant} from a <code>TIMESTAMP WITH TIME ZONE</code>
     * (ie: "TIMESTAMPTZ") column.
     * <p>
     * Make sure the type of the column you use can store up to <em>nanoseconds</em>,
     * if this is required.
     */
    public Instant getInstant(int columnIndex) throws SQLException;

    /**
     * Returns an {@link Instant} from a <code>TIMESTAMP WITH TIME ZONE</code>
     * (ie: "TIMESTAMPTZ") column.
     * <p>
     * Make sure the type of the column you use can store up to <em>nanoseconds</em>,
     * if this is required.
     */
    public Instant getInstant(String columnLabel) throws SQLException;

}
