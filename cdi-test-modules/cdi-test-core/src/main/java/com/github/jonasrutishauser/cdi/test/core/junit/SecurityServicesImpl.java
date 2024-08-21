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
        return new TestPrincipal(testInfo == null ? "test-principal" : testInfo.getTestName());
    }

    @Override
    public void cleanup() {
        // nothing to do
    }

    private static final class TestPrincipal implements Principal {
        private final String name;

        public TestPrincipal(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
