package com.github.jonasrutishauser.cdi.test.jndi;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.stream.Stream;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.osjava.sj.loader.JndiLoader;

import com.github.jonasrutishauser.cdi.test.api.TestInfo;
import com.github.jonasrutishauser.cdi.test.api.context.TestScoped;

@TestScoped
class TestEnvEntries {

    private static final Map<String, Properties> GLOBAL_DATA_SOURCES = findResourcesProperties("datasource");

    private final Context rootContext = getRootContext();
    private final JndiLoader loader = createJndiLoader();
    private final Map<String, Object> oldValues = new HashMap<>();
    private final List<Connection> connections = new ArrayList<>();

    void registerEnvEntries(@Observes @Initialized(TestScoped.class) TestInfo testInfo) {
        for (Entry<String, Properties> dataSource : GLOBAL_DATA_SOURCES.entrySet()) {
            registerDataSource(dataSource.getKey(), dataSource.getValue());
        }
        for (EnvEntry envEntry : testInfo.getTestClass().getAnnotationsByType(EnvEntry.class)) {
            registerEnvEntry(envEntry);
        }
        for (EnvEntry envEntry : testInfo.getTestMethod().getAnnotationsByType(EnvEntry.class)) {
            registerEnvEntry(envEntry);
        }
        for (DataSourceEntry envEntry : testInfo.getTestClass().getAnnotationsByType(DataSourceEntry.class)) {
            registerDataSource(envEntry);
        }
    }

    @PreDestroy
    void unregisterEnvEntries() {
        for (Connection connection : connections) {
            try {
                connection.close();
            } catch (SQLException e) {
                // ignore
            }
        }
        for (Entry<String, Object> oldValue : oldValues.entrySet()) {
            try {
                unbind(oldValue.getKey());
                if (oldValue.getValue() != null) {
                    bind(oldValue.getKey(), oldValue.getValue());
                }
            } catch (NamingException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    private static Map<String, Properties> findResourcesProperties(String folder) {
        Map<String, Properties> properties = new HashMap<>();
        Enumeration<URL> folders;
        try {
            folders = TestEnvEntries.class.getClassLoader().getResources(folder);
        } catch (IOException e) {
            return Collections.emptyMap();
        }
        while (folders.hasMoreElements()) {
            URI uri;
            try {
                uri = folders.nextElement().toURI();
            } catch (URISyntaxException e) {
                throw new IllegalStateException(e);
            }
            if ("jar".equals(uri.getScheme())) {
                try (FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
                    properties.putAll(findResourcesProperties(fileSystem.provider().getPath(uri)));
                } catch (IOException e) {
                    // ignore
                }
            } else {
                properties.putAll(findResourcesProperties(Paths.get(uri)));
            }
        }
        return properties;
    }

    private static Map<String, Properties> findResourcesProperties(Path folder) {
        Map<String, Properties> properties = new HashMap<>();
        try (Stream<Path> walkStream = Files.walk(folder)) {
            walkStream.filter(p -> Files.isRegularFile(p) && p.toString().endsWith(".properties")).forEach(f -> {
                Properties props = new Properties();
                try (InputStream in = Files.newInputStream(f)) {
                    props.load(in);
                    properties.put(folder.relativize(f).toString().replaceAll("\\.properties$", ""), props);
                } catch (IOException e) {
                    // ignore
                }
            });
        } catch (IOException e1) {
            // ignore
        }
        return properties;
    }

    private void bind(String name, Object value) throws NamingException {
        int lastSeparator = name.lastIndexOf("/");
        if (lastSeparator > 0) {
            String subContextName = name.substring(0, lastSeparator);
            ((Context) rootContext.lookup(subContextName)).bind(name.substring(lastSeparator + 1), value);
        } else {
            rootContext.bind(name, value);
        }
    }

    private void unbind(String name) throws NamingException {
        int lastSeparator = name.lastIndexOf("/");
        if (lastSeparator > 0) {
            String subContextName = name.substring(0, lastSeparator);
            ((Context) rootContext.lookup(subContextName)).unbind(name.substring(lastSeparator + 1));
        } else {
            rootContext.unbind(name);
        }
    }

    private void registerDataSource(String name, Properties properties) {
        storeAndRemoveOldEntry(name);
        try {
            Properties prefixedProperties = new Properties();
            properties.forEach((key, value) -> prefixedProperties.put(name + "/" + key, value));
            prefixedProperties.put(name + "/type", DataSource.class.getName());
            loader.load(prefixedProperties, rootContext);
        } catch (NamingException e) {
            throw new IllegalStateException(e);
        }
        try {
            Connection connection = ((DataSource) rootContext.lookup(name)).getConnection();
            connections.add(connection);
        } catch (SQLException | NamingException e) {
            // ignore
        }
    }

    private void registerDataSource(DataSourceEntry dataSource) {
        String name = getName(dataSource);
        storeAndRemoveOldEntry(name);
        try {
            loader.load(createProperties(dataSource), rootContext);
        } catch (NamingException e) {
            throw new IllegalStateException(e);
        }
        try {
            Connection connection = ((DataSource) rootContext.lookup(name)).getConnection();
            connections.add(connection);
        } catch (SQLException | NamingException e) {
            // ignore
        }
    }

    private void registerEnvEntry(EnvEntry envEntry) {
        String name = getName(envEntry);
        storeAndRemoveOldEntry(name);
        try {
            loader.load(createProperties(envEntry), rootContext);
        } catch (NamingException e) {
            throw new IllegalStateException(e);
        }
    }

    private void storeAndRemoveOldEntry(String name) {
        if (!oldValues.containsKey(name)) {
            try {
                oldValues.put(name, rootContext.lookup(name));
            } catch (NamingException e) {
                oldValues.put(name, null);
            }
            try {
                unbind(name);
            } catch (NamingException e) {
                // ignore
            }
        } else {
            try {
                unbind(name);
            } catch (NamingException e) {
                // ignore
            }
        }
    }

    private Properties createProperties(EnvEntry envEntry) {
        Properties properties = new Properties();
        String name = getName(envEntry);
        properties.put(name + "/type", EnvEntry.UNSPECIFIED.equals(envEntry.typeAsString()) ? envEntry.type().getName()
                : envEntry.typeAsString());
        properties.put(name, envEntry.value());
        return properties;
    }

    private Properties createProperties(DataSourceEntry dataSource) {
        Properties properties = new Properties();
        String prefix = getName(dataSource) + "/";
        properties.put(prefix + "type", DataSource.class.getName());
        properties.put(prefix + "driver",
                DataSourceEntry.UNSPECIFIED.equals(dataSource.driverAsString()) ? dataSource.driver().getName()
                        : dataSource.driverAsString());
        properties.put(prefix + "url", dataSource.url());
        properties.put(prefix + "user", dataSource.user());
        properties.put(prefix + "password", dataSource.password());
        return properties;
    }

    private String getName(EnvEntry envEntry) {
        String name = envEntry.name();
        if (envEntry.compEnv()) {
            name = "java:comp/env/" + name;
        }
        return name;
    }

    private String getName(DataSourceEntry dataSource) {
        String name = dataSource.name();
        if (dataSource.compEnv()) {
            name = "java:comp/env/" + name;
        }
        return name;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private JndiLoader createJndiLoader() {
        Hashtable<String, String> env;
        try {
            env = new Hashtable<>((Map) rootContext.getEnvironment());
        } catch (NamingException e) {
            throw new IllegalStateException(e);
        }
        env.put(JndiLoader.DELIMITER, "/");
        return new JndiLoader(env);
    }

    private Context getRootContext() {
        try {
            return new InitialContext();
        } catch (NamingException e) {
            throw new IllegalStateException();
        }
    }

}
