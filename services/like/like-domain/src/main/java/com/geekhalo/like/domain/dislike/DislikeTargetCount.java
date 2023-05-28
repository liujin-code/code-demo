package com.geekhalo.like.domain.dislike;

import com.geekhalo.like.domain.AbstractTargetCount;
import com.geekhalo.like.domain.target.ActionTarget;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "dislike_target_count")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class DislikeTargetCount extends AbstractTargetCount {
    public static DislikeTargetCount create(ActionTarget target) {
        return create(target, 0L);
    }

    public static DislikeTargetCount create(ActionTarget target, Long count) {
        DislikeTargetCount targetCount = new DislikeTargetCount();
        targetCount.init(target, count);
        return targetCount;
    }
}
