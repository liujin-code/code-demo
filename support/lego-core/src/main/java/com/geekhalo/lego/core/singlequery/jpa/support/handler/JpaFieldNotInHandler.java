package com.geekhalo.lego.core.singlequery.jpa.support.handler;

import com.geekhalo.lego.annotation.singlequery.FieldNotIn;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.lang.reflect.Field;
import java.util.Collection;

/**
 * Created by taoli on 2022/8/31.
 * gitee : https://gitee.com/litao851025/lego
 * 编程就像玩 Lego
 */
public class JpaFieldNotInHandler
    extends AbstractJpaAnnotationHandler<FieldNotIn>{
    public JpaFieldNotInHandler() {
        super(FieldNotIn.class);
    }

    @Override
    public <E> Predicate create(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder, FieldNotIn fieldNotIn, Object value) {
        if (value instanceof Collection){
            return criteriaBuilder.in(createExpression(root, fieldNameOf(fieldNotIn)))
                    .value((Collection<?>) value).not();
        }
        return null;
    }

    @Override
    protected boolean matchField(Field field, Class queryType) {
        return Collection.class.isAssignableFrom(queryType);
    }

    @Override
    protected String fieldNameOf(FieldNotIn fieldNotIn) {
        return fieldNotIn.value();
    }
}
