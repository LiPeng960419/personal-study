package com.coship.producer.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.coship.producer.constant.Constants;
import com.coship.producer.entity.BrokerMessageLog;
import com.coship.producer.entity.Order;
import com.coship.producer.mapper.BrokerMessageLogMapper;
import com.coship.producer.producer.RabbitOrderSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class RetryMessageTask {

    @Autowired
    private RabbitOrderSender rabbitOrderSender;

    @Autowired
    private BrokerMessageLogMapper brokerMessageLogMapper;

    @Scheduled(initialDelay = 2000,fixedDelay = 1000*10)
    public void reSendMessage(){
        log.info("-----RABBITMQ重传定时任务开始-----");
        List<BrokerMessageLog> list = brokerMessageLogMapper.query4StatusAndTimeoutMessage();
        for (int i = 0; i < list.size() ; i++) {
            if (list.get(i).getTryCount() >= 3){
                brokerMessageLogMapper.changeBrokerMessageLogStatus(list.get(i).getMessageId(), Constants.ORDER_SEND_FAILURE, new Date());
            }else {
                brokerMessageLogMapper.update4ReSend(list.get(i).getMessageId(), new Date());
                log.info("消息重传第"+brokerMessageLogMapper.queryBrokerMessageById(list.get(i).getMessageId()).getTryCount()+"次");
                //Order reSender = JSON.parseObject(list.get(i).getMessage(),new TypeReference<Order>(){});
                Order reSender = JSON.parseObject(list.get(i).getMessage(),Order.class);
                try {
                    rabbitOrderSender.sendOrder(reSender);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error(e.getMessage());
                }
            }
        }
    }
}