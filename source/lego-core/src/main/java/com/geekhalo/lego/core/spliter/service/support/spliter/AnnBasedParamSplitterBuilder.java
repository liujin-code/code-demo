package com.geekhalo.lego.core.spliter.service.support.spliter;

import com.geekhalo.lego.annotation.spliter.SplitParam;
import com.geekhalo.lego.core.spliter.service.ParamSplitter;
import com.geekhalo.lego.core.spliter.service.SmartParamSplitter;
import com.geekhalo.lego.core.spliter.service.support.ParamSplitterBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by taoli on 2022/7/19.
 * gitee : https://gitee.com/litao851025/lego
 * 编程就像玩 Lego
 */
@Slf4j
public class AnnBasedParamSplitterBuilder implements ParamSplitterBuilder {
    private final List<SmartParamSplitter> smartParamSplitters;

    public AnnBasedParamSplitterBuilder(List<SmartParamSplitter> smartParamSplitters) {
        this.smartParamSplitters = smartParamSplitters;
    }

    @Override
    public boolean support(Class paramCls) {
        Field splitteField = getSplitteField(paramCls);
        if (splitteField == null){
            log.warn("Filed to find @SplitParam splitter for type {}", paramCls);
            return false;
        }

        Class<?> fieldType = splitteField.getType();
        ParamSplitter paramSplitter = getParamSplitter(fieldType);
        if (paramSplitter == null){
            log.warn("Filed to find param splitter for type {}", fieldType);
            return false;
        }

        return true;
    }

    @Override
    public ParamSplitter build(Class paramCls) {
        Field splitteField = getSplitteField(paramCls);
        ParamSplitter paramSplitter = getParamSplitter(splitteField.getType());

        return new AnnBasedParamSplitter(paramCls, paramSplitter, splitteField);
    }

    private ParamSplitter getParamSplitter(Class fieldCls){
        return this.smartParamSplitters.stream()
                .filter(smartParamSplitter -> smartParamSplitter.support(fieldCls))
                .findAny()
                .orElse(null);
    }

    private Field getSplitteField(Class paramCls){
        Field[] splitteFields = FieldUtils.getFieldsWithAnnotation(paramCls, SplitParam.class);
        if (splitteFields.length == 0){
            return null;
        }
        if (splitteFields.length != 1){
            log.warn("More Than One Filed has @SplitParam on {}", paramCls);
            return null;
        }
        return splitteFields[0];
    }
}
