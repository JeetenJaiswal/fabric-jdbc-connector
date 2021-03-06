/*******************************************************************************
* * Copyright 2018 Impetus Infotech.
* *
* * Licensed under the Apache License, Version 2.0 (the "License");
* * you may not use this file except in compliance with the License.
* * You may obtain a copy of the License at
* *
* * http://www.apache.org/licenses/LICENSE-2.0
* *
* * Unless required by applicable law or agreed to in writing, software
* * distributed under the License is distributed on an "AS IS" BASIS,
* * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* * See the License for the specific language governing permissions and
* * limitations under the License.
******************************************************************************/
package com.impetus.fabric.jdbc;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Array;
import java.sql.Date;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;

import com.impetus.blkch.BlkchnException;
import com.impetus.blkch.jdbc.AbstractResultSet;
import com.impetus.blkch.jdbc.BlkchnArray;
import com.impetus.blkch.sql.DataFrame;

public class FabricResultSet extends AbstractResultSet {

    private Statement statement;

    private Object[] recordData;

    private DataFrame dataframe;
    
    private String tableName;

    private int recIdx;

    private boolean closed;

    private static final int BEFORE_FIRST = -1;

    FabricResultSet(Statement statement, DataFrame dataframe, String tableName) {
        this.statement = statement;
        this.dataframe = dataframe;
        this.tableName = tableName;
        this.recIdx = BEFORE_FIRST;
        this.closed = false;
    }

    public void close() throws SQLException {
        if (!isClosed()) {
            closed = true;
            statement = null;
        }

    }

    public int findColumn(String column) throws SQLException {
        for (int i = 0; i < dataframe.getColumns().size(); i++) {
            if (dataframe.getColumns().get(i).equalsIgnoreCase(column)) {
                return i + 1;
            }
        }
        throw new SQLException(String.format("Result set data doesn't contain column '%s'", column));
    }

    public BigDecimal getBigDecimal(int index) throws SQLException {
        if (index > recordData.length) {
            throw new SQLException(String.format("Result set doesn't contain index %d", index));
        }
        return recordData[index - 1] == null ? new BigDecimal(0.0) : new BigDecimal(recordData[index - 1].toString());
    }

    public BigDecimal getBigDecimal(String column) throws SQLException {
        return getBigDecimal(getColumnIndex(column));
    }

    public BigDecimal getBigDecimal(int index, int scale) throws SQLException {
        return getBigDecimal(index).setScale(scale, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getBigDecimal(String column, int scale) throws SQLException {
        return getBigDecimal(getColumnIndex(column), scale);
    }

    public boolean getBoolean(int index) throws SQLException {
        if (index > recordData.length) {
            throw new SQLException(String.format("Result set doesn't contain index %d", index));
        }
        return recordData[index - 1] == null ? false : Boolean.parseBoolean(recordData[index - 1].toString());
    }

    public boolean getBoolean(String column) throws SQLException {
        return getBoolean(getColumnIndex(column));
    }

    public byte getByte(int index) throws SQLException {
        if (index > recordData.length) {
            throw new SQLException(String.format("Result set doesn't contain index %d", index));
        }
        return recordData[index - 1] == null ? 0 : Byte.parseByte(recordData[index - 1].toString());
    }

    public byte getByte(String column) throws SQLException {
        return getByte(getColumnIndex(column));
    }

    public int getConcurrency() throws SQLException {
        return statement.getResultSetConcurrency();
    }

    public Date getDate(int index) throws SQLException {
        if (index > recordData.length) {
            throw new SQLException(String.format("Result set doesn't contain index %d", index));
        }
        DateFormat format = DateFormat.getDateInstance();
        try {
            return recordData[index - 1] == null ? null : new java.sql.Date(format.parse(
                    recordData[index - 1].toString()).getTime());
        } catch (ParseException e) {
            throw new SQLException(e.getMessage());
        }
    }

    public Date getDate(String column) throws SQLException {
        return getDate(getColumnIndex(column));
    }
    
    @Override
    public Timestamp getTimestamp(int index) throws SQLException {
        if (index > recordData.length) {
            throw new SQLException(String.format("Result set doesn't contain index %d", index));
        }
        return recordData[index - 1] == null ? null : (Timestamp)recordData[index - 1];
    }
    
    @Override
    public Timestamp getTimestamp(String column) throws SQLException {
        return getTimestamp(getColumnIndex(column));
    }

    public double getDouble(int index) throws SQLException {
        if (index > recordData.length) {
            throw new SQLException(String.format("Result set doesn't contain index %d", index));
        }
        return recordData[index - 1] == null ? 0.0 : Double.parseDouble(recordData[index - 1].toString());
    }

    public double getDouble(String column) throws SQLException {
        return getDouble(getColumnIndex(column));
    }

    public float getFloat(int index) throws SQLException {
        if (index > recordData.length) {
            throw new SQLException(String.format("Result set doesn't contain index %d", index));
        }
        return recordData[index - 1] == null ? 0.0f : Float.parseFloat(recordData[index - 1].toString());
    }

    public float getFloat(String column) throws SQLException {
        return getFloat(getColumnIndex(column));
    }

    public int getHoldability() throws SQLException {
        return statement.getResultSetHoldability();
    }

    public int getInt(int index) throws SQLException {
        if (index > recordData.length) {
            throw new SQLException(String.format("Result set doesn't contain index %d", index));
        }
        return recordData[index - 1] == null ? 0 : Integer.parseInt(recordData[index - 1].toString());
    }

    public int getInt(String column) throws SQLException {
        return getInt(getColumnIndex(column));
    }

    public long getLong(int index) throws SQLException {
        if (index > recordData.length) {
            throw new SQLException(String.format("Result set doesn't contain index %d", index));
        }
        return recordData[index - 1] == null ? 0 : Long.parseLong(recordData[index - 1].toString());
    }

    public long getLong(String column) throws SQLException {
        return getLong(getColumnIndex(column));
    }

    public Object getObject(int index) throws SQLException {
        if (index > recordData.length) {
            throw new SQLException(String.format("Result set doesn't contain index %d", index));
        }
        return recordData[index - 1];
    }

    public Object getObject(String column) throws SQLException {
        return getObject(getColumnIndex(column));
    }

    public short getShort(int index) throws SQLException {
        if (index > recordData.length) {
            throw new SQLException(String.format("Result set doesn't contain index %d", index));
        }
        return recordData[index - 1] == null ? 0 : Short.parseShort(recordData[index - 1].toString());
    }

    public short getShort(String column) throws SQLException {
        return getShort(getColumnIndex(column));
    }

    public Statement getStatement() throws SQLException {
        return statement;
    }

    public String getString(int index) throws SQLException {
        if (index > recordData.length) {
            throw new SQLException(String.format("Result set doesn't contain index %d", index));
        }
        return recordData[index - 1] == null ? null : recordData[index - 1].toString();
    }

    public String getString(String column) throws SQLException {
        return getString(getColumnIndex(column));
    }
    
    @Override
    public Array getArray(int index) throws SQLException {
        if (index > recordData.length) {
            throw new SQLException(String.format("Result set doesn't contain index %d", index));
        }
        return (BlkchnArray) recordData[index - 1];
    }
    
    @Override
    public Array getArray(String column) throws SQLException {
        return getArray(getColumnIndex(column));
    }

    public int getType() throws SQLException {
        return statement.getResultSetType();
    }

    public URL getURL(int index) throws SQLException {
        String url = getString(index);
        try {
            return url == null ? null : new java.net.URL(url);
        } catch (MalformedURLException e) {
            throw new SQLException(e.getMessage());
        }
    }

    public URL getURL(String column) throws SQLException {
        return getURL(getColumnIndex(column));
    }

    public boolean isClosed() throws SQLException {
        return closed;
    }

    public boolean isFirst() throws SQLException {
        return recIdx == 0;
    }

    public boolean isLast() throws SQLException {
        return recIdx == dataframe.getData().size() - 1;
    }

    public boolean next() throws SQLException {
        if (++recIdx >= dataframe.getData().size()) {
            return false;
        }
        recordData = dataframe.getData().get(recIdx).toArray();
        return true;
    }
    
    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return new FabricResultSetMetaData(tableName, dataframe.getColumnNamesMap(), dataframe.getAliasMapping());
    }
    
    private int getColumnIndex(String columnLabel) {
        if(!dataframe.getAliasMapping().isEmpty() && dataframe.getAliasMapping().containsKey(columnLabel)) {
            return dataframe.getColumnNamesMap().get(dataframe.getAliasMapping().get(columnLabel)) + 1;
        } else {
            if(dataframe.getColumnNamesMap().get(columnLabel) == null) {
                throw new BlkchnException(String.format("Result set data doesn't contain column '%s'", columnLabel));
            }
            return dataframe.getColumnNamesMap().get(columnLabel) + 1;
        }
    }

}
