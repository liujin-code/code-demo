package com.geekhalo.lego.core.command;

import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;

/**
 * Created by taoli on 2022/10/2.
 * gitee : https://gitee.com/litao851025/lego
 * 编程就像玩 Lego
 *
 * 标记一个接口为 CommandService，系统自动生成实现的代理对象
 */
@Indexed
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface CommandServiceDefinition {
    Class domainClass();

    Class<? extends CommandRepository> repositoryClass();
}
