package com.github.jonasrutishauser.cdi.test.ejb;

import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.Stream.concat;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;

import org.jboss.weld.ejb.spi.BusinessInterfaceDescriptor;
import org.jboss.weld.ejb.spi.EjbDescriptor;
import org.jboss.weld.environment.deployment.WeldBeanDeploymentArchive;
import org.jboss.weld.environment.deployment.discovery.BeanArchiveBuilder;
import org.jboss.weld.environment.util.Reflections;
import org.jboss.weld.resources.spi.ResourceLoader;

import jakarta.ejb.Local;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Singleton;
import jakarta.ejb.Stateful;
import jakarta.ejb.Stateless;

public class EjbSupportBeanArchiveBuilder extends BeanArchiveBuilder {

    private Set<String> originalClasses;

    public EjbSupportBeanArchiveBuilder(BeanArchiveBuilder source) {
        source.getClasses().forEach(this::addClass);
    }

    @Override
    public Iterator<String> getClassIterator() {
        originalClasses = new HashSet<>(getClasses());
        return super.getClassIterator();
    }

    @Override
    public WeldBeanDeploymentArchive build() {
        WeldBeanDeploymentArchive archive = super.build();
        Collection<String> classes = originalClasses == null ? archive.getBeanClasses() : originalClasses;
        return new WeldBeanDeploymentArchive(archive.getId(), archive.getBeanClasses(), archive.getKnownClasses(),
                archive.getBeansXml()) {
            private Collection<EjbDescriptor<?>> ejbs;

            @Override
            public Collection<EjbDescriptor<?>> getEjbs() {
                if (ejbs == null) {
                    discoverEjbs();
                }
                return ejbs;
            }

            private void discoverEjbs() {
                ejbs = new ArrayList<>();
                for (String className : classes) {
                    Class<?> clazz = Reflections.loadClass(getServices().get(ResourceLoader.class), className);
                    if (clazz == null) {
                        continue;
                    }
                    if (clazz.isAnnotationPresent(Singleton.class) || clazz.isAnnotationPresent(Stateless.class)
                            || clazz.isAnnotationPresent(Stateful.class)) {
                        ejbs.add(new EjbDescriptorImpl<>(clazz));
                    }
                }
            }
        };
    }

    private static class EjbDescriptorImpl<T> implements EjbDescriptor<T> {
        private final Class<T> clazz;

        public EjbDescriptorImpl(Class<T> clazz) {
            this.clazz = clazz;
        }

        @Override
        public Class<T> getBeanClass() {
            return clazz;
        }

        @Override
        public Collection<BusinessInterfaceDescriptor<?>> getLocalBusinessInterfaces() {
            Stream<Class<?>> interfaces = Stream.empty();
            if (clazz.isAnnotationPresent(LocalBean.class)) {
                interfaces = concat(interfaces, Stream.of(clazz));
            }
            if (clazz.isAnnotationPresent(Local.class)) {
                interfaces = concat(interfaces, Arrays.<Class<?>>stream(clazz.getAnnotation(Local.class).value()));
            } else if (!clazz.isAnnotationPresent(LocalBean.class)) {
                if (clazz.getInterfaces().length == 0) {
                    interfaces = Stream.of(clazz);
                } else if (clazz.getInterfaces().length == 1) {
                    interfaces = Stream.of(clazz.getInterfaces()[0]);
                }
            }
            return interfaces.<BusinessInterfaceDescriptor<?>>map(this::toBusinessInterfaceDescriptor)
                    .collect(toUnmodifiableList());
        }

        private <I> BusinessInterfaceDescriptor<I> toBusinessInterfaceDescriptor(Class<I> cl) {
            return () -> cl;
        }

        @Override
        public Collection<BusinessInterfaceDescriptor<?>> getRemoteBusinessInterfaces() {
            return Collections.emptyList();
        }

        @Override
        public String getEjbName() {
            if (isStateless() && !clazz.getAnnotation(Stateless.class).name().isBlank()) {
                return clazz.getAnnotation(Stateless.class).name();
            }
            if (isSingleton() && !clazz.getAnnotation(Singleton.class).name().isBlank()) {
                return clazz.getAnnotation(Singleton.class).name();
            }
            if (isStateful() && !clazz.getAnnotation(Stateful.class).name().isBlank()) {
                return clazz.getAnnotation(Stateful.class).name();
            }
            return clazz.getSimpleName();
        }

        @Override
        public Collection<Method> getRemoveMethods() {
            return null;
        }

        @Override
        public boolean isStateless() {
            return clazz.isAnnotationPresent(Stateless.class);
        }

        @Override
        public boolean isSingleton() {
            return clazz.isAnnotationPresent(Singleton.class);
        }

        @Override
        public boolean isStateful() {
            return clazz.isAnnotationPresent(Stateful.class);
        }

        @Override
        public boolean isMessageDriven() {
            return false;
        }

        @Override
        public boolean isPassivationCapable() {
            return false;
        }
    }
}
