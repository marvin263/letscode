package org.apache.activemq.book.ch13;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.book.Const;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;
import java.util.Properties;

public class BatchSending {

    public void setOptimizeAcknowledge() throws JMSException {
        ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory(Const.BROKER_URL);
        cf.setOptimizeAcknowledge(true);
    }

    public void setSessionAsyncOff() throws JMSException {
        ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory(Const.BROKER_URL);
        cf.setAlwaysSessionAsync(false);
    }

    public void setNoCopyOnSend() throws JMSException {
        ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory(Const.BROKER_URL);
        cf.setAlwaysSyncSend(true);
    }

    public void setAsyncSend() throws JMSException {
        ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory(Const.BROKER_URL);
        cf.setCopyMessageOnSend(false);
    }

    public void setDeliveryMode() throws JMSException {
        ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory(Const.BROKER_URL);
        Connection connection = cf.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Topic topic = session.createTopic("Test.Transactions");
        MessageProducer producer = session.createProducer(topic);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
    }

    /**
     * 事务的session 上发送消息
     */
    public void sendTransacted() throws JMSException {
        ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory(Const.BROKER_URL);
        Connection connection = cf.createConnection();
        connection.start();
        // 1. conn->session 
        Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
        // 2. session->topic
        Topic topic = session.createTopic("Test.Transactions");
        // 2. session->Producer
        MessageProducer producer = session.createProducer(topic);
        // 2. session->msg
        for (int i = 0; i < 1000; i++) {
            Message message = session.createTextMessage("message " + i);
            // 3. Producer.send(msg)
            producer.send(message);

            if (i != 0 && i % 10 == 0) {
                session.commit();
            }
        }
        session.commit();
    }

    public void setPrefetchProperties() throws JMSException {
        ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory(Const.BROKER_URL);
        Properties props = new Properties();
        props.setProperty("prefetchPolicy.queuePrefetch", "1000");
        props.setProperty("prefetchPolicy.queueBrowserPrefetch", "500");
        props.setProperty("prefetchPolicy.topicPrefetch", "60000");
        props.setProperty("prefetchPolicy.durableTopicPrefetch", "100");
        cf.setProperties(props);

    }


    /**
     * 非事务性的session 上发送消息
     */
    public void sendNonTransacted() throws JMSException {
        ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory(Const.BROKER_URL);
        Connection connection = cf.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Topic topic = session.createTopic("Test.Transactions");
        MessageProducer producer = session.createProducer(topic);
        for (int i = 0; i < 1000; i++) {
            Message message = session.createTextMessage("message " + i);
            producer.send(message);
        }
    }

    public static void main(String[] args) throws Exception {
        BatchSending bs = new BatchSending();
        bs.sendTransacted();
    }
}
