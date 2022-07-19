package com.geekhalo.lego.core.spliter.service.support;

import com.geekhalo.lego.core.spliter.service.ParamSplitter;

/**
 * Created by taoli on 2022/7/19.
 * gitee : https://gitee.com/litao851025/lego
 * 编程就像玩 Lego
 */
public interface ParamSplitterBuilder {
    boolean support(Class paramCls);

    ParamSplitter build(Class paramCls);
}
