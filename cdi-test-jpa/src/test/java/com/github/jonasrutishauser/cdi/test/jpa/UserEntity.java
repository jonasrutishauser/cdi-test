package com.github.jonasrutishauser.cdi.test.jpa;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;

@Entity
@EntityListeners(TestEntityListener.class)
@NamedQuery(name = "findAllUserEntity", query = "SELECT u from UserEntity u")
public class UserEntity {

    public long getId() {
        return id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
}
