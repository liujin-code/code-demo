package com.geekhalo.lego.core.singlequery.jpa.support.handler;

import com.geekhalo.lego.annotation.singlequery.FieldGreaterThanOrEqualTo;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Created by taoli on 2022/8/31.
 * gitee : https://gitee.com/litao851025/lego
 * 编程就像玩 Lego
 */
public class JpaFieldGreaterThanOrEqualToHandler
    extends AbstractJpaAnnotationHandler<FieldGreaterThanOrEqualTo>{
    public JpaFieldGreaterThanOrEqualToHandler() {
        super(FieldGreaterThanOrEqualTo.class);
    }

    @Override
    public <E> Predicate create(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder, FieldGreaterThanOrEqualTo fieldGreaterThanOrEqualTo, Object value) {
        if (value instanceof Comparable){
            return criteriaBuilder.greaterThanOrEqualTo(root.get(fieldGreaterThanOrEqualTo.value()), (Comparable) value);
        }
        return null;
    }
}
