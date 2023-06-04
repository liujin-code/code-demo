package com.geekhalo.lego.core.command.support.handler;

import lombok.Value;

@Value
public class ResultConverterNotFoundException extends RuntimeException{
    private final Class aggClass;
    private final Class contextClass;
    private final Class resultClass;
}
