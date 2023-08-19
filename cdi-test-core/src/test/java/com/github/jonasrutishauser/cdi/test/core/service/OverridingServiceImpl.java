package com.github.jonasrutishauser.cdi.test.core.service;

import com.github.jonasrutishauser.cdi.test.api.annotations.GlobalTestImplementation;

@GlobalTestImplementation
public class OverridingServiceImpl implements OverriddenService {
    @Override
    public String serviceMethod() {
        return "OverridingServiceImpl";
    }
}
