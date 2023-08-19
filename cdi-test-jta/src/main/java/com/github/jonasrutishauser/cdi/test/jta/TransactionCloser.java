package com.github.jonasrutishauser.cdi.test.jta;

import org.jboss.logging.Logger;

import com.github.jonasrutishauser.cdi.test.api.context.TestScoped;

import jakarta.enterprise.context.BeforeDestroyed;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Status;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;

@TestScoped
class TransactionCloser {
    private static final Logger LOGGER = Logger.getLogger(TransactionCloser.class);

    static void closeOpenTransactions(@Observes @BeforeDestroyed(TestScoped.class) Object event,
            UserTransaction userTransaction) {
        try {
            while (userTransaction.getStatus() != Status.STATUS_NO_TRANSACTION) {
                userTransaction.rollback();
            }
        } catch (SystemException e) {
            LOGGER.warn(e);
        }
    }
}
