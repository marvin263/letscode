package org.apache.activemq.book.ch13;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.book.Const;

import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueRequestor;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.Date;

public class EmbeddedService {

    public void service() throws Exception {
        ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory("tcp://localhost:61616");
        Connection connection = cf.createConnection();
        connection.start();
        
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue("Test.testQueue");
        
        final MessageProducer producer = session.createProducer(queue);

        TextMessage txtMsg = session.createTextMessage("Producer msg is here--" + Const.date2String(new Date(), Const.YYYY_MM_DD_HH_MM_SS));
        producer.send(txtMsg);
        System.out.println("txtMsg been sent...");
        
        Thread.sleep(2000);
        MessageConsumer consumer = session.createConsumer(queue);
        Message rcved = consumer.receive();
        System.out.println(rcved);
//        MessageConsumer consumer = session.createConsumer(queue);
//        consumer.setMessageListener(new MessageListener() {
//            @Override
//            public void onMessage(Message msg) {
//                try {
//                    TextMessage textMsg = (TextMessage)msg;
//                    String payload = "REPLY: " + textMsg.getText();
//                    Destination replyTo;
//                    replyTo = msg.getJMSReplyTo();
//                    textMsg.clearBody();
//                    textMsg.setText(payload);
//                    producer.send(replyTo, textMsg);
//                } catch (JMSException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            }
//        });

    }

    public void requestor() throws Exception {
        ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory("tcp://localhost:61616");
        QueueConnection connection = cf.createQueueConnection();
        connection.start();
        QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue("service.queue");
        QueueRequestor requestor = new QueueRequestor(session, queue);
        for (int i = 0; i < 10; i++) {
            TextMessage msg = session.createTextMessage("test msg: " + i);
            TextMessage result = (TextMessage) requestor.request(msg);
            System.err.println("Result = " + result.getText());
        }
    }

    public static void main(String[] args) throws Exception {
        EmbeddedService service = new EmbeddedService();
        service.service();
        //service.requestor();
        Thread.sleep(5000);
    }
}
