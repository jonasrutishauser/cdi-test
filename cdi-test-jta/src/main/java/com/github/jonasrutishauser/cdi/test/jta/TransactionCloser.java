package com.github.jonasrutishauser.cdi.test.jta;

import javax.enterprise.context.BeforeDestroyed;
import javax.enterprise.event.Observes;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.jboss.logging.Logger;

import com.github.jonasrutishauser.cdi.test.api.context.TestScoped;

@TestScoped
class TransactionCloser {
    private static final Logger LOGGER = Logger.getLogger(TransactionCloser.class);

    private TransactionCloser() {
    }

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
