package com.geekhalo.lego.core.query.support.method;

import java.lang.reflect.Method;

/**
 * Created by taoli on 2022/9/29.
 * gitee : https://gitee.com/litao851025/lego
 * 编程就像玩 Lego
 */
public interface QueryServiceMethodFactory {
    QueryServiceMethod createForMethod(Method method);
}
