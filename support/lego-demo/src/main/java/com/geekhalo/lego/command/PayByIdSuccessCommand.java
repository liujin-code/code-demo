package com.geekhalo.lego.command;

import com.geekhalo.lego.core.command.CommandForUpdateById;
import lombok.Data;

/**
 * Created by taoli on 2022/10/2.
 * gitee : https://gitee.com/litao851025/lego
 * 编程就像玩 Lego
 */
@Data
public class PayByIdSuccessCommand implements CommandForUpdateById {
    private Long orderId;

    private String chanel;

    private Long price;

    @Override
    public Object getId() {
        return orderId;
    }
}
