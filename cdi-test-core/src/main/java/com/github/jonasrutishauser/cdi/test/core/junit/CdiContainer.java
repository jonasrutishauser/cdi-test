package com.github.jonasrutishauser.cdi.test.core.junit;

import org.jboss.logging.Logger;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.exceptions.IllegalStateException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext.Store.CloseableResource;
import org.junit.jupiter.api.extension.Extensions;

import com.github.jonasrutishauser.cdi.test.api.TestInfo;
import com.github.jonasrutishauser.cdi.test.api.annotations.ActivatableTestImplementation;
import com.github.jonasrutishauser.cdi.test.core.context.ContextControl;
import com.github.jonasrutishauser.cdi.test.core.interceptor.TestImplementationManager;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.AmbiguousResolutionException;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.inject.Singleton;

class CdiContainer implements CloseableResource {

    private static final Logger LOGGER = Logger.getLogger(CdiContainer.class);

    private final Weld weld = new Weld();

    private WeldContainer weldContainer;

    public void setTest(TestInfo testInfo) {
        if (weldContainer == null || !weldContainer.isRunning()) {
            initialize(testInfo);
        }
        contextControl().start(testInfo);
    }

    public void testEnded() {
        contextControl().preStop();
    }

    public void clearTest() {
        contextControl().stop();
    }

    public <T> TestMethodInterceptor inject(Object testInstance, Class<T> testClass) {
        BeanManager beanManager = weldContainer.getBeanManager();
        AnnotatedType<T> annotatedType = beanManager.createAnnotatedType(testClass);
        Bean<T> bean;
        try {
            bean = (Bean<T>) beanManager.resolve(beanManager.getBeans(testClass, Any.Literal.INSTANCE));
        } catch (AmbiguousResolutionException e) {
            LOGGER.warnv("Failed to get test instance bean: {}", e.getMessage());
            bean = null;
        }
        InjectionTarget<T> injectionTarget = beanManager.getInjectionTargetFactory(annotatedType)
                .createInjectionTarget(bean);
        CreationalContext<T> ctx = beanManager.createCreationalContext(bean);
        injectionTarget.inject(testClass.cast(testInstance), ctx);
        for (InjectionPoint ip : injectionTarget.getInjectionPoints()) {
            if (ip.getType() instanceof Class
                    && ((Class<?>) ip.getType()).isAnnotationPresent(ActivatableTestImplementation.class)) {
                weldContainer.select(TestImplementationManager.class).get()
                        .enableTestImplementation((Class<?>) ip.getType());
            }
        }
        injectionTarget.postConstruct(testClass.cast(testInstance));
        return new CdiTestMethodInterceptor<T>(weldContainer.getBeanManager(), testClass, testClass.cast(testInstance),
                injectionTarget, ctx);
    }

    private ContextControl contextControl() {
        return weldContainer.select(ContextControl.class).get();
    }

    private void initialize(TestInfo testInfo) {
        LOGGER.info("Booting CDI container");
        long start = System.currentTimeMillis();
        weld.addExtension(new CdiTestExtension(testInfo));
        addBeans();
        weldContainer = weld.initialize();
        long end = System.currentTimeMillis();
        LOGGER.infov("Booting CDI container finished in {0} ms", end - start);
        if (!weldContainer.isRunning()) {
            throw new IllegalStateException("Failed to start CDI container");
        }
    }

    private void addBeans() {
        weld.addBeanDefiningAnnotations(Singleton.class, ExtendWith.class, Extensions.class);
    }

    @Override
    public void close() throws Throwable {
        weld.shutdown();
    }
}
