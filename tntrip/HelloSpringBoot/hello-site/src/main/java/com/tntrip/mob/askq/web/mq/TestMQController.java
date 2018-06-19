package com.tntrip.mob.askq.web.mq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

@RestController
public class TestMQController {
    @Autowired
    private JmsTemplate queueJmsTemplate;

    @Autowired
    private JmsTemplate topicJmsTemplate;

    @RequestMapping(value = "api/send", method = RequestMethod.GET)
    public String send() {
        queueJmsTemplate.send("test.a.b.c.myqueue", new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage("qqqqq");
            }
        });

        topicJmsTemplate.send("test.a.b.c.mytopic", new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage("tttttt");
            }
        });
        
        queueJmsTemplate.send("test.notconsume.a.b.c.myqueue", new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage("qqqqq");
            }
        });

        topicJmsTemplate.send("test.notconsume.a.b.c.mytopic", new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage("tttttt");
            }
        });
        return "";
    }

}
