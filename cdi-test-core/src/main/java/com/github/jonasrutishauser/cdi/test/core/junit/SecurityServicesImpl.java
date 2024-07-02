package com.github.jonasrutishauser.cdi.test.core.junit;

import java.security.Principal;

import org.jboss.weld.security.spi.SecurityServices;

import com.github.jonasrutishauser.cdi.test.api.TestInfo;

public class SecurityServicesImpl implements SecurityServices {

    private TestInfo testInfo;

    public void setTestInfo(TestInfo testInfo) {
        this.testInfo = testInfo;
    }

    @Override
    public Principal getPrincipal() {
        return new Principal() {
            @Override
            public String getName() {
                if (testInfo == null) {
                    return "test-principal";
                }
                return testInfo.getTestName();
            }
        };
    }

    @Override
    public void cleanup() {
        // nothing to do
    }
}
