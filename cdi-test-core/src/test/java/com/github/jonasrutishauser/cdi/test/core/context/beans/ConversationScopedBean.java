package com.github.jonasrutishauser.cdi.test.core.context.beans;

import java.io.Serializable;

import jakarta.enterprise.context.ConversationScoped;

@ConversationScoped
public class ConversationScopedBean extends ScopedBean implements Serializable {

}
