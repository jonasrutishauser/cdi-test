package com.github.jonasrutishauser.cdi.test.ejb;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import com.github.jonasrutishauser.cdi.test.api.context.TestScoped;

import jakarta.ejb.Startup;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.AfterBeanDiscovery;
import jakarta.enterprise.inject.spi.AnnotatedType;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.enterprise.inject.spi.ProcessSessionBean;
import jakarta.enterprise.inject.spi.SessionBeanType;

public class EjbExtension implements Extension {

    private final Set<AnnotatedType<?>> singletons = new HashSet<>();
    private final Set<AnnotatedType<?>> statelessBeans = new HashSet<>();
    private final Set<AnnotatedType<?>> statefullBeans = new HashSet<>();

    void addEjb(@Observes ProcessSessionBean<?> event) {
        if (event.getSessionBeanType() == SessionBeanType.SINGLETON) {
            singletons.add(event.getAnnotatedBeanClass());
        } else if (event.getSessionBeanType() == SessionBeanType.STATELESS) {
            statelessBeans.add(event.getAnnotatedBeanClass());
        } else if (event.getSessionBeanType() == SessionBeanType.STATEFUL) {
            statefullBeans.add(event.getAnnotatedBeanClass());
        }
    }

    void addBackendBeans(@Observes AfterBeanDiscovery event) {
        for (AnnotatedType<?> singleton : singletons) {
            event.addBean().read(singleton).scope(TestScoped.class).qualifiers(EjbInstance.Literal.INSTANCE)
                    .types(singleton.getJavaClass());
        }
        for (AnnotatedType<?> bean : statelessBeans) {
            event.addBean().read(bean).scope(Dependent.class).qualifiers(EjbInstance.Literal.INSTANCE)
                    .types(bean.getJavaClass());
        }
        for (AnnotatedType<?> bean : statefullBeans) {
            event.addBean().read(bean).scope(Dependent.class).qualifiers(EjbInstance.Literal.INSTANCE)
                    .types(bean.getJavaClass());
        }
    }

    Stream<AnnotatedType<?>> getStartupBeans() {
        return singletons.stream().filter(singleton -> singleton.isAnnotationPresent(Startup.class));
    }

}
