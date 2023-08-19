package com.github.jonasrutishauser.cdi.jpa;

import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@RequestScoped
public class RequestScopedUserService {

    @PersistenceContext(unitName = "test-jee-unit")
    private EntityManager entityManager;

    public long addUser(UserEntity userEntity) {
        entityManager.persist(userEntity);
        return userEntity.getId();
    }
}
