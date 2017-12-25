package org.spincast.plugins.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Calendar;
import java.util.Map;


public class SpincastResultSetDefault implements SpincastResultSet {

    private final ResultSet wrappedResultSet;

    public SpincastResultSetDefault(ResultSet wrappedResultSet) {
        this.wrappedResultSet = wrappedResultSet;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return this.wrappedResultSet.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return this.wrappedResultSet.isWrapperFor(iface);
    }

    @Override
    public boolean next() throws SQLException {
        return this.wrappedResultSet.next();
    }

    @Override
    public void close() throws SQLException {
        this.wrappedResultSet.close();
    }

    @Override
    public boolean wasNull() throws SQLException {
        return this.wrappedResultSet.wasNull();
    }

    @Override
    public String getString(int columnIndex) throws SQLException {
        return this.wrappedResultSet.getString(columnIndex);
    }

    @Override
    public boolean getBoolean(int columnIndex) throws SQLException {
        return this.wrappedResultSet.getBoolean(columnIndex);
    }

    @Override
    public Boolean getBooleanOrNull(int columnIndex) throws SQLException {
        boolean val = getBoolean(columnIndex);
        return wasNull() ? null : val;
    }

    @Override
    public boolean getBoolean(String columnLabel) throws SQLException {
        return this.wrappedResultSet.getBoolean(columnLabel);
    }

    @Override
    public Boolean getBooleanOrNull(String columnLabel) throws SQLException {
        boolean val = getBoolean(columnLabel);
        return wasNull() ? null : val;
    }

    @Override
    public boolean getBooleanOrFalse(int columnIndex) throws SQLException {
        boolean val = getBoolean(columnIndex);
        return wasNull() ? false : val;
    }

    @Override
    public boolean getBooleanOrFalse(String columnLabel) throws SQLException {
        boolean val = getBoolean(columnLabel);
        return wasNull() ? false : val;
    }

    @Override
    public byte getByte(int columnIndex) throws SQLException {
        return this.wrappedResultSet.getByte(columnIndex);
    }

    @Override
    public Byte getByteOrNull(int columnIndex) throws SQLException {
        byte val = getByte(columnIndex);
        return wasNull() ? null : val;
    }

    @Override
    public byte getByte(String columnLabel) throws SQLException {
        return this.wrappedResultSet.getByte(columnLabel);
    }

    @Override
    public Byte getByteOrNull(String columnLabel) throws SQLException {
        byte val = getByte(columnLabel);
        return wasNull() ? null : val;
    }

    @Override
    public short getShort(int columnIndex) throws SQLException {
        return this.wrappedResultSet.getShort(columnIndex);
    }

    @Override
    public Short getShortOrNull(int columnIndex) throws SQLException {
        short val = getShort(columnIndex);
        return wasNull() ? null : val;
    }

    @Override
    public short getShort(String columnLabel) throws SQLException {
        return this.wrappedResultSet.getShort(columnLabel);
    }

    @Override
    public Short getShortOrNull(String columnLabel) throws SQLException {
        short val = getShort(columnLabel);
        return wasNull() ? null : val;
    }

    @Override
    public int getInt(int columnIndex) throws SQLException {
        return this.wrappedResultSet.getInt(columnIndex);
    }

    @Override
    public Integer getIntOrNull(int columnIndex) throws SQLException {
        int val = getInt(columnIndex);
        return wasNull() ? null : val;
    }

    @Override
    public int getInt(String columnLabel) throws SQLException {
        return this.wrappedResultSet.getInt(columnLabel);
    }

    @Override
    public Integer getIntOrNull(String columnLabel) throws SQLException {
        int val = getInt(columnLabel);
        return wasNull() ? null : val;
    }

    @Override
    public long getLong(int columnIndex) throws SQLException {
        return this.wrappedResultSet.getLong(columnIndex);
    }

    @Override
    public Long getLongOrNull(int columnIndex) throws SQLException {
        long val = getLong(columnIndex);
        return wasNull() ? null : val;
    }

    @Override
    public long getLong(String columnLabel) throws SQLException {
        return this.wrappedResultSet.getLong(columnLabel);
    }

    @Override
    public Long getLongOrNull(String columnLabel) throws SQLException {
        long val = getLong(columnLabel);
        return wasNull() ? null : val;
    }

    @Override
    public float getFloat(int columnIndex) throws SQLException {
        return this.wrappedResultSet.getFloat(columnIndex);
    }

    @Override
    public Float getFloatOrNull(int columnIndex) throws SQLException {
        float val = getFloat(columnIndex);
        return wasNull() ? null : val;
    }

    @Override
    public float getFloat(String columnLabel) throws SQLException {
        return this.wrappedResultSet.getFloat(columnLabel);
    }

    @Override
    public Float getFloatOrNull(String columnLabel) throws SQLException {
        float val = getFloat(columnLabel);
        return wasNull() ? null : val;
    }

    @Override
    public double getDouble(int columnIndex) throws SQLException {
        return this.wrappedResultSet.getDouble(columnIndex);
    }

    @Override
    public Double getDoubleOrNull(int columnIndex) throws SQLException {
        double val = getDouble(columnIndex);
        return wasNull() ? null : val;
    }

    @Override
    public double getDouble(String columnLabel) throws SQLException {
        return this.wrappedResultSet.getDouble(columnLabel);
    }

    @Override
    public Double getDoubleOrNull(String columnLabel) throws SQLException {
        double val = getDouble(columnLabel);
        return wasNull() ? null : val;
    }

    @SuppressWarnings("deprecation")
    @Override
    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        return this.wrappedResultSet.getBigDecimal(columnIndex, scale);
    }

    @Override
    public Date getDate(int columnIndex) throws SQLException {
        return this.wrappedResultSet.getDate(columnIndex);
    }

    @Override
    public Time getTime(int columnIndex) throws SQLException {
        return this.wrappedResultSet.getTime(columnIndex);
    }

    @Override
    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        return this.wrappedResultSet.getTimestamp(columnIndex);
    }

    @Override
    public InputStream getAsciiStream(int columnIndex) throws SQLException {
        return this.wrappedResultSet.getAsciiStream(columnIndex);
    }

    @SuppressWarnings("deprecation")
    @Override
    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
        return this.wrappedResultSet.getUnicodeStream(columnIndex);
    }

    @Override
    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        return this.wrappedResultSet.getBinaryStream(columnIndex);
    }

    @Override
    public String getString(String columnLabel) throws SQLException {
        return this.wrappedResultSet.getString(columnLabel);
    }

    @SuppressWarnings("deprecation")
    @Override
    public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
        return this.wrappedResultSet.getBigDecimal(columnLabel, scale);
    }

    @Override
    public byte[] getBytes(int columnIndex) throws SQLException {
        return this.wrappedResultSet.getBytes(columnIndex);
    }

    @Override
    public byte[] getBytes(String columnLabel) throws SQLException {
        return this.wrappedResultSet.getBytes(columnLabel);
    }

    @Override
    public Date getDate(String columnLabel) throws SQLException {
        return this.wrappedResultSet.getDate(columnLabel);
    }

    @Override
    public Time getTime(String columnLabel) throws SQLException {
        return this.wrappedResultSet.getTime(columnLabel);
    }

    @Override
    public Timestamp getTimestamp(String columnLabel) throws SQLException {
        return this.wrappedResultSet.getTimestamp(columnLabel);
    }

    @Override
    public InputStream getAsciiStream(String columnLabel) throws SQLException {
        return this.wrappedResultSet.getAsciiStream(columnLabel);
    }

    @SuppressWarnings("deprecation")
    @Override
    public InputStream getUnicodeStream(String columnLabel) throws SQLException {
        return this.wrappedResultSet.getUnicodeStream(columnLabel);
    }

    @Override
    public InputStream getBinaryStream(String columnLabel) throws SQLException {
        return this.wrappedResultSet.getBinaryStream(columnLabel);
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return this.wrappedResultSet.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        this.wrappedResultSet.clearWarnings();
    }

    @Override
    public String getCursorName() throws SQLException {
        return this.wrappedResultSet.getCursorName();
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return this.wrappedResultSet.getMetaData();
    }

    @Override
    public Object getObject(int columnIndex) throws SQLException {
        return this.wrappedResultSet.getObject(columnIndex);
    }

    @Override
    public Object getObject(String columnLabel) throws SQLException {
        return this.wrappedResultSet.getObject(columnLabel);
    }

    @Override
    public int findColumn(String columnLabel) throws SQLException {
        return this.wrappedResultSet.findColumn(columnLabel);
    }

    @Override
    public Reader getCharacterStream(int columnIndex) throws SQLException {
        return this.wrappedResultSet.getCharacterStream(columnIndex);
    }

    @Override
    public Reader getCharacterStream(String columnLabel) throws SQLException {
        return this.wrappedResultSet.getCharacterStream(columnLabel);
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        return this.wrappedResultSet.getBigDecimal(columnIndex);
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        return this.wrappedResultSet.getBigDecimal(columnLabel);
    }

    @Override
    public boolean isBeforeFirst() throws SQLException {
        return this.wrappedResultSet.isBeforeFirst();
    }

    @Override
    public boolean isAfterLast() throws SQLException {
        return this.wrappedResultSet.isAfterLast();
    }

    @Override
    public boolean isFirst() throws SQLException {
        return this.wrappedResultSet.isFirst();
    }

    @Override
    public boolean isLast() throws SQLException {
        return this.wrappedResultSet.isLast();
    }

    @Override
    public void beforeFirst() throws SQLException {
        this.wrappedResultSet.beforeFirst();
    }

    @Override
    public void afterLast() throws SQLException {
        this.wrappedResultSet.afterLast();
    }

    @Override
    public boolean first() throws SQLException {
        return this.wrappedResultSet.first();
    }

    @Override
    public boolean last() throws SQLException {
        return this.wrappedResultSet.last();
    }

    @Override
    public int getRow() throws SQLException {
        return this.wrappedResultSet.getRow();
    }

    @Override
    public boolean absolute(int row) throws SQLException {
        return this.wrappedResultSet.absolute(row);
    }

    @Override
    public boolean relative(int rows) throws SQLException {
        return this.wrappedResultSet.relative(rows);
    }

    @Override
    public boolean previous() throws SQLException {
        return this.wrappedResultSet.previous();
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        this.wrappedResultSet.setFetchDirection(direction);
    }

    @Override
    public int getFetchDirection() throws SQLException {
        return this.wrappedResultSet.getFetchDirection();
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        this.wrappedResultSet.setFetchSize(rows);
    }

    @Override
    public int getFetchSize() throws SQLException {
        return this.wrappedResultSet.getFetchSize();
    }

    @Override
    public int getType() throws SQLException {
        return this.wrappedResultSet.getType();
    }

    @Override
    public int getConcurrency() throws SQLException {
        return this.wrappedResultSet.getConcurrency();
    }

    @Override
    public boolean rowUpdated() throws SQLException {
        return this.wrappedResultSet.rowUpdated();
    }

    @Override
    public boolean rowInserted() throws SQLException {
        return this.wrappedResultSet.rowInserted();
    }

    @Override
    public boolean rowDeleted() throws SQLException {
        return this.wrappedResultSet.rowDeleted();
    }

    @Override
    public void updateNull(int columnIndex) throws SQLException {
        this.wrappedResultSet.updateNull(columnIndex);
    }

    @Override
    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        this.wrappedResultSet.updateBoolean(columnIndex, x);
    }

    @Override
    public void updateByte(int columnIndex, byte x) throws SQLException {
        this.wrappedResultSet.updateByte(columnIndex, x);
    }

    @Override
    public void updateShort(int columnIndex, short x) throws SQLException {
        this.wrappedResultSet.updateShort(columnIndex, x);
    }

    @Override
    public void updateInt(int columnIndex, int x) throws SQLException {
        this.wrappedResultSet.updateInt(columnIndex, x);
    }

    @Override
    public void updateLong(int columnIndex, long x) throws SQLException {
        this.wrappedResultSet.updateLong(columnIndex, x);
    }

    @Override
    public void updateFloat(int columnIndex, float x) throws SQLException {
        this.wrappedResultSet.updateFloat(columnIndex, x);
    }

    @Override
    public void updateDouble(int columnIndex, double x) throws SQLException {
        this.wrappedResultSet.updateDouble(columnIndex, x);
    }

    @Override
    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        this.wrappedResultSet.updateBigDecimal(columnIndex, x);
    }

    @Override
    public void updateString(int columnIndex, String x) throws SQLException {
        this.wrappedResultSet.updateString(columnIndex, x);
    }

    @Override
    public void updateBytes(int columnIndex, byte[] x) throws SQLException {
        this.wrappedResultSet.updateBytes(columnIndex, x);
    }

    @Override
    public void updateDate(int columnIndex, Date x) throws SQLException {
        this.wrappedResultSet.updateDate(columnIndex, x);
    }

    @Override
    public void updateTime(int columnIndex, Time x) throws SQLException {
        this.wrappedResultSet.updateTime(columnIndex, x);
    }

    @Override
    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        this.wrappedResultSet.updateTimestamp(columnIndex, x);
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
        this.wrappedResultSet.updateAsciiStream(columnIndex, x, length);
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
        this.wrappedResultSet.updateBinaryStream(columnIndex, x, length);
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
        this.wrappedResultSet.updateCharacterStream(columnIndex, x, length);
    }

    @Override
    public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
        this.wrappedResultSet.updateObject(columnIndex, x, scaleOrLength);
    }

    @Override
    public void updateObject(int columnIndex, Object x) throws SQLException {
        this.wrappedResultSet.updateObject(columnIndex, x);
    }

    @Override
    public void updateNull(String columnLabel) throws SQLException {
        this.wrappedResultSet.updateNull(columnLabel);
    }

    @Override
    public void updateBoolean(String columnLabel, boolean x) throws SQLException {
        this.wrappedResultSet.updateBoolean(columnLabel, x);
    }

    @Override
    public void updateByte(String columnLabel, byte x) throws SQLException {
        this.wrappedResultSet.updateByte(columnLabel, x);
    }

    @Override
    public void updateShort(String columnLabel, short x) throws SQLException {
        this.wrappedResultSet.updateShort(columnLabel, x);
    }

    @Override
    public void updateInt(String columnLabel, int x) throws SQLException {
        this.wrappedResultSet.updateInt(columnLabel, x);
    }

    @Override
    public void updateLong(String columnLabel, long x) throws SQLException {
        this.wrappedResultSet.updateLong(columnLabel, x);
    }

    @Override
    public void updateFloat(String columnLabel, float x) throws SQLException {
        this.wrappedResultSet.updateFloat(columnLabel, x);
    }

    @Override
    public void updateDouble(String columnLabel, double x) throws SQLException {
        this.wrappedResultSet.updateDouble(columnLabel, x);
    }

    @Override
    public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
        this.wrappedResultSet.updateBigDecimal(columnLabel, x);
    }

    @Override
    public void updateString(String columnLabel, String x) throws SQLException {
        this.wrappedResultSet.updateString(columnLabel, x);
    }

    @Override
    public void updateBytes(String columnLabel, byte[] x) throws SQLException {
        this.wrappedResultSet.updateBytes(columnLabel, x);
    }

    @Override
    public void updateDate(String columnLabel, Date x) throws SQLException {
        this.wrappedResultSet.updateDate(columnLabel, x);
    }

    @Override
    public void updateTime(String columnLabel, Time x) throws SQLException {
        this.wrappedResultSet.updateTime(columnLabel, x);
    }

    @Override
    public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
        this.wrappedResultSet.updateTimestamp(columnLabel, x);
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
        this.wrappedResultSet.updateAsciiStream(columnLabel, x, length);
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
        this.wrappedResultSet.updateBinaryStream(columnLabel, x, length);
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
        this.wrappedResultSet.updateCharacterStream(columnLabel, reader, length);
    }

    @Override
    public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
        this.wrappedResultSet.updateObject(columnLabel, x, scaleOrLength);
    }

    @Override
    public void updateObject(String columnLabel, Object x) throws SQLException {
        this.wrappedResultSet.updateObject(columnLabel, x);
    }

    @Override
    public void insertRow() throws SQLException {
        this.wrappedResultSet.insertRow();
    }

    @Override
    public void updateRow() throws SQLException {
        this.wrappedResultSet.updateRow();
    }

    @Override
    public void deleteRow() throws SQLException {
        this.wrappedResultSet.deleteRow();
    }

    @Override
    public void refreshRow() throws SQLException {
        this.wrappedResultSet.refreshRow();
    }

    @Override
    public void cancelRowUpdates() throws SQLException {
        this.wrappedResultSet.cancelRowUpdates();
    }

    @Override
    public void moveToInsertRow() throws SQLException {
        this.wrappedResultSet.moveToInsertRow();
    }

    @Override
    public void moveToCurrentRow() throws SQLException {
        this.wrappedResultSet.moveToCurrentRow();
    }

    @Override
    public Statement getStatement() throws SQLException {
        return this.wrappedResultSet.getStatement();
    }

    @Override
    public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
        return this.wrappedResultSet.getObject(columnIndex, map);
    }

    @Override
    public Ref getRef(int columnIndex) throws SQLException {
        return this.wrappedResultSet.getRef(columnIndex);
    }

    @Override
    public Blob getBlob(int columnIndex) throws SQLException {
        return this.wrappedResultSet.getBlob(columnIndex);
    }

    @Override
    public Clob getClob(int columnIndex) throws SQLException {
        return this.wrappedResultSet.getClob(columnIndex);
    }

    @Override
    public Array getArray(int columnIndex) throws SQLException {
        return this.wrappedResultSet.getArray(columnIndex);
    }

    @Override
    public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
        return this.wrappedResultSet.getObject(columnLabel, map);
    }

    @Override
    public Ref getRef(String columnLabel) throws SQLException {
        return this.wrappedResultSet.getRef(columnLabel);
    }

    @Override
    public Blob getBlob(String columnLabel) throws SQLException {
        return this.wrappedResultSet.getBlob(columnLabel);
    }

    @Override
    public Clob getClob(String columnLabel) throws SQLException {
        return this.wrappedResultSet.getClob(columnLabel);
    }

    @Override
    public Array getArray(String columnLabel) throws SQLException {
        return this.wrappedResultSet.getArray(columnLabel);
    }

    @Override
    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        return this.wrappedResultSet.getDate(columnIndex, cal);
    }

    @Override
    public Date getDate(String columnLabel, Calendar cal) throws SQLException {
        return this.wrappedResultSet.getDate(columnLabel, cal);
    }

    @Override
    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        return this.wrappedResultSet.getTime(columnIndex, cal);
    }

    @Override
    public Time getTime(String columnLabel, Calendar cal) throws SQLException {
        return this.wrappedResultSet.getTime(columnLabel, cal);
    }

    @Override
    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        return this.wrappedResultSet.getTimestamp(columnIndex, cal);
    }

    @Override
    public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
        return this.wrappedResultSet.getTimestamp(columnLabel, cal);
    }

    @Override
    public URL getURL(int columnIndex) throws SQLException {
        return this.wrappedResultSet.getURL(columnIndex);
    }

    @Override
    public URL getURL(String columnLabel) throws SQLException {
        return this.wrappedResultSet.getURL(columnLabel);
    }

    @Override
    public void updateRef(int columnIndex, Ref x) throws SQLException {
        this.wrappedResultSet.updateRef(columnIndex, x);
    }

    @Override
    public void updateRef(String columnLabel, Ref x) throws SQLException {
        this.wrappedResultSet.updateRef(columnLabel, x);
    }

    @Override
    public void updateBlob(int columnIndex, Blob x) throws SQLException {
        this.wrappedResultSet.updateBlob(columnIndex, x);
    }

    @Override
    public void updateBlob(String columnLabel, Blob x) throws SQLException {
        this.wrappedResultSet.updateBlob(columnLabel, x);
    }

    @Override
    public void updateClob(int columnIndex, Clob x) throws SQLException {
        this.wrappedResultSet.updateClob(columnIndex, x);
    }

    @Override
    public void updateClob(String columnLabel, Clob x) throws SQLException {
        this.wrappedResultSet.updateClob(columnLabel, x);
    }

    @Override
    public void updateArray(int columnIndex, Array x) throws SQLException {
        this.wrappedResultSet.updateArray(columnIndex, x);
    }

    @Override
    public void updateArray(String columnLabel, Array x) throws SQLException {
        this.wrappedResultSet.updateArray(columnLabel, x);
    }

    @Override
    public RowId getRowId(int columnIndex) throws SQLException {
        return this.wrappedResultSet.getRowId(columnIndex);
    }

    @Override
    public RowId getRowId(String columnLabel) throws SQLException {
        return this.wrappedResultSet.getRowId(columnLabel);
    }

    @Override
    public void updateRowId(int columnIndex, RowId x) throws SQLException {
        this.wrappedResultSet.updateRowId(columnIndex, x);
    }

    @Override
    public void updateRowId(String columnLabel, RowId x) throws SQLException {
        this.wrappedResultSet.updateRowId(columnLabel, x);
    }

    @Override
    public int getHoldability() throws SQLException {
        return this.wrappedResultSet.getHoldability();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return this.wrappedResultSet.isClosed();
    }

    @Override
    public void updateNString(int columnIndex, String nString) throws SQLException {
        this.wrappedResultSet.updateNString(columnIndex, nString);
    }

    @Override
    public void updateNString(String columnLabel, String nString) throws SQLException {
        this.wrappedResultSet.updateNString(columnLabel, nString);
    }

    @Override
    public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
        this.wrappedResultSet.updateNClob(columnIndex, nClob);
    }

    @Override
    public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
        this.wrappedResultSet.updateNClob(columnLabel, nClob);
    }

    @Override
    public NClob getNClob(int columnIndex) throws SQLException {
        return this.wrappedResultSet.getNClob(columnIndex);
    }

    @Override
    public NClob getNClob(String columnLabel) throws SQLException {
        return this.wrappedResultSet.getNClob(columnLabel);
    }

    @Override
    public SQLXML getSQLXML(int columnIndex) throws SQLException {
        return this.wrappedResultSet.getSQLXML(columnIndex);
    }

    @Override
    public SQLXML getSQLXML(String columnLabel) throws SQLException {
        return this.wrappedResultSet.getSQLXML(columnLabel);
    }

    @Override
    public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
        this.wrappedResultSet.updateSQLXML(columnIndex, xmlObject);
    }

    @Override
    public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
        this.wrappedResultSet.updateSQLXML(columnLabel, xmlObject);
    }

    @Override
    public String getNString(int columnIndex) throws SQLException {
        return this.wrappedResultSet.getNString(columnIndex);
    }

    @Override
    public String getNString(String columnLabel) throws SQLException {
        return this.wrappedResultSet.getNString(columnLabel);
    }

    @Override
    public Reader getNCharacterStream(int columnIndex) throws SQLException {
        return this.wrappedResultSet.getNCharacterStream(columnIndex);
    }

    @Override
    public Reader getNCharacterStream(String columnLabel) throws SQLException {
        return this.wrappedResultSet.getNCharacterStream(columnLabel);
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        this.wrappedResultSet.updateNCharacterStream(columnIndex, x, length);
    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        this.wrappedResultSet.updateNCharacterStream(columnLabel, reader, length);
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
        this.wrappedResultSet.updateAsciiStream(columnIndex, x, length);
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
        this.wrappedResultSet.updateBinaryStream(columnIndex, x, length);
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        this.wrappedResultSet.updateCharacterStream(columnIndex, x, length);
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
        this.wrappedResultSet.updateAsciiStream(columnLabel, x, length);
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
        this.wrappedResultSet.updateBinaryStream(columnLabel, x, length);
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        this.wrappedResultSet.updateCharacterStream(columnLabel, reader, length);
    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
        this.wrappedResultSet.updateBlob(columnIndex, inputStream, length);
    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
        this.wrappedResultSet.updateBlob(columnLabel, inputStream, length);
    }

    @Override
    public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
        this.wrappedResultSet.updateClob(columnIndex, reader, length);
    }

    @Override
    public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
        this.wrappedResultSet.updateClob(columnLabel, reader, length);
    }

    @Override
    public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
        this.wrappedResultSet.updateNClob(columnIndex, reader, length);
    }

    @Override
    public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
        this.wrappedResultSet.updateNClob(columnLabel, reader, length);
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
        this.wrappedResultSet.updateNCharacterStream(columnIndex, x);
    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
        this.wrappedResultSet.updateNCharacterStream(columnLabel, reader);
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
        this.wrappedResultSet.updateAsciiStream(columnIndex, x);
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
        this.wrappedResultSet.updateBinaryStream(columnIndex, x);
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
        this.wrappedResultSet.updateCharacterStream(columnIndex, x);
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
        this.wrappedResultSet.updateAsciiStream(columnLabel, x);
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
        this.wrappedResultSet.updateBinaryStream(columnLabel, x);
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
        this.wrappedResultSet.updateCharacterStream(columnLabel, reader);
    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
        this.wrappedResultSet.updateBlob(columnIndex, inputStream);
    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
        this.wrappedResultSet.updateBlob(columnLabel, inputStream);
    }

    @Override
    public void updateClob(int columnIndex, Reader reader) throws SQLException {
        this.wrappedResultSet.updateClob(columnIndex, reader);
    }

    @Override
    public void updateClob(String columnLabel, Reader reader) throws SQLException {
        this.wrappedResultSet.updateClob(columnLabel, reader);
    }

    @Override
    public void updateNClob(int columnIndex, Reader reader) throws SQLException {
        this.wrappedResultSet.updateNClob(columnIndex, reader);
    }

    @Override
    public void updateNClob(String columnLabel, Reader reader) throws SQLException {
        this.wrappedResultSet.updateNClob(columnLabel, reader);
    }

    @Override
    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        return this.wrappedResultSet.getObject(columnIndex, type);
    }

    @Override
    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        return this.wrappedResultSet.getObject(columnLabel, type);
    }

    @Override
    public Instant getInstant(int columnIndex) throws SQLException {
        Timestamp ts = getTimestamp(columnIndex, JdbcUtils.UTC_CALENDAR);
        return ts != null ? Instant.ofEpochMilli(ts.getTime()) : null;
    }

    @Override
    public Instant getInstant(String columnLabel) throws SQLException {
        Timestamp ts = getTimestamp(columnLabel, JdbcUtils.UTC_CALENDAR);
        return ts != null ? Instant.ofEpochMilli(ts.getTime()) : null;
    }

}
