package com.tntrip.playes;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.net.InetAddress;
import java.util.Date;


/**
 * Created by nuc on 2016/9/22.
 */
public class HiES {
    public void fdfd() throws Exception {
        Settings settings = Settings.settingsBuilder()
                .put("cluster.name", "myClusterName").build();

        TransportClient client = TransportClient.builder().build()
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("192.168.19.133"), 9300));


        IndexResponse response = client.prepareIndex("twitter", "tweet", "50")
                .setSource(XContentFactory.jsonBuilder()
                        .startObject()
                        .field("user", "kimchy")
                        .field("postDate", new Date())
                        .field("message", "trying out Elasticsearch")
                        .endObject()
                )
                .get();
        System.out.println(response.getVersion());

    }

    public static void main(String[] args) throws Exception {
        new HiES().fdfd();
    }
}
