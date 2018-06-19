package org.apache.activemq.book.ch13;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.book.Const;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

public class SelectorProduceMessage extends ReadConsoleAndRun {
    private MessageProducer producer;
    private Session session;

    public SelectorProduceMessage() throws Exception {
        String brokerURI = Const.BROKER_URL;
//        String brokerURI = "tcp://localhost:61616";
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerURI);
        Connection connection = connectionFactory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Queue queue = session.createQueue("Selector.MessageInOrder");
        producer = session.createProducer(queue);
    }

    protected Object runWithConsoleInput(String line) {
        try {
            TextMessage textMessage = session.createTextMessage(line);
            if(line.startsWith("car")){
                textMessage.setStringProperty("car", "1");
            }
            producer.send(textMessage);
            System.out.println("Already sent: " + textMessage);
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        ReadConsoleAndRun.startMain(new SelectorProduceMessage());
    }
}
