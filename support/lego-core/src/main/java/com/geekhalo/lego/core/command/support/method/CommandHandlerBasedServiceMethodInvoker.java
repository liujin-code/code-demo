package com.geekhalo.lego.core.command.support.method;

import com.geekhalo.lego.core.command.support.handler.CommandHandler;
import com.geekhalo.lego.core.support.invoker.ServiceMethodInvoker;
import com.google.common.base.Preconditions;

import java.lang.reflect.Method;

public class CommandHandlerBasedServiceMethodInvoker implements ServiceMethodInvoker {
    private final CommandHandler commandHandler;

    public CommandHandlerBasedServiceMethodInvoker(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public Object invoke(Method method, Object[] arguments) {
        Preconditions.checkArgument(arguments.length == 1);
        Object param = arguments[0];
        return commandHandler.handle(param);
    }

    @Override
    public String toString(){
        return this.commandHandler.toString();
    }
}
