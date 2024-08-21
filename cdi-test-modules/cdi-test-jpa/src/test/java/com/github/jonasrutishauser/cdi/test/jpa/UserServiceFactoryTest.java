package com.github.jonasrutishauser.cdi.test.jpa;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.h2.Driver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.github.jonasrutishauser.cdi.test.core.junit.CdiTestJunitExtension;
import com.github.jonasrutishauser.cdi.test.jndi.DataSourceEntry;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import jakarta.transaction.UserTransaction;

@ExtendWith(CdiTestJunitExtension.class)
@DataSourceEntry(name = "jdbc/cdi-test", driver = Driver.class, url = "jdbc:h2:mem:test", user = "sa", password = "sa")
class UserServiceFactoryTest {
    @Inject
    private UserService userService;

    @PersistenceUnit
    private EntityManagerFactory entityManager;

    @Inject
    private UserTransaction userTransaction;

    @Test
    void assertPersistenceContextInjected() {
        assertNotNull(entityManager);
    }

    @Test
    void assertUserTransactionInjected() {
        assertNotNull(userTransaction);
    }

    @Test
    void storeUser() throws Exception {
        userTransaction.begin();
        UserEntity userEntity = new UserEntity();
        UserEntity userEntityTwo = new UserEntity();
        userService.storeUser(userEntity);
        userService.storeUser(userEntityTwo);
        userTransaction.commit();

        long id = userEntityTwo.getId();
        assertNotNull(userService.loadUser(id));
    }

    @Test
    void storeUserRollback() throws Exception {
        userTransaction.begin();
        UserEntity userEntity = new UserEntity();
        userService.storeUser(userEntity);
        userTransaction.rollback();

        long id = userEntity.getId();
        assertNull(userService.loadUser(id));
    }

    @Test
    void storeUserNewTransaction() throws Exception {
        userTransaction.begin();
        UserEntity userEntity = new UserEntity();
        userService.storeUserInNewTransaction(userEntity);
        userTransaction.rollback();

        long id = userEntity.getId();
        assertNotNull(userService.loadUser(id));
    }

    @Test
    void storeUserNewTransactionDontRollback() {
        UserEntity userEntity = new UserEntity();
        userService.storeUserInNewTransaction(userEntity);

        long id = userEntity.getId();
        assertNotNull(userService.loadUser(id));
    }

}
