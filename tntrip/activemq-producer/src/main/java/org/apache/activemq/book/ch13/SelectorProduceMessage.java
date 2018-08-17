package org.apache.activemq.book.ch13;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.book.Const;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.connection.SingleConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.ArrayList;
import java.util.List;

public class SelectorProduceMessage extends ReadConsoleAndRun {
    private MessageProducer producer;
    private Session session;

    public SelectorProduceMessage() throws Exception {
        String brokerURI = Const.BROKER_URL;
//        String brokerURI = "tcp://localhost:61616";
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerURI);
        Connection connection = connectionFactory.createConnection();

        SingleConnectionFactory ss = new SingleConnectionFactory(connectionFactory);
        //ss.setSessionCacheSize(3);
        
        Connection ddddd = ss.createConnection();
        Connection ddddd2 = ss.createConnection();
        System.out.println(ddddd2);
        ddddd.start();
        List<Session> lllllll = new ArrayList<>();
        lllllll.add(ddddd.createSession(false, session.AUTO_ACKNOWLEDGE));
        lllllll.add(ddddd.createSession(false, session.AUTO_ACKNOWLEDGE));
        lllllll.get(0).close();
        lllllll.get(1).close();
        for (int i = 0; i < 10; i++) {
            lllllll.add(ddddd.createSession(false, session.AUTO_ACKNOWLEDGE));
        }
        System.out.println(ddddd);
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
