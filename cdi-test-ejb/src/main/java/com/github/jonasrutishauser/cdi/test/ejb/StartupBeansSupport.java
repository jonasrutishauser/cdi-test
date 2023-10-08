package com.github.jonasrutishauser.cdi.test.ejb;

import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import com.github.jonasrutishauser.cdi.test.api.TestInfo;
import com.github.jonasrutishauser.cdi.test.api.context.TestScoped;

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
