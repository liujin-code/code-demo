package com.geekhalo.lego.core.command.support.handler;

import com.geekhalo.lego.core.command.*;
import com.geekhalo.lego.core.command.support.handler.aggfactory.AggFactory;
import com.geekhalo.lego.core.loader.LazyLoadProxyFactory;
import com.geekhalo.lego.core.validator.ValidateService;
import com.google.common.base.Preconditions;
import lombok.Setter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.support.TransactionTemplate;

@Setter
public class CreateAggCommandHandler<
        AGG extends AggRoot,
        CMD,
        CONTEXT,
        RESULT>
        extends AbstractAggCommandHandler<AGG, CMD, CONTEXT, RESULT>{

    private AggFactory<CONTEXT, AGG> aggFactory;

    public CreateAggCommandHandler(ValidateService validateService,
                                   LazyLoadProxyFactory lazyLoadProxyFactory,
                                   ApplicationEventPublisher eventPublisher,
                                   TransactionTemplate transactionTemplate) {
        super(validateService, lazyLoadProxyFactory, eventPublisher, transactionTemplate);
    }


    @Override
    protected AGG getOrCreateAgg(CMD cmd, CONTEXT context) {
        return this.aggFactory.create(context);
    }

    @Override
    public void validate(){
        super.validate();
        Preconditions.checkArgument(this.aggFactory != null, "Agg Factory Can not be null");
    }

    public void setAggFactory(AggFactory<CONTEXT, AGG> aggFactory) {
        Preconditions.checkArgument(aggFactory != null);
        this.aggFactory = aggFactory;
    }

}
