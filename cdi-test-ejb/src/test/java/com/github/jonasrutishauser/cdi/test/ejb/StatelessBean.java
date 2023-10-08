package com.github.jonasrutishauser.cdi.test.ejb;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class StatelessBean {

    @EJB
    private SingletonBean singleton;

    public boolean isStarted() {
        return singleton.isStarted();
    }

}
