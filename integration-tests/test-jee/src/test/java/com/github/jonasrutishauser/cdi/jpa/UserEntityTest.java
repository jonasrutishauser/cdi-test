package com.github.jonasrutishauser.cdi.jpa;

import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.github.jonasrutishauser.cdi.test.core.junit.CdiTestJunitExtension;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.PersistenceProperty;
import jakarta.transaction.UserTransaction;

@ExtendWith(CdiTestJunitExtension.class)
class UserEntityTest {
    @PersistenceContext(unitName = "test-jee-unit", properties = {@PersistenceProperty(name = "hibernate.hbm2ddl.auto", value = "update")})
    private EntityManager entityManager;
    @Inject
    private EntitySupport testUtils;
    @Inject
    private InvocationCounter counter;
    @Inject
    private UserTransaction userTransaction;

    @Test
    void storeUserEntityWithNullAttributes() {
        UserEntity userEntity = new UserEntity();
        Assertions.assertThrows(PersistenceException.class, () -> entityManager.persist(userEntity));
    }

    @Test
    void storeUserEntity() {
        entityManager.persist(testUtils.createGunnar());
        Assertions.assertEquals(1, counter.get());
    }

    @Test
    void storeAndRemoveUserEntity() {
        UserEntity gunnar = testUtils.createGunnar();
        entityManager.persist(gunnar);
        entityManager.remove(gunnar);
        Assertions.assertEquals(2, counter.get());
    }

    @Test
    void storeUserRollback() throws Exception {
        userTransaction.begin();
        UserEntity userEntity = testUtils.createGunnar();
        entityManager.persist(userEntity);
        userTransaction.rollback();

        long id = userEntity.getId();
        assertNull(entityManager.find(UserEntity.class, id));
    }
}
