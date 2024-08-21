package com.github.jonasrutishauser.cdi.test.jndi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.h2.Driver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.github.jonasrutishauser.cdi.test.core.junit.CdiTestJunitExtension;

import jakarta.annotation.Resource;
import jakarta.inject.Inject;

@ExtendWith(CdiTestJunitExtension.class)
@EnvEntry(name = "env/string", value = "foo-bar")
@EnvEntry(name = "env/int", type = Integer.class, value = "42")
@EnvEntry(name = "string", value = "test", compEnv = true)
@EnvEntry(name = "com.github.jonasrutishauser.cdi.test.jndi.ResourceInjectionTest/defaultName", value = "foo",
        compEnv = true)
@DataSourceEntry(name = "ds/test", driver = Driver.class, url = "jdbc:h2:mem:test", user = "sa", password = "sa")
class ResourceInjectionTest {

    @Resource(lookup = "env/string")
    String stringProperty;

    @Resource(lookup = "env/int")
    int intProperty;

    @Resource(name = "string")
    String compEnvStringProperty;

    @Resource
    String defaultName;

    @Resource(lookup = "ds/test")
    DataSource dataSource;

    @Resource(lookup = "ds/global")
    DataSource globalDataSource;

    @Resource(lookup = "ds/jar-global")
    DataSource jarGlobalDataSource;

    @Inject
    ApplicationScopedBean applicationScopedBean;

    @BeforeAll
    static void setDummyEntries() throws NamingException, ClassNotFoundException {
        Class.forName(TestResourceService.class.getName()); // call static initializer
        Context context = new InitialContext();
        context = context.createSubcontext("env");
        context.bind("string", "some-wrong-string");
    }

    @AfterAll
    static void ensureRestoredEntries() throws NamingException {
        Context context = new InitialContext();
        assertEquals("some-wrong-string", context.lookup("env/string"));
    }

    @Test
    void injection() {
        assertEquals("foo-bar", stringProperty);
        assertEquals(42, intProperty);
        assertEquals("test", compEnvStringProperty);
        assertEquals("foo", defaultName);
        assertNotNull(dataSource);
        assertNotNull(globalDataSource);
        assertNotNull(jarGlobalDataSource);
        assertNotNull(applicationScopedBean);
    }

    @Test
    @EnvEntry(name = "env/int", type = Integer.class, value = "13")
    void methodOverride() {
        assertEquals(13, intProperty);
    }

    @Test
    void dataSource() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            assertNotNull(connection);
        }
        try (Connection connection = globalDataSource.getConnection()) {
            assertNotNull(connection);
        }
    }

    @Test
    void applicationScopedDataSource() {
        assertEquals(globalDataSource, applicationScopedBean.getGlobalDataSource());
    }

}
