package com.github.jonasrutishauser.cdi.test.jta;

import java.util.function.Supplier;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.jboss.logging.Logger;
import org.jboss.weld.transaction.spi.TransactionServices;

import com.arjuna.ats.jta.TransactionManager;
import com.arjuna.ats.jta.common.jtaPropertyManager;

import jakarta.transaction.RollbackException;
import jakarta.transaction.Status;
import jakarta.transaction.Synchronization;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;

/**
 * SPI extension point of the Weld for integrate with transaction manager. If
 * the interface is implemented by the deployment the Weld stops to show info
 * message:
 * <p>
 * <code>
 *  WELD-000101: Transactional services not available. Injection of @Inject UserTransaction not available.
 *    Transactional observers will be invoked synchronously.
 * </code>
 * </p>
 */
public class TestTransactionServices implements TransactionServices {
    private static final Logger LOG = Logger.getLogger(TestTransactionServices.class);

    public TestTransactionServices() {
        try {
            registerInJndi(jtaPropertyManager.getJTAEnvironmentBean().getTransactionManagerJNDIContext(),
                    TransactionManager::transactionManager);
            registerInJndi(
                    jtaPropertyManager.getJTAEnvironmentBean().getTransactionSynchronizationRegistryJNDIContext(),
                    jtaPropertyManager.getJTAEnvironmentBean()::getTransactionSynchronizationRegistry);
        } catch (NamingException e) {
            // ignore
        }
    }

    private void registerInJndi(String jndiName, Supplier<?> supplier) throws NamingException {
        int lastSeparator = jndiName.lastIndexOf('/');
        Context context = new InitialContext();
        try {
            context.lookup(jndiName);
        } catch (NameNotFoundException e) {
            if (lastSeparator > 0) {
                ((Context) context.lookup(jndiName.substring(0, lastSeparator)))
                        .bind(jndiName.substring(lastSeparator + 1), supplier.get());
            } else {
                context.bind(jndiName, supplier.get());
            }
        } catch (NamingException e) {
            context.createSubcontext(jndiName.substring(0, lastSeparator)).bind(jndiName.substring(lastSeparator + 1),
                    supplier.get());
        } finally {
            context.close();
        }
    }

    @Override
    public void registerSynchronization(Synchronization synchronizedObserver) {
        try {
            TransactionManager.transactionManager().getTransaction().registerSynchronization(synchronizedObserver);
        } catch (SystemException | IllegalStateException | RollbackException e) {
            throw new IllegalStateException("Cannot register synchronization observer " + synchronizedObserver
                    + " to the available transaction", e);
        }
    }

    @Override
    public boolean isTransactionActive() {
        try {
            int status = TransactionManager.transactionManager().getStatus();
            return status == Status.STATUS_ACTIVE || status == Status.STATUS_COMMITTING
                    || status == Status.STATUS_MARKED_ROLLBACK || status == Status.STATUS_PREPARED
                    || status == Status.STATUS_PREPARING || status == Status.STATUS_ROLLING_BACK
                    || status == Status.STATUS_UNKNOWN;
        } catch (SystemException se) {
            LOG.error("Cannot obtain the status of the transaction", se);
            return false;
        }
    }

    @Override
    public UserTransaction getUserTransaction() {
        return com.arjuna.ats.jta.UserTransaction.userTransaction();
    }

    @Override
    public void cleanup() {
        // nothing to clean
    }
}
