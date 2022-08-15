package com.geekhalo.lego.joininmemory.demo.v2;

import com.geekhalo.lego.joininmemory.demo.OrderDetailVO;
import com.geekhalo.lego.joininmemory.demo.AddressVO;
import com.geekhalo.lego.joininmemory.demo.OrderVO;
import com.geekhalo.lego.joininmemory.demo.ProductVO;
import com.geekhalo.lego.joininmemory.demo.UserVO;
import lombok.Data;

/**
 * Created by taoli on 2022/7/30.
 * gitee : https://gitee.com/litao851025/lego
 * 编程就像玩 Lego
 */
@Data
public class OrderDetailVOV2 extends OrderDetailVO {
    private final OrderVO order;
    private UserVO user;
    private AddressVO address;
    private ProductVO product;

}
