package com.github.jonasrutishauser.cdi.jpa;

import java.time.LocalDate;
import java.time.Month;

import javax.enterprise.context.Dependent;

@Dependent
public class EntitySupport {

    public UserEntity createGunnar() {
        UserEntity userEntity = new UserEntity();
        userEntity.setFirstName("Gunnar");
        userEntity.setBirthDate(LocalDate.of(1971, Month.JUNE, 15));
        return userEntity;
    }
}
