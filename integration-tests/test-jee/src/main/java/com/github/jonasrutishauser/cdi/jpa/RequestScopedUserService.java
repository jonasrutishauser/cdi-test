package com.github.jonasrutishauser.cdi.jpa;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RequestScoped
public class RequestScopedUserService {

    @PersistenceContext(unitName = "test-jee-unit")
    private EntityManager entityManager;

    public long addUser(UserEntity userEntity) {
        entityManager.persist(userEntity);
        return userEntity.getId();
    }
}
