package org.apache.activemq.book.ch13;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSession;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

public class TestMyMessageListener {

    private static String brokerURL = "tcp://localhost:61616";
    private static transient ConnectionFactory factory;
    private transient Connection connection;
    private transient Session session;
    private transient Session session2;

    private String jobs[] = new String[]{"marvin.messagelistener"};

    public TestMyMessageListener() throws JMSException {
        factory = new ActiveMQConnectionFactory(brokerURL);
        connection = factory.createConnection();
        ((ActiveMQConnection)connection).setAlwaysSessionAsync(false);
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        session2 = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
//        ((ActiveMQSession)session).setSessionAsyncDispatch(false);
//        ((ActiveMQSession)session2).setSessionAsyncDispatch(false);
    }

    public void close() throws JMSException {
        if (connection != null) {
            connection.close();
        }
    }

    public static void main(String[] args) throws JMSException {
        TestMyMessageListener consumer = new TestMyMessageListener();
        for (String job : consumer.jobs) {
            Destination destination = consumer.getSession().createQueue("JOBS." + job);
            MessageConsumer messageConsumer = consumer.getSession().createConsumer(destination);
            messageConsumer.setMessageListener(new MyMessageListener(job));
            
            MessageConsumer messageConsumer2 = consumer.getSession2().createConsumer(destination);
            messageConsumer2.setMessageListener(new MyMessageListener(job));
        }
    }

    public Session getSession() {
        return session;
    }
    public Session getSession2() {
        return session2;
    }


}
