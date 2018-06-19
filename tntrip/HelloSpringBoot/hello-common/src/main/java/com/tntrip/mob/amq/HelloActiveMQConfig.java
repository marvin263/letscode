package com.tntrip.mob.amq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.stereotype.Service;

import javax.jms.ConnectionFactory;
import javax.jms.Session;

@Service
public class HelloActiveMQConfig {
    @Value("${spring.activemq.brokerUrl}")
    private String brokerUrl;
    private static final long ONE_DAY_IN_MILLIS = 24 * 60 * 60 * 1000L;

    @Bean
    public DefaultMessageListenerContainer testqueue(ConnectionFactory cf) {
        DefaultMessageListenerContainer factory = new DefaultMessageListenerContainer();
        factory.setConnectionFactory(cf);
        factory.setDestination(new ActiveMQQueue("test.a.b.c.myqueue"));
        factory.setMessageListener(new ReceiveQueueMessage());
        factory.setSubscriptionDurable(true);
        //factory.setClientId("aa");
        return factory;
    }

    @Bean
    public DefaultMessageListenerContainer createMessageListenerContainer(ConnectionFactory cf) {
        DefaultMessageListenerContainer factory = new DefaultMessageListenerContainer();
        factory.setConnectionFactory(cf);
        factory.setDestination(new ActiveMQTopic("test.a.b.c.mytopic"));
        factory.setMessageListener(new ReceiveTopicMessage());
        factory.setSubscriptionDurable(true);
        return factory;
    }


    @Bean
    public CachingConnectionFactory getCachingConnectionFactory() {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(new ActiveMQConnectionFactory(brokerUrl));
        cachingConnectionFactory.setSessionCacheSize(1);
        cachingConnectionFactory.setClientId("clientId_test.a.b.c.mytopic");
        return cachingConnectionFactory;
    }

    @Bean("queueJmsTemplate")
    public JmsTemplate getQueueJmsTemplate(CachingConnectionFactory ccf) {
        JmsTemplate queueJmsTemplate = new JmsTemplate();
        queueJmsTemplate.setConnectionFactory(ccf);
        queueJmsTemplate.setDeliveryPersistent(true);
        queueJmsTemplate.setPubSubDomain(false);
        queueJmsTemplate.setSessionAcknowledgeMode(Session.AUTO_ACKNOWLEDGE);
        queueJmsTemplate.setExplicitQosEnabled(true);
        queueJmsTemplate.setTimeToLive(ONE_DAY_IN_MILLIS);
        return queueJmsTemplate;
    }


    @Bean("topicJmsTemplate")
    public JmsTemplate getTopicJmsTemplate(CachingConnectionFactory ccf) {
        JmsTemplate topicJmsTemplate = new JmsTemplate();
        topicJmsTemplate.setConnectionFactory(ccf);
        topicJmsTemplate.setDeliveryPersistent(true);
        topicJmsTemplate.setPubSubDomain(true);
        topicJmsTemplate.setSessionAcknowledgeMode(Session.AUTO_ACKNOWLEDGE);
        topicJmsTemplate.setExplicitQosEnabled(true);
        topicJmsTemplate.setTimeToLive(ONE_DAY_IN_MILLIS);
        return topicJmsTemplate;
    }
}
