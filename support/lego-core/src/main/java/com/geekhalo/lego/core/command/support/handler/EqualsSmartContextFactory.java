package com.geekhalo.lego.core.command.support.handler;

import org.springframework.stereotype.Component;

@Component
public class EqualsSmartContextFactory implements SmartContextFactory{
    @Override
    public Object create(Object o) {
        return o;
    }

    @Override
    public boolean apply(Class cmdClass, Class contextClass) {
        return cmdClass.equals(contextClass);
    }
}
