package com.github.jonasrutishauser.cdi.test.jpa;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class UserService {
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void storeUser(UserEntity userEntity) {
        entityManager.persist(userEntity);
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void storeUserInNewTransaction(UserEntity userEntity) {
        entityManager.persist(userEntity);
    }

    @Transactional
    public UserEntity loadUser(long id) {
        return entityManager.find(UserEntity.class, id);
    }

}
