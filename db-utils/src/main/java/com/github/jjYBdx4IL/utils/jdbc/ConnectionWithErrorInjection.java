/*
 * Copyright © 2017 jjYBdx4IL (https://github.com/jjYBdx4IL)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jjYBdx4IL.utils.jdbc;

//CHECKSTYLE:OFF
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class ConnectionWithErrorInjection implements Connection {

    private static final Logger LOG = LoggerFactory.getLogger(ConnectionWithErrorInjection.class);

    private final Connection origConnection;
    private boolean failBeforeBegin = false;
    private boolean failBeforeCommit = false;
    private boolean failBeforeRollback = false;
    private boolean failAfterBegin = false;
    private boolean failAfterCommit = false;
    private boolean failAfterRollback = false;
    private int beginCounter = 0;
    private int commitCounter = 0;
    private int rollbackCounter = 0;
    private boolean failOnceOnly = false;

    public ConnectionWithErrorInjection(Connection origConnection) throws SQLException {
        if (!origConnection.getAutoCommit()) {
            throw new RuntimeException("connection initially in transaction mode");
        }
        this.origConnection = origConnection;
    }

    @Override
    public Statement createStatement() throws SQLException {
        return getOrigConnection().createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return getOrigConnection().prepareStatement(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return getOrigConnection().prepareCall(sql);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        return getOrigConnection().nativeSQL(sql);
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        if (!autoCommit) {
            beginCounter++;
            if (isFailBeforeBegin()) {
                if (isFailOnceOnly()) {
                    clearFailSettings();
                }
                LOG.warn("error injection: force fail before transaction start");
                throw new RuntimeException("error injection: force fail before transaction start");
            }
        }
        getOrigConnection().setAutoCommit(autoCommit);
        if (!autoCommit) {
            if (isFailAfterBegin()) {
                if (isFailOnceOnly()) {
                    clearFailSettings();
                }
                LOG.warn("error injection: force fail after transaction start");
                throw new RuntimeException("error injection: force fail after transaction start");
            }
        }
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return getOrigConnection().getAutoCommit();
    }

    @Override
    public void commit() throws SQLException {
        commitCounter++;
        if (isFailBeforeCommit()) {
            if (isFailOnceOnly()) {
                clearFailSettings();
            }
            LOG.warn("error injection: force fail before transaction commit");
            throw new RuntimeException("error injection: force fail before transaction commit");
        }
        getOrigConnection().commit();
        if (isFailAfterCommit()) {
            if (isFailOnceOnly()) {
                clearFailSettings();
            }
            LOG.warn("error injection: force fail after transaction commit");
            throw new RuntimeException("error injection: force fail after transaction commit");
        }
    }

    @Override
    public void rollback() throws SQLException {
        rollbackCounter++;
        if (isFailBeforeRollback()) {
            if (isFailOnceOnly()) {
                clearFailSettings();
            }
            LOG.warn("error injection: force fail before transaction rollback");
            throw new RuntimeException("error injection: force fail before transaction rollback");
        }
        getOrigConnection().rollback();
        if (isFailAfterRollback()) {
            if (isFailOnceOnly()) {
                clearFailSettings();
            }
            LOG.warn("error injection: force fail after transaction rollback");
            throw new RuntimeException("error injection: force fail after transaction rollback");
        }
    }

    @Override
    public void close() throws SQLException {
        getOrigConnection().close();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return getOrigConnection().isClosed();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return getOrigConnection().getMetaData();
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        getOrigConnection().setReadOnly(readOnly);
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return getOrigConnection().isReadOnly();
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        getOrigConnection().setCatalog(catalog);
    }

    @Override
    public String getCatalog() throws SQLException {
        return getOrigConnection().getCatalog();
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        getOrigConnection().setTransactionIsolation(level);
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return getOrigConnection().getTransactionIsolation();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return getOrigConnection().getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        getOrigConnection().clearWarnings();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return getOrigConnection().createStatement(resultSetType, resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return getOrigConnection().prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return getOrigConnection().prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return getOrigConnection().getTypeMap();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        getOrigConnection().setTypeMap(map);
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        getOrigConnection().setHoldability(holdability);
    }

    @Override
    public int getHoldability() throws SQLException {
        return getOrigConnection().getHoldability();
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        return getOrigConnection().setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        return getOrigConnection().setSavepoint(name);
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        getOrigConnection().rollback(savepoint);
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        getOrigConnection().releaseSavepoint(savepoint);
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return getOrigConnection().createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return getOrigConnection().prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return getOrigConnection().prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return getOrigConnection().prepareStatement(sql, autoGeneratedKeys);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return getOrigConnection().prepareStatement(sql, columnIndexes);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return getOrigConnection().prepareStatement(sql, columnNames);
    }

    @Override
    public Clob createClob() throws SQLException {
        return getOrigConnection().createClob();
    }

    @Override
    public Blob createBlob() throws SQLException {
        return getOrigConnection().createBlob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        return getOrigConnection().createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return getOrigConnection().createSQLXML();
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return getOrigConnection().isValid(timeout);
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        getOrigConnection().setClientInfo(name, value);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        getOrigConnection().setClientInfo(properties);
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return getOrigConnection().getClientInfo(name);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return getOrigConnection().getClientInfo();
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return getOrigConnection().createArrayOf(typeName, elements);
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return getOrigConnection().createStruct(typeName, attributes);
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        getOrigConnection().setSchema(schema);
    }

    @Override
    public String getSchema() throws SQLException {
        return getOrigConnection().getSchema();
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        getOrigConnection().abort(executor);
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        getOrigConnection().setNetworkTimeout(executor, milliseconds);
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return getOrigConnection().getNetworkTimeout();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return getOrigConnection().unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return getOrigConnection().isWrapperFor(iface);
    }

    /**
     * @return the origConnection
     */
    private Connection getOrigConnection() {
        return origConnection;
    }

    /**
     * @return the failBeforeCommit
     */
    public boolean isFailOnCommit() {
        return isFailBeforeCommit();
    }

    /**
     * @param failOnCommit the failBeforeCommit to set
     */
    public void setFailOnCommit(boolean failOnCommit) {
        this.setFailBeforeCommit(failOnCommit);
    }

    /**
     * @return the failBeforeBegin
     */
    public boolean isFailOnBegin() {
        return isFailBeforeBegin();
    }

    /**
     * @param failOnBegin the failBeforeBegin to set
     */
    public void setFailOnBegin(boolean failOnBegin) {
        this.setFailBeforeBegin(failOnBegin);
    }

    /**
     * @return the failBeforeRollback
     */
    public boolean isFailOnRollback() {
        return isFailBeforeRollback();
    }

    /**
     * @param failOnRollback the failBeforeRollback to set
     */
    public void setFailOnRollback(boolean failOnRollback) {
        this.setFailBeforeRollback(failOnRollback);
    }

    /**
     * @return the beginCounter
     */
    public int getBeginCounter() {
        return beginCounter;
    }

    /**
     * @param beginCounter the beginCounter to set
     */
    public void setBeginCounter(int beginCounter) {
        this.beginCounter = beginCounter;
    }

    /**
     * @return the rollbackCounter
     */
    public int getRollbackCounter() {
        return rollbackCounter;
    }

    /**
     * @param rollbackCounter the rollbackCounter to set
     */
    public void setRollbackCounter(int rollbackCounter) {
        this.rollbackCounter = rollbackCounter;
    }

    /**
     * @return the commitCounter
     */
    public int getCommitCounter() {
        return commitCounter;
    }

    /**
     * @param commitCounter the commitCounter to set
     */
    public void setCommitCounter(int commitCounter) {
        this.commitCounter = commitCounter;
    }

    /**
     * @return the failAfterBegin
     */
    public boolean isFailAfterBegin() {
        return failAfterBegin;
    }

    /**
     * @param failAfterBegin the failAfterBegin to set
     */
    public void setFailAfterBegin(boolean failAfterBegin) {
        this.failAfterBegin = failAfterBegin;
    }

    /**
     * @return the failAfterCommit
     */
    public boolean isFailAfterCommit() {
        return failAfterCommit;
    }

    /**
     * @param failAfterCommit the failAfterCommit to set
     */
    public void setFailAfterCommit(boolean failAfterCommit) {
        this.failAfterCommit = failAfterCommit;
    }

    /**
     * @return the failAfterRollback
     */
    public boolean isFailAfterRollback() {
        return failAfterRollback;
    }

    /**
     * @param failAfterRollback the failAfterRollback to set
     */
    public void setFailAfterRollback(boolean failAfterRollback) {
        this.failAfterRollback = failAfterRollback;
    }

    /**
     * @return the failBeforeBegin
     */
    public boolean isFailBeforeBegin() {
        return failBeforeBegin;
    }

    /**
     * @param failBeforeBegin the failBeforeBegin to set
     */
    public void setFailBeforeBegin(boolean failBeforeBegin) {
        this.failBeforeBegin = failBeforeBegin;
    }

    /**
     * @return the failBeforeCommit
     */
    public boolean isFailBeforeCommit() {
        return failBeforeCommit;
    }

    /**
     * @param failBeforeCommit the failBeforeCommit to set
     */
    public void setFailBeforeCommit(boolean failBeforeCommit) {
        this.failBeforeCommit = failBeforeCommit;
    }

    /**
     * @return the failBeforeRollback
     */
    public boolean isFailBeforeRollback() {
        return failBeforeRollback;
    }

    /**
     * @param failBeforeRollback the failBeforeRollback to set
     */
    public void setFailBeforeRollback(boolean failBeforeRollback) {
        this.failBeforeRollback = failBeforeRollback;
    }

    /**
     * @return the failOnceOnly
     */
    public boolean isFailOnceOnly() {
        return failOnceOnly;
    }

    /**
     * @param failOnceOnly the failOnceOnly to set
     */
    public void setFailOnceOnly(boolean failOnceOnly) {
        this.failOnceOnly = failOnceOnly;
    }

    public void clearFailSettings() {
        setFailAfterBegin(false);
        setFailAfterCommit(false);
        setFailAfterRollback(false);
        setFailBeforeBegin(false);
        setFailBeforeCommit(false);
        setFailBeforeRollback(false);
        setFailOnceOnly(false);
    }
}
