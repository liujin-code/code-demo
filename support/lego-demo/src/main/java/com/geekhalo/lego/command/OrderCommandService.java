package com.geekhalo.lego.command;

import com.geekhalo.lego.core.command.NoCommandService;

/**
 * Created by taoli on 2022/10/2.
 * gitee : https://gitee.com/litao851025/lego
 * 编程就像玩 Lego
 */
@NoCommandService
public interface OrderCommandService extends CustomOrderCommandService{
    Order create(CreateOrderCommand command);

    void paySuccess(PayByIdSuccessCommand command);

    Order syncByOrderId(SyncOrderByIdCommand command);
}
