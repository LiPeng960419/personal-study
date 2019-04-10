package com.coship.consumer.service;

import com.coship.consumer.entity.Order;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class OrderReceiver {

    @RabbitListener(containerFactory = "rabbitListenerContainerFactory",bindings = @QueueBinding(
            value = @Queue(value = "order-queue",durable = "true"),
            exchange = @Exchange(value = "order-exchange",durable = "true",type = "topic",ignoreDeclarationExceptions = "true"),key = "order.*"
    ))

    @RabbitHandler
    public void onOrderMessage(@Payload Order order,
                               @Headers Map<String,Object> headers,
                               Channel channel) throws Exception {
        System.out.println("收到消息 开始消费");
        System.out.println(order.getName());
        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
        //ack
        channel.basicAck(deliveryTag, false);
    }

}