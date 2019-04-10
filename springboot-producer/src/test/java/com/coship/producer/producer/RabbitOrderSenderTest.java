package com.coship.producer.producer;

import com.alibaba.fastjson.JSON;
import com.coship.producer.entity.Order;
import com.coship.producer.service.OrderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RabbitOrderSenderTest {

    @Autowired
    private RabbitOrderSender orderSender;

    @Autowired
    private OrderService orderService;

    @Test
    public void sendOrder() throws Exception {
        Order order = new Order();
        order.setId("12345678");
        order.setName("测试重传222");
        order.setMessageId(System.currentTimeMillis()+"$"+ UUID.randomUUID().toString());
        orderService.createOrder(order);
    }

}