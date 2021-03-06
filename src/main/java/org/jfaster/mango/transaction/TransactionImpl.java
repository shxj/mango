/*
 * Copyright 2014 mango.jfaster.org
 *
 * The Mango Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package org.jfaster.mango.transaction;

import org.jfaster.mango.exception.TransactionSystemException;
import org.jfaster.mango.datasource.DataSourceUtils;
import org.jfaster.mango.util.logging.InternalLogger;
import org.jfaster.mango.util.logging.InternalLoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author ash
 */
public class TransactionImpl implements Transaction {

    private final static InternalLogger logger = InternalLoggerFactory.getInstance(TransactionFactory.class);

    private TransactionContext transactionContext;

    private TransactionState state = TransactionState.RUNNING;

    public TransactionImpl(TransactionContext transactionContext) {
        this.transactionContext = transactionContext;
    }

    @Override
    public void commit() {
        if (state != TransactionState.RUNNING) {
            throw new TransactionSystemException("transaction has commit or rollback");
        }
        TransactionContext tc = TransactionSynchronizationManager.getTransactionContext();
        if (tc == null) {
            throw new TransactionSystemException("no transaction context");
        }

        Connection conn = transactionContext.getConnection();

        if (conn == null) { // 开启了事务，但是没有获得conn
            TransactionSynchronizationManager.clear();
            state = TransactionState.COMMIT_SUCCESS;
            return;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Committing JDBC transaction");
        }
        try {
            conn.commit();
        } catch (SQLException e) { // commit出现异常，交由rollback回收connection
            state = TransactionState.COMMIT_FAIL;
            new TransactionSystemException("Could not commit JDBC transaction");
        }

        TransactionSynchronizationManager.clear();
        DataSource ds = transactionContext.getDataSource();
        DataSourceUtils.resetConnectionAfterTransaction(conn, ds, tc.getPreviousLevel());
        DataSourceUtils.releaseConnection(conn);
        state = TransactionState.COMMIT_SUCCESS;
    }

    @Override
    public void rollback() {
        if (state != TransactionState.RUNNING && state != TransactionState.COMMIT_FAIL) {
            throw new TransactionSystemException("transaction has rollback or commit");
        }
        TransactionContext tc = TransactionSynchronizationManager.getTransactionContext();
        if (tc == null) {
            throw new TransactionSystemException("no transaction context");
        }

        Connection conn = transactionContext.getConnection();

        if (conn == null) { // 开启了事务，但是没有获得conn
            TransactionSynchronizationManager.clear();
            state = TransactionState.ROLLBACK_SUCCESS;
            return;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Rolling back JDBC transaction");
        }
        try {
            conn.rollback();
            state = TransactionState.ROLLBACK_SUCCESS;
        } catch (SQLException e) {
            state = TransactionState.ROLLBACK_FAIL;
            new TransactionSystemException("Could not roll back JDBC transaction");
        } finally {
            TransactionSynchronizationManager.clear();
            DataSource ds = transactionContext.getDataSource();
            DataSourceUtils.resetConnectionAfterTransaction(conn, ds, tc.getPreviousLevel());
            DataSourceUtils.releaseConnection(conn);
        }
    }

    public TransactionState getState() {
        return state;
    }
}
