package org.apache.activemq.book.ch13;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.book.Const;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;

public class JustConsumeMessage extends ReadConsoleAndRun {
    private MessageConsumer consumer;
    private Session session;

    public JustConsumeMessage() throws Exception {
        String brokerURI = Const.BROKER_URL;
//        String brokerURI = "tcp://192.168.0.108:61618";
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerURI);
        Connection connection = connectionFactory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        //Queue queue = session.createQueue("mybrokerqueue");
        Topic queue = session.createTopic("foo.bar");
        consumer = session.createConsumer(queue);
        System.out.println(brokerURI);
    }
    
    @Override
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
        ReadConsoleAndRun.startMain(new JustConsumeMessage());
    }
}
