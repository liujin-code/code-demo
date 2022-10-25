package com.geekhalo.lego.msg.consumer;

import com.alibaba.fastjson.JSON;
import com.geekhalo.lego.DemoApplication;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by taoli on 2022/10/21.
 * gitee : https://gitee.com/litao851025/lego
 * 编程就像玩 Lego
 */
@SpringBootTest(classes = DemoApplication.class)
@Slf4j
class UserMessageConsumerTest {
    @Autowired
    private UserMessageConsumer userMessageConsumer;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    private List<Long> userIds;

    @BeforeEach
    void setUp() throws InterruptedException {
        this.userMessageConsumer.clean();

        this.userIds = new ArrayList<>();
        for (int i = 0; i< 100; i++){
            userIds.add(10000L + i);
        }
        this.userIds.forEach(userId -> sendMessage(userId));

        TimeUnit.SECONDS.sleep(3);
    }

    private void sendMessage(Long userId) {
        String topic = "consumer-test-topic";
        {
            String tag = "UserCreatedEvent";
            UserEvents.UserCreatedEvent userCreatedEvent = new UserEvents.UserCreatedEvent();
            userCreatedEvent.setUserId(userId);
            userCreatedEvent.setUserName("Name-" + userId);

            sendOrderlyMessage(topic, tag, userCreatedEvent);
        }

        {
            String tag = "UserEnableEvent";
            UserEvents.UserEnableEvent userEnableEvent = new UserEvents.UserEnableEvent();
            userEnableEvent.setUserId(userId);
            userEnableEvent.setUserName("Name-" + userId);

            sendOrderlyMessage(topic, tag, userEnableEvent);
        }

        {
            String tag = "UserDisableEvent";
            UserEvents.UserDisableEvent userDisableEvent = new UserEvents.UserDisableEvent();
            userDisableEvent.setUserId(userId);
            userDisableEvent.setUserName("Name-" + userId);

            sendOrderlyMessage(topic, tag, userDisableEvent);
        }

        {
            String tag = "UserDeletedEvent";
            UserEvents.UserDeletedEvent userDeletedEvent = new UserEvents.UserDeletedEvent();
            userDeletedEvent.setUserId(userId);
            userDeletedEvent.setUserName("Name-" + userId);


            sendOrderlyMessage(topic, tag, userDeletedEvent);
        }
    }

    private void sendOrderlyMessage(String topic, String tag, UserEvents.UserEvent event) {
        String shardingKey = String.valueOf(event.getUserId());
        String json = JSON.toJSONString(event);
        Message<String> msg = MessageBuilder
                .withPayload(json)
                .build();
        String destination = createDestination(topic, tag);
        SendResult sendResult = this.rocketMQTemplate.syncSendOrderly(destination, msg, shardingKey, 2000);
        log.info("Send result is {} for msg", sendResult, msg);
    }

    protected String createDestination(String topic, String tag) {
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(tag)){
            return topic + ":" + tag;
        }else {
            return topic;
        }
    }

    @AfterEach
    void tearDown() {

    }

    @Test
    void getUserEvents() {
        this.userIds.forEach(userId ->{
            List<UserEvents.UserEvent> userEvents = this.userMessageConsumer.getUserEvents(userId);
            Assertions.assertEquals(4, userEvents.size());

            Assertions.assertTrue(userEvents.get(0) instanceof UserEvents.UserCreatedEvent);
            Assertions.assertTrue(userEvents.get(1) instanceof UserEvents.UserEnableEvent);
            Assertions.assertTrue(userEvents.get(2) instanceof UserEvents.UserDisableEvent);
            Assertions.assertTrue(userEvents.get(3) instanceof UserEvents.UserDeletedEvent);
        });
    }
}