package com.github.jonasrutishauser.cdi.test.microprofile.metrics;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.LongAdder;

import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.Metric;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.jboss.logging.Logger;
import org.jboss.weld.bean.proxy.CombinedInterceptorAndDecoratorStackMethodHandler;
import org.jboss.weld.bean.proxy.MethodHandler;
import org.jboss.weld.bean.proxy.ProxyObject;
import org.jboss.weld.interceptor.proxy.InterceptionContext;
import org.jboss.weld.interceptor.proxy.InterceptorMethodHandler;

import com.github.jonasrutishauser.cdi.test.api.TestInfo;
import com.github.jonasrutishauser.cdi.test.api.context.TestScoped;

import io.smallrye.metrics.app.CounterImpl;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Destroyed;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.spi.AnnotatedType;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;

@ApplicationScoped
class MetricsCleanup {

    private static final Logger LOGGER = Logger.getLogger(MetricsCleanup.class);

    private final Field counterCount = getCounterCountField();
    private final MetricRegistry registry;

    @Inject
    MetricsCleanup(MetricRegistry registry) {
        this.registry = registry;
    }

    void cleanup(@Observes @Destroyed(TestScoped.class) TestInfo event, BeanManager beanManager) {
        registry.removeMatching((metricId, metric) -> "ForwardingGauge".equals(metric.getClass().getSimpleName()) && isTestScopedDelegate(metric, beanManager));
        registry.getCounters().values().forEach(this::reset);
    }

    private boolean isTestScopedDelegate(Metric metric, BeanManager beanManager) {
        try {
            Field field = metric.getClass().getDeclaredField("object");
            field.setAccessible(true);
            Object delegate = field.get(metric);
            AnnotatedType<?> annotatedType = getAnnotatedType(delegate);
            return (annotatedType != null && annotatedType.isAnnotationPresent(TestScoped.class))
                    || beanManager.getBeans(delegate.getClass(), Any.Literal.INSTANCE).stream()
                            .anyMatch(bean -> TestScoped.class.equals(bean.getScope()));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            LOGGER.warn("failed to get delegate object");
        }
        return false;
    }

    private AnnotatedType<?> getAnnotatedType(Object proxyObject) throws NoSuchFieldException, IllegalAccessException {
        if (proxyObject instanceof ProxyObject) {
            MethodHandler handler = ((ProxyObject) proxyObject).weld_getHandler();
            if (handler instanceof CombinedInterceptorAndDecoratorStackMethodHandler) {
                Field ctx = InterceptorMethodHandler.class.getDeclaredField("ctx");
                ctx.setAccessible(true);
                InterceptionContext context = (InterceptionContext) ctx.get(
                        ((CombinedInterceptorAndDecoratorStackMethodHandler) handler).getInterceptorMethodHandler());
                Field annotatedType = InterceptionContext.class.getDeclaredField("annotatedType");
                annotatedType.setAccessible(true);
                return (AnnotatedType<?>) annotatedType.get(context);
            }
        }
        return null;
    }

    private void reset(Counter counter) {
        if (counter instanceof CounterImpl) {
            try {
                ((LongAdder) counterCount.get(counter)).reset();
            } catch (IllegalAccessException e) {
                LOGGER.warn("failed to reset counter");
            }
        }
    }

    private Field getCounterCountField() {
        try {
            Field field = CounterImpl.class.getDeclaredField("count");
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException(e);
        }
    }

}
