package com.geekhalo.like.domain;

import com.geekhalo.lego.core.command.support.AbstractDomainEvent;

public abstract class AbstractCancelledEvent<AGG extends AbstractAction>
        extends AbstractDomainEvent<Long, AGG> {
    public AbstractCancelledEvent(AGG source) {
        super(source);
    }
}
