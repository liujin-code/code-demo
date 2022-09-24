package com.geekhalo.lego.validator;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by taoli on 2022/9/19.
 * gitee : https://gitee.com/litao851025/lego
 * 编程就像玩 Lego
 */
public interface ValidateableOrderRepository extends JpaRepository<ValidateableOrder, Long> {
}
