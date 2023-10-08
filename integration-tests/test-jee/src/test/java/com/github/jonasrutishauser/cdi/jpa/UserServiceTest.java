package com.github.jonasrutishauser.cdi.jpa;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.github.jonasrutishauser.cdi.test.core.junit.CdiTestJunitExtension;

@ExtendWith(CdiTestJunitExtension.class)
class UserServiceTest {

    @Inject
    private UserService userService;
    @PersistenceContext(unitName = "test-jee-unit")
    private EntityManager entityManager;
    @Inject
    private EntitySupport testUtils;

    @Test
    void addUser() {
        long id = userService.addUser(testUtils.createGunnar());
        Assertions.assertNotNull(entityManager.find(UserEntity.class, id));
    }
}
