/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package com.tntrip.tidyfile;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;

/**
 * Example how to use multipart/form encoded POST request.
 */
public class ClientMultipartFormPost {

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            //System.out.println("File path not given");
            //System.exit(1);
        }
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpPost httppost = new HttpPost("http://192.168.0.105:5000/webapi/entry.cgi?api=SYNO.FileStation.Upload&method=upload&version=2");
            
            httppost.addHeader("Cookie","id=eNfL.iMPUCwX.1780NNN555507");
            
            
            FileBody fileBody = new FileBody(new File("C:\\Users\\libing\\Desktop\\1-160P4230Q1311.jpg"), ContentType.DEFAULT_BINARY, "1-160P4230Q1311.jpg");

            HttpEntity reqEntity = MultipartEntityBuilder.create()
                    .addPart("overwrite", new StringBody("false", ContentType.TEXT_PLAIN))
                    .addPart("path", new StringBody("/video/v15/v124", ContentType.TEXT_PLAIN))
                    .addPart("create_parents", new StringBody("true", ContentType.TEXT_PLAIN))
                    .addPart("filename", fileBody)
                    .setLaxMode()
                    .build();

            // 牛逼大发了，发送multipart请求。棒棒棒
            httppost.setEntity(reqEntity);

            System.out.println("executing request " + httppost.getRequestLine());
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                System.out.println("----------------------------------------");
                System.out.println(response.getStatusLine());
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    System.out.println("Response content length: " + resEntity.getContentLength());
                    System.out.println(EntityUtils.toString(resEntity));
                }
                EntityUtils.consume(resEntity);
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
    }
}
