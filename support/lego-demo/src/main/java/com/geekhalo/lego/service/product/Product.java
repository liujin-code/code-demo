package com.geekhalo.lego.service.product;

import com.geekhalo.lego.core.wide.WideItemData;
import com.geekhalo.lego.wide.WideOrderType;
import lombok.Builder;
import lombok.Data;

/**
 * Created by taoli on 2022/7/30.
 * gitee : https://gitee.com/litao851025/lego
 * 编程就像玩 Lego
 */

@Builder
@Data
public class Product implements WideItemData<WideOrderType, Long> {
    private Long id;
    private String name;
    private Integer price;

    public boolean isSaleable() {
        return true;
    }

    @Override
    public WideOrderType getItemType() {
        return WideOrderType.PRODUCT;
    }

    @Override
    public Long getKey() {
        return id;
    }
}
