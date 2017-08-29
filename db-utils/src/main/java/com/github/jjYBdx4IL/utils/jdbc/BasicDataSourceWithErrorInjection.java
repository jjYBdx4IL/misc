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
import java.sql.Connection;
import java.sql.SQLException;
import org.apache.commons.dbcp.BasicDataSource;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class BasicDataSourceWithErrorInjection extends BasicDataSource {

    private boolean failBeforeBegin = false;
    private boolean failBeforeCommit = false;
    private boolean failBeforeRollback = false;
    private boolean failAfterBegin = false;
    private boolean failAfterCommit = false;
    private boolean failAfterRollback = false;
    private boolean failOnceOnly = false;

    @Override
    public Connection getConnection() throws SQLException {
        ConnectionWithErrorInjection conn = new ConnectionWithErrorInjection(super.getConnection());
        applyErrorInjectionParams(conn);
        return conn;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        ConnectionWithErrorInjection conn = new ConnectionWithErrorInjection(super.getConnection(url, password));
        applyErrorInjectionParams(conn);
        return conn;
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

    private void applyErrorInjectionParams(ConnectionWithErrorInjection conn) {
        conn.setFailAfterBegin(isFailAfterBegin());
        conn.setFailAfterCommit(isFailAfterCommit());
        conn.setFailAfterRollback(isFailAfterRollback());
        conn.setFailBeforeBegin(isFailBeforeBegin());
        conn.setFailBeforeCommit(isFailBeforeCommit());
        conn.setFailBeforeRollback(isFailBeforeRollback());
        conn.setFailOnceOnly(isFailOnceOnly());
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
}
