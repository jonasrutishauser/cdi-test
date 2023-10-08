package com.github.jonasrutishauser.cdi.test.core.junit;

import static java.util.function.Predicate.isEqual;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extensions;
import org.mockito.Mock;

import com.github.jonasrutishauser.cdi.test.api.TestInfo;
import com.github.jonasrutishauser.cdi.test.api.annotations.ActivatableTestImplementation;
import com.github.jonasrutishauser.cdi.test.api.annotations.GlobalTestImplementation;
import com.github.jonasrutishauser.cdi.test.api.context.TestScoped;
import com.github.jonasrutishauser.cdi.test.core.context.TestContext;
import com.github.jonasrutishauser.cdi.test.core.interceptor.ReplaceableStereotype;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.BeforeDestroyed;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AfterTypeDiscovery;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessBeanAttributes;
import javax.enterprise.inject.spi.WithAnnotations;
import javax.enterprise.inject.spi.configurator.AnnotatedMethodConfigurator;
import javax.enterprise.inject.spi.configurator.AnnotatedTypeConfigurator;

public class CdiTestExtension implements Extension {

    private final TestContext testContext = new TestContext();

    private final Map<Type, Set<Class<?>>> testImplementations = new HashMap<>();
    private final Set<Type> mockedTypes = new HashSet<>();

    private final Set<String> testScopedBeans;

    CdiTestExtension(TestInfo testInfo) {
        testContext.setTestInfo(testInfo);
        testScopedBeans =  readAllLines("testScoped.beans");
    }

    private Set<String> readAllLines(String fileName) {
        Set<String> result = new HashSet<>();
        try {
            Enumeration<URL> resources = getClass().getClassLoader().getResources(fileName);
            while (resources.hasMoreElements()) {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(resources.nextElement().openStream()))) {
                    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                        if (!line.trim().isEmpty() && !line.trim().startsWith("#")) {
                            result.add(line.trim());
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return result;
    }

    void changeToTestScope(@Observes @WithAnnotations(ApplicationScoped.class) ProcessAnnotatedType<?> event) {
        if (testScopedBeans.contains(event.getAnnotatedType().getJavaClass().getName())) {
            AnnotatedTypeConfigurator<?> configurator = event.configureAnnotatedType()
                    .remove(ApplicationScoped.class::isInstance).add(TestScoped.Literal.INSTANCE);
            for (AnnotatedMethodConfigurator<?> method : configurator.methods()) {
                AnnotatedMethod<?> annotatedMethod = method.getAnnotated();
                if (!annotatedMethod.getParameters().isEmpty()
                        && annotatedMethod.getParameters().get(0).isAnnotationPresent(Observes.class)) {
                    if (annotatedMethod.getParameters().get(0).isAnnotationPresent(Initialized.class)) {
                        method.params().get(0).remove(Initialized.class::isInstance)
                                .add(Initialized.Literal.of(TestScoped.class));
                    } else if (annotatedMethod.getParameters().get(0).isAnnotationPresent(BeforeDestroyed.class)) {
                        method.params().get(0).remove(BeforeDestroyed.class::isInstance)
                                .add(BeforeDestroyed.Literal.of(TestScoped.class));
                    }
                }
            }
        }
    }

    void addContexts(@Observes AfterBeanDiscovery event) {
        event.addContext(testContext);
        event.addBean().types(TestInfo.class).beanClass(TestContext.class).scope(TestScoped.class).createWith(ctx -> {
            throw new IllegalStateException();
        });
    }

    void registerMockedTypes(@Observes @WithAnnotations({ExtendWith.class, Extensions.class}) ProcessAnnotatedType<?> event) {
        try {
            MockitoHelper helper = new MockitoHelper(mockedTypes);
            event.getAnnotatedType().getFields().forEach(helper::registerMockedTypeIfNeeded);
        } catch (NoClassDefFoundError e) {
            // ignore as mockito is not in use
        }
    }

    private static class MockitoHelper {
        private final Set<Type> mockedTypes;

        public MockitoHelper(Set<Type> mockedTypes) {
            this.mockedTypes = mockedTypes;
        }

        private <T> void registerMockedTypeIfNeeded(AnnotatedField<T> field) {
            if (field.isAnnotationPresent(Mock.class)) {
                mockedTypes.addAll(field.getTypeClosure());
            }
        }
    }

    void registerActivatableTestImplementation(
            @Observes @WithAnnotations(ActivatableTestImplementation.class) ProcessAnnotatedType<?> event) {
        ActivatableTestImplementation activatableTestImplementation = event.getAnnotatedType()
                .getAnnotation(ActivatableTestImplementation.class);
        if (activatableTestImplementation != null) {
            if (activatableTestImplementation.value().length == 0) {
                event.getAnnotatedType().getTypeClosure()
                        .forEach(t -> registerTestImplementation(t, event.getAnnotatedType()));
            } else {
                for (Class<?> type : activatableTestImplementation.value()) {
                    registerTestImplementation(event.getAnnotatedType().getTypeClosure().stream()
                            .filter(t -> type.equals(t) || (t instanceof ParameterizedType
                                    && type.equals(((ParameterizedType) t).getRawType())))
                            .findAny().orElse(type), event.getAnnotatedType());
                }
            }
        }
    }

    public void registerTestImplementation(Type type, AnnotatedType<?> testImplementation) {
        if (!Object.class.equals(type)) {
            testImplementations.computeIfAbsent(type, key -> new HashSet<>()).add(testImplementation.getJavaClass());
        }
    }

    void configureTestBeans(@Observes ProcessBeanAttributes<?> event) {
        if (!event.getAnnotated().getAnnotations(ExtendWith.class).isEmpty()
                && Dependent.class.equals(event.getBeanAttributes().getScope())
                && !event.getAnnotated().isAnnotationPresent(Dependent.class)) {
            if (event.getAnnotated().getAnnotations(ExtendWith.class).stream().map(ExtendWith::value)
                    .flatMap(Arrays::stream).anyMatch(isEqual(CdiTestJunitExtension.class))) {
                event.configureBeanAttributes().scope(TestScoped.class);
            } else {
                event.veto();
            }
        }
    }

    void configureActivatableTestImplementations(@Observes ProcessBeanAttributes<?> event) {
        ActivatableTestImplementation activatableTestImplementation = event.getAnnotated()
                .getAnnotation(ActivatableTestImplementation.class);
        if (activatableTestImplementation != null) {
            event.configureBeanAttributes().types(event.getAnnotated().getBaseType());
        } else if (event.getBeanAttributes().getTypes().stream().anyMatch(testImplementations.keySet()::contains)
                || event.getBeanAttributes().getTypes().stream()
                        .anyMatch(isEqual(Object.class).negate().and(mockedTypes::contains))) {
                            event.configureBeanAttributes().addStereotype(ReplaceableStereotype.class);
                        }
    }

    void enableGlobalTestImplementations(@Observes AfterTypeDiscovery event) {
        event.getAlternatives().add(GlobalTestImplementation.class);
    }

    public Map<Class<?>, Set<Class<?>>> getTestImplementations(Bean<?> bean) {
        return bean.getTypes().stream().filter(testImplementations::containsKey).collect(Collectors.toMap(
                t -> t instanceof ParameterizedType ? (Class<?>) ((ParameterizedType) t).getRawType() : (Class<?>) t,
                testImplementations::get));
    }

}
