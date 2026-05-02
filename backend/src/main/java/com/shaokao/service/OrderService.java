package com.shaokao.service;

import com.shaokao.entity.OrderInfo;
import com.shaokao.entity.OrderItem;
import com.shaokao.mapper.OrderItemMapper;
import com.shaokao.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Transactional
    public Long createOrder(OrderInfo orderInfo) {
        List<OrderItem> items = orderInfo.getItems();

        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : items) {
            total = total.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        orderInfo.setTotalAmount(total);
        orderInfo.setStatus(0);
        orderMapper.insert(orderInfo);

        for (OrderItem item : items) {
            item.setOrderId(orderInfo.getId());
            orderItemMapper.insert(item);
        }

        return orderInfo.getId();
    }
}
