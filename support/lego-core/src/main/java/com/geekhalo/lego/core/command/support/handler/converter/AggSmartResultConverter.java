package com.geekhalo.lego.core.command.support.handler.converter;

import com.geekhalo.lego.core.command.AggRoot;
import org.springframework.stereotype.Component;

public class AggSmartResultConverter<AGG extends AggRoot>
        implements SmartResultConverter<AGG, Object, AGG> {

    @Override
    public AGG convert(AGG agg, Object o) {
        return agg;
    }

    @Override
    public boolean apply(Class aggClass, Class contextClass, Class resultClass) {
        return aggClass.equals(resultClass);
    }
}
