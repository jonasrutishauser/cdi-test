package com.github.jonasrutishauser.cdi.test.jndi;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.sql.DataSource;

import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;

@ApplicationScoped
public class ApplicationScopedBean {

    @Resource(lookup = "ds/global")
    DataSource globalDataSource;

    void eagerInit(@Observes @Initialized(ApplicationScoped.class) Object event) {
        assertNotNull(globalDataSource);
    }
    
    public DataSource getGlobalDataSource() {
        return globalDataSource;
    }

}
