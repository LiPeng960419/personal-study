package com.coship.producer.service;

import com.alibaba.fastjson.JSON;
import com.coship.producer.constant.Constants;
import com.coship.producer.entity.BrokerMessageLog;
import com.coship.producer.entity.Order;
import com.coship.producer.mapper.BrokerMessageLogMapper;
import com.coship.producer.mapper.OrderMapper;
import com.coship.producer.producer.RabbitOrderSender;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;

@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private BrokerMessageLogMapper brokerMessageLogMapper;

    @Autowired
    private RabbitOrderSender rabbitOrderSender;

    public void createOrder(Order order) throws Exception {
        // 使用当前时间当做订单创建时间（为了模拟一下简化）
        Date orderTime = new Date();
        // 插入业务数据
        orderMapper.insert(order);
        // 插入消息记录表数据
        BrokerMessageLog brokerMessageLog = new BrokerMessageLog();
        // 消息唯一ID
        brokerMessageLog.setMessageId(order.getMessageId());
        // 保存消息整体 转为JSON 格式存储入库
        brokerMessageLog.setMessage(JSON.toJSONString(order));
        // 设置消息状态为0 表示发送中
        brokerMessageLog.setStatus("0");
        // 设置消息未确认超时时间窗口为 一分钟
        brokerMessageLog.setNextRetry(DateUtils.addMinutes(orderTime, Constants.ORDER_TIMEOUT));
        brokerMessageLog.setCreateTime(new Date());
        brokerMessageLog.setUpdateTime(new Date());
        brokerMessageLogMapper.insert(brokerMessageLog);
        // 发送消息
        rabbitOrderSender.sendOrder(order);
    }

}