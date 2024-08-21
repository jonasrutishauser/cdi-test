package com.github.jonasrutishauser.cdi.test.jta;

import static jakarta.transaction.Transactional.TxType.REQUIRES_NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.github.jonasrutishauser.cdi.test.core.junit.CdiTestJunitExtension;

import jakarta.inject.Inject;
import jakarta.transaction.Status;
import jakarta.transaction.TransactionSynchronizationRegistry;
import jakarta.transaction.Transactional;
import jakarta.transaction.TransactionalException;
import jakarta.transaction.UserTransaction;

@ExtendWith(CdiTestJunitExtension.class)
class TransactionsTest {
    @Inject
    private ExecutionService executionService;

    @Inject
    private UserTransaction userTransaction;

    @Inject
    private TransactionSynchronizationRegistry transactionSynchronizationRegistry;
    
    @Test
    void required() throws Throwable {
        assertEquals(Status.STATUS_NO_TRANSACTION, userTransaction.getStatus());
        executionService.executeRequired(() -> assertEquals(Status.STATUS_ACTIVE, userTransaction.getStatus()));
        assertEquals(Status.STATUS_NO_TRANSACTION, userTransaction.getStatus());
    }
    
    @Test
    @Transactional
    void requiresNew() throws Throwable {
        assertEquals(Status.STATUS_ACTIVE, userTransaction.getStatus());
        Object nestedTxKey = executionService.executeRequiresNew(transactionSynchronizationRegistry::getTransactionKey);
        assertEquals(Status.STATUS_ACTIVE, userTransaction.getStatus());
        assertNotEquals(transactionSynchronizationRegistry.getTransactionKey(), nestedTxKey);
    }

    @Test
    @Transactional(REQUIRES_NEW)
    void notSupported() throws Throwable {
        assertEquals(Status.STATUS_ACTIVE, userTransaction.getStatus());
        executionService.executeNotSupported(() -> assertEquals(Status.STATUS_NO_TRANSACTION, userTransaction.getStatus()));
        assertEquals(Status.STATUS_ACTIVE, userTransaction.getStatus());
    }
    
    @Test
    void never() throws Throwable {
        assertEquals(Status.STATUS_NO_TRANSACTION, userTransaction.getStatus());
        executionService.executeTxNever(() -> assertEquals(Status.STATUS_NO_TRANSACTION, userTransaction.getStatus()));
        assertEquals(Status.STATUS_NO_TRANSACTION, userTransaction.getStatus());
        userTransaction.begin();
        assertThrows(TransactionalException.class, () -> executionService.executeTxNever(() -> {}));
    }
    
    @AfterEach
    void noTransaction() throws Throwable {
        assertEquals(Status.STATUS_NO_TRANSACTION, userTransaction.getStatus());
    }
}
