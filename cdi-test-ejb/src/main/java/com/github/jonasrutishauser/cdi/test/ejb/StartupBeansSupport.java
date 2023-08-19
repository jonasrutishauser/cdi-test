package com.github.jonasrutishauser.cdi.test.ejb;

import com.github.jonasrutishauser.cdi.test.api.TestInfo;
import com.github.jonasrutishauser.cdi.test.api.context.TestScoped;

import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;

@TestScoped
public class StartupBeansSupport {

    void createStartupBeans(@Observes @Initialized(TestScoped.class) TestInfo event, EjbExtension extension,
            BeanManager beanManager) {
        extension.getStartupBeans().forEach(startup -> {
            Bean<?> bean = beanManager
                    .resolve(beanManager.getBeans(startup.getJavaClass(), EjbInstance.Literal.INSTANCE));
            beanManager.getContext(TestScoped.class).get(bean, beanManager.createCreationalContext(null));
        });
    }

}
