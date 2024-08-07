package com.github.jonasrutishauser.cdi.test.core.service;

import com.github.jonasrutishauser.cdi.test.api.annotations.ActivatableTestImplementation;

@ActivatableTestImplementation(OverridingServiceImpl.class)
public class TestActivatedOverridenService implements OverriddenService {

    private int invocationCounter = 0;

    public int getInvocationCounter() {
        return invocationCounter;
    }

    @Override
    public String serviceMethod() {
        invocationCounter++;
        return "TestActivatedOverridenService";
    }
}
