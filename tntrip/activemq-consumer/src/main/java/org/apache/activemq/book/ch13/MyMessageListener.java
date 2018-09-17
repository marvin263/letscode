package org.apache.activemq.book.ch13;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

public class MyMessageListener implements MessageListener {

	private String job;
	
	public MyMessageListener(String job) {
		this.job = job;
	}

	@Override
	public void onMessage(Message message) {
		try {
			//do something here
			System.out.println(job + " id:" + ((TextMessage)message).getText());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
