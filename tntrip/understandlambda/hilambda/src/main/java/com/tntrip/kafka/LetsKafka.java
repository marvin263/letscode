package com.tntrip.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class LetsKafka {
    private final KafkaProducer<String, String> producer;
    public static final String BOOTSTRAP_SERVERS_204 = "192.168.86.204:9092,192.168.86.204:9093,192.168.86.204:9094";
    public static final String BOOTSTRAP_SERVERS_205 = "192.168.86.205:9092,192.168.86.205:9093,192.168.86.205:9094";

    private LetsKafka(String svrs) {
        Properties props = new Properties();
        props.put("bootstrap.servers", svrs);//xxx服务器ip
        props.put("acks", "all");//所有follower都响应了才认为消息提交成功，即"committed"
        props.put("retries", 0);//retries = MAX 无限重试，直到你意识到出现了问题:)
        props.put("batch.size", 10);//producer将试图批处理消息记录，以减少请求次数.默认的批量处理消息字节数
        //batch.size当批量的数据大小达到设定值后，就会立即发送，不顾下面的linger.ms
        props.put("linger.ms", 1);//延迟1ms发送，这项设置将通过增加小的延迟来完成--即，不是立即发送一条记录，producer将会等待给定的延迟时间以允许其他消息记录发送，这些消息记录可以批量处理
        props.put("buffer.memory", 33554432);//producer可以用来缓存数据的内存大小。
        props.put("key.serializer",
                "org.apache.kafka.common.serialization.IntegerSerializer");
        props.put("value.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");

        producer = new KafkaProducer<>(props);
    }

    public void produce(String topicName) {
        int messageNo = 1;
        final int COUNT = 100000000;

        while (messageNo < COUNT) {
            String key = String.valueOf(messageNo);
            String data = String.format(topicName + " hello KafkaProducer message %s from hubo 06291018 ", key);

            try {
                System.out.println(data);
                producer.send(new ProducerRecord<>(topicName, data));
            } catch (Exception e) {
                e.printStackTrace();
            }

            messageNo++;
        }

        producer.close();
    }

    public static void main(String[] args) {
        new Thread(() -> new LetsKafka(BOOTSTRAP_SERVERS_205).produce("test_2055")).start();
       new Thread(() -> new LetsKafka(BOOTSTRAP_SERVERS_204).produce("test_204")).start();

    }
}