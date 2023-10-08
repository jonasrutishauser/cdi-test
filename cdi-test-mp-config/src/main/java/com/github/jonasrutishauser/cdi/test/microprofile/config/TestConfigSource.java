package com.github.jonasrutishauser.cdi.test.microprofile.config;

import java.util.Map;
import java.util.Set;

import javax.enterprise.inject.spi.CDI;

import org.eclipse.microprofile.config.spi.ConfigSource;

public class TestConfigSource implements ConfigSource {

    private final TestProperties testProperties = CDI.current().select(TestProperties.class).get();

    @Override
    public Map<String, String> getProperties() {
        return testProperties.getProperties();
    }

    @Override
    public Set<String> getPropertyNames() {
        return getProperties().keySet();
    }

    @Override
    public String getValue(String propertyName) {
        if (!propertyName.startsWith("smallrye.") && !propertyName.startsWith("mp.config.")
                && !testProperties.isInitialized()) {
            // avoid property validation warnings
            // (may result in runtime errors, but that is ok for tests)
            return "0";
        }
        return getProperties().get(propertyName);
    }

    @Override
    public int getOrdinal() {
        return 5000;
    }

    @Override
    public String getName() {
        return "cdi-test-configuration";
    }

}
