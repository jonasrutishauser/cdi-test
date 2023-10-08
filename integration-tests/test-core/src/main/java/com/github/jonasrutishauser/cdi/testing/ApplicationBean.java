package com.github.jonasrutishauser.cdi.testing;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ApplicationBean extends BaseBean {

    @Inject
    private OtherApplicationBean otherApplicationBean;
}
