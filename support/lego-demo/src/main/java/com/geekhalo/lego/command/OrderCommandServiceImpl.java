package com.geekhalo.lego.command;

import com.geekhalo.lego.core.command.AggNotFoundException;
import com.geekhalo.lego.core.loader.LazyLoadProxyFactory;
import com.geekhalo.lego.core.validator.ValidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

/**
 * Created by taoli on 2022/10/2.
 * gitee : https://gitee.com/litao851025/lego
 * 编程就像玩 Lego
 */
@Service
@Transactional
public class OrderCommandServiceImpl implements OrderCommandService{
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private LazyLoadProxyFactory lazyLoadProxyFactory;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private ValidateService validateService;

    @Override
    public Order create(CreateOrderCommand command) {
        CreateOrderContext context = new CreateOrderContext(command);
        CreateOrderContext contextProxy = this.lazyLoadProxyFactory.createProxyFor(context);

        validateService.validateBusiness(contextProxy);

        Order order = Order.create(contextProxy);

        this.orderRepository.save(order);
        order.consumeAndClearEvent(event -> eventPublisher.publishEvent(event));
        return order;
    }

    @Override
    public void paySuccess(PayByIdSuccessCommand command) {
        Order order = this.orderRepository.findById(command.getOrderId())
                .orElseThrow(() -> new AggNotFoundException(command.getOrderId()));
        order.paySuccess(command);
        this.orderRepository.save(order);
        order.consumeAndClearEvent(event -> eventPublisher.publishEvent(event));
    }

    @Override
    public Order syncByOrderId(SyncOrderByIdCommand command) {
        Optional<Order> orderOpt = this.orderRepository.findById(command.getKey());
        SyncOrderByIdContext context = this.lazyLoadProxyFactory.createProxyFor(new SyncOrderByIdContext(command));
        this.validateService.validateRule(context);

        Order order = orderOpt.orElseGet(() -> Order.createForSync(context));

        order.applySync(context);
        order.consumeAndClearEvent(event -> eventPublisher.publishEvent(event));
        this.orderRepository.save(order);
        return order;
    }

    @Override
    public void cancel(Long orderId) {
        Order order = this.orderRepository.findById(orderId)
                .orElseThrow(() -> new AggNotFoundException(orderId));
        order.cancel();
        this.orderRepository.save(order);
    }
}
