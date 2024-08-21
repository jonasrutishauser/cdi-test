package com.github.jonasrutishauser.cdi.test.ejb;

import org.jboss.weld.environment.deployment.discovery.BeanArchiveBuilder;
import org.jboss.weld.environment.deployment.discovery.FileSystemBeanArchiveHandler;

import jakarta.annotation.Priority;

@Priority(10)
public class EjbFileSystemBeanArchiveHandler extends FileSystemBeanArchiveHandler {
    @Override
    public BeanArchiveBuilder handle(String path) {
        return new EjbSupportBeanArchiveBuilder(super.handle(path));
    }
}
