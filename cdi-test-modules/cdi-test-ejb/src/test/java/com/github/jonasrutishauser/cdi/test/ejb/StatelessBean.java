package com.github.jonasrutishauser.cdi.test.ejb;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

@Stateless
public class StatelessBean {

    @EJB
    private SingletonBean singleton;

    public boolean isStarted() {
        return singleton.isStarted();
    }

}
