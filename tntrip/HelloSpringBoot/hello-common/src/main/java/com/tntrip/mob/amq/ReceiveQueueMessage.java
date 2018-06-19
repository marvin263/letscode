package com.tntrip.mob.amq;

import javax.jms.Message;
import javax.jms.MessageListener;

public class ReceiveQueueMessage implements MessageListener {
    @Override
    public void onMessage(Message message) {
        System.out.println("queue" + message);
    }
}
