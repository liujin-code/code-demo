package com.geekhalo.lego.core.joininmemory;

import java.util.List;

/**
 * Created by taoli on 2022/7/31.
 * gitee : https://gitee.com/litao851025/lego
 * 编程就像玩 Lego
 *
 * 执行数据 join 操作
 */
public interface JoinExecutor<DATA> {
    void execute(List<DATA> datas);
}
