package com.github.jonasrutishauser.cdi.test.ejb;

import javax.annotation.Priority;

import org.jboss.weld.environment.deployment.discovery.BeanArchiveBuilder;
import org.jboss.weld.environment.deployment.discovery.FileSystemBeanArchiveHandler;

@Priority(10)
public class EjbFileSystemBeanArchiveHandler extends FileSystemBeanArchiveHandler {
    @Override
    public BeanArchiveBuilder handle(String path) {
        return new EjbSupportBeanArchiveBuilder(super.handle(path));
    }
}
