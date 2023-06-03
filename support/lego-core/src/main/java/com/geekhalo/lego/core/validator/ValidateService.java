package com.geekhalo.lego.core.validator;

import com.geekhalo.lego.common.validator.ValidateErrorHandler;
import com.geekhalo.lego.common.validator.ValidateErrors;
import com.geekhalo.lego.common.validator.ValidateErrorsHandler;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import java.util.List;
import java.util.Set;

/**
 * Created by taoli on 2022/9/11.
 * gitee : https://gitee.com/litao851025/lego
 * 编程就像玩 Lego
 */
public class ValidateService {
    private List<ParamValidator> paramValidators = Lists.newArrayList();
    private List<BusinessValidator> businessValidators = Lists.newArrayList();
    private List<RuleValidator> ruleValidators = Lists.newArrayList();

    @Autowired
    private ValidateErrorsHandler validateErrorsHandler;

    @Value("${validateService.business.blacklist:[]}")
    private Set<String> businessBlacklist;

    public void validateParam(Object param, ValidateErrorHandler handler){
        this.paramValidators.stream()
                .filter(paramValidator -> paramValidator.support(param))
                .forEach(paramValidator -> paramValidator.validate(param, handler));
    }

    public void validateParam(List<Object> params){
        ErrorsCollector errorsCollector = new ErrorsCollector();
        for (Object param : params) {
            this.paramValidators.stream()
                    .filter(paramValidator -> paramValidator.support(param))
                    .forEach(paramValidator -> paramValidator.validate(param, errorsCollector));
        }

        ValidateErrors errors = errorsCollector.getValidateErrors();

        if (errors != null && errors.hasError()) {
            this.validateErrorsHandler.handleErrors(errors);
        }
    }

    public void validateBusiness(Object context, ValidateErrorHandler handler){
        this.businessValidators.stream()
                .filter(businessValidator -> notInBusinessBlackList(businessValidator.id()))
                .filter(businessValidator -> businessValidator.support(context))
                .forEach(businessValidator -> businessValidator.validate(context, handler));
    }

    public void validateBusiness(Object context){
        this.businessValidators.stream()
                .filter(businessValidator -> notInBusinessBlackList(businessValidator.id()))
                .filter(businessValidator -> businessValidator.support(context))
                .forEach(businessValidator -> businessValidator.validate(context));
    }

    public void validateRule(Object rule, ValidateErrorHandler handler){
        this.ruleValidators.stream()
                .filter(ruleValidator -> ruleValidator.support(rule))
                .forEach(ruleValidator -> ruleValidator.validate(rule, handler));
    }

    public void validateRule(Object rule){
        this.businessValidators.stream()
                .filter(ruleValidator -> ruleValidator.support(rule))
                .forEach(ruleValidator -> ruleValidator.validate(rule));
    }

    private boolean notInBusinessBlackList(String id) {
        return !this.businessBlacklist.contains(id);
    }

    @Autowired(required = false)
    public void setParamValidators(List<ParamValidator> paramValidators){
        this.paramValidators.addAll(paramValidators);
        AnnotationAwareOrderComparator.sort(this.paramValidators);
    }

    @Autowired(required = false)
    public void setBeanValidators(List<BusinessValidator> businessValidators){
        this.businessValidators.addAll(businessValidators);
        AnnotationAwareOrderComparator.sort(this.businessValidators);
    }

    @Autowired(required = false)
    public void setRuleValidators(List<RuleValidator> ruleValidators){
        this.ruleValidators.addAll(ruleValidators);
        AnnotationAwareOrderComparator.sort(this.ruleValidators);
    }
}
