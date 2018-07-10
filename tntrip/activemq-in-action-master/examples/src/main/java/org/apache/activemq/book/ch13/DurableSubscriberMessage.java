package org.apache.activemq.book.ch13;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.book.Const;
import org.apache.activemq.command.ActiveMQTopic;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;

public class DurableSubscriberMessage extends ReadConsoleAndRun {
    private MessageConsumer consumer;
    private Session session;

    public DurableSubscriberMessage() throws Exception {
        //String brokerURI = Const.BROKER_URL;
        String brokerURI = "tcp://localhost:61616";

        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerURI);
        Connection conn1 = connectionFactory.createConnection();
        conn1.setClientID("clientID_1");

        Session session1 = conn1.createSession(false, Session.AUTO_ACKNOWLEDGE);
        ActiveMQTopic topic1 = new ActiveMQTopic("topic_1");

        TopicSubscriber subscriber1 = session1.createDurableSubscriber(topic1, "durableSubscription_aa");
        TopicSubscriber subscriber11 = session1.createDurableSubscriber(topic1, "durableSubscription_bb");
        //TopicSubscriber subscriber111 = session1.createDurableSubscriber(topic1, "durableSubscription_bb");
        MessageConsumer subscriber1111 = session1.createConsumer(topic1);



        Connection conn2 = connectionFactory.createConnection();
        conn2.setClientID("clientID_2");
        Session session2 = conn2.createSession(false, Session.AUTO_ACKNOWLEDGE);
        ActiveMQTopic topic2 = new ActiveMQTopic("topic_2");
        TopicSubscriber subscriber2 = session2.createDurableSubscriber(topic2, "durableSubscription_aa");
        TopicSubscriber subscriber22 = session2.createDurableSubscriber(topic2, "durableSubscription_bb");
        //TopicSubscriber subscriber222 = session2.createDurableSubscriber(topic2, "durableSubscription_bb");
        MessageConsumer subscriber2222 = session2.createConsumer(topic2);





        conn1.start();
        conn2.start();


        Queue queue = session1.createQueue("Selector.MessageInOrder");
        consumer = session1.createConsumer(queue, "car='1'");
    }

    protected Object runWithConsoleInput(String line) {
        try {
            Message msg = consumer.receive();
            return msg;
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        ReadConsoleAndRun.startMain(new DurableSubscriberMessage());
    }
}
