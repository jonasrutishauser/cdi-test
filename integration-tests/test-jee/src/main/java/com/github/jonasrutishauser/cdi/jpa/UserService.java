package com.github.jonasrutishauser.cdi.jpa;

import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Startup
@Singleton
public class UserService {

    @PersistenceContext(unitName = "test-jee-unit")
    private EntityManager entityManager;

    public long addUser(UserEntity userEntity) {
        entityManager.persist(userEntity);
        return userEntity.getId();
    }
}
