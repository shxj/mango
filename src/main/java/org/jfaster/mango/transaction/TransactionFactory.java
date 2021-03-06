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
import org.jfaster.mango.util.logging.InternalLogger;
import org.jfaster.mango.util.logging.InternalLoggerFactory;

/**
 * @author ash
 */
public abstract class TransactionFactory {

    private final static InternalLogger logger = InternalLoggerFactory.getInstance(TransactionFactory.class);

    public static Transaction newTransaction(TransactionIsolationLevel level) {
        if (level == null) {
            new IllegalArgumentException("TransactionIsolationLevel can't be null");
        }

        TransactionContext tc = TransactionSynchronizationManager.getTransactionContext();
        if (tc != null) {
            throw new TransactionSystemException("already exists transaction");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Creating new transaction");
        }

        tc = new TransactionContext(level);
        Transaction transaction = new TransactionImpl(tc);
        TransactionSynchronizationManager.setTransactionContext(tc);
        return transaction;
    }

    public static Transaction newTransaction() {
        return newTransaction(TransactionIsolationLevel.DEFAULT);
    }

}
