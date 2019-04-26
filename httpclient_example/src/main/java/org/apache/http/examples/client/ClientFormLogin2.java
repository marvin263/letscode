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
package org.apache.http.examples.client;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * This example demonstrates how HttpClient can be used to perform form-based
 * logon.
 * 
 * A example that demonstrates how HttpClient APIs can be used to perform
 * form-based logon.
 */
public class ClientFormLogin2 {
	public static final String user = "SoftwareEngineer";
	public static final String pwd = "aaaa";
	public static final String URL_LOGIN = "https://passport.jd.com/new/login.aspx";
	public static final String User_Agent = "Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:37.0) Gecko/20100101 Firefox/37.0";

	public static final String POST_LOGIN = "https://passport.jd.com/uc/loginService?";
	public static final String ORDER = "http://order.jd.com/center/list.action";
	public static final String IMAGE = "https://authcode.jd.com/verify/image?a=1";
	public static final String POST_AUTH = "https://passport.jd.com/uc/showAuthCode?r=0.5168508280235723&version=2015";

	public static final String CKIMAGE_FOLDER = "/home/marvin/Desktop/monitor/";

	private static String buildPostLoginURL(String uuid) {
		StringBuilder sb = new StringBuilder(POST_LOGIN);
		sb.append("uuid=" + uuid);
		sb.append("&&r=0.7920307175038792&version=2015");
		System.out.println(sb.toString());
		return sb.toString();
	}

	private static String buildImageURL(String uuid) {
		StringBuilder sb = new StringBuilder(IMAGE);
		sb.append("&acid=" + uuid);
		sb.append("&uid=" + uuid);
		sb.append("&yys=" + System.currentTimeMillis());
		System.out.println(sb.toString());
		return sb.toString();
	}

	/**
	 * @param loginContent
	 * @return
	 */
	private static Map<String, String> postParameters(String loginContent) {
		Map<String, String> kv = new HashMap<String, String>();
		kv.put("authcode", "");
		kv.put("chkRememberMe", "on");
		kv.put("loginname", user);
		kv.put("loginpwd", pwd);
		kv.put("machineCpu", "");
		kv.put("machineDisk", "");
		kv.put("machineNet", "");
		kv.put("nloginpwd", pwd);
		kv.put("uuid", extractUUID(loginContent));
		String[] rdm = extractRandom(loginContent);
		kv.put(rdm[0], rdm[1]);
		return kv;
	}

	private static String extractUUID(String loginContent) {
		String uuidField = "<input type=\"hidden\" id=\"uuid\" name=\"uuid\" value=\"";
		int start = loginContent.indexOf(uuidField) + uuidField.length();
		int end = loginContent.indexOf("\"", start);
		String uuid = loginContent.substring(start, end);
		System.out.println("uuid=" + uuid);
		return uuid;
	}

	private static String extractImage(String newContent) {
		String uuidField = "src2=";
		int start = newContent.indexOf(uuidField) + uuidField.length();

		int dq_1 = newContent.indexOf("\"", start);
		int dq_2 = newContent.indexOf("\"", dq_1 + 1);

		String uuid = newContent.substring(dq_1 + 1, dq_2);
		System.out.println("src2=" + uuid);
		return uuid;
	}

	private static String[] extractRandom(String loginContent) {
		String rdmAbove = "<input type=\"hidden\" name=\"machineDisk\" id=\"machineDisk\" value=\"\" class=\"hide\"/>";
		int start = loginContent.indexOf(rdmAbove) + rdmAbove.length();

		String rdmHeader = "<input type=\"hidden\" name";
		int nameBegin = loginContent.indexOf(rdmHeader, start)
				+ rdmHeader.length();

		int dq_1 = loginContent.indexOf("\"", nameBegin);
		int dq_2 = loginContent.indexOf("\"", dq_1 + 1);
		int dq_3 = loginContent.indexOf("\"", dq_2 + 1);
		int dq_4 = loginContent.indexOf("\"", dq_3 + 1);

		String rdm0 = loginContent.substring(dq_1 + 1, dq_2);
		String rdm1 = loginContent.substring(dq_3 + 1, dq_4);
		System.out.println(rdm0 + "=" + rdm1);
		return new String[] { rdm0, rdm1 };
	}

	public static void main(String[] args) throws Exception {
		BasicCookieStore cookieStore = new BasicCookieStore();
		CloseableHttpClient httpclient = HttpClients.custom()
				.setDefaultCookieStore(cookieStore).build();

		String firstLoginContent = "";
		try {

			firstLoginContent = firstLogin(cookieStore, httpclient);

			boolean need = needAuthImage(cookieStore, httpclient);
			if (need == true) {
				writeCheckImage(cookieStore, httpclient, firstLoginContent);
				System.err.println("PLEASE CHAGNE FILE, QUICKLY");
				Thread.sleep(25 * 1000);
			}

			String realLogin = doRealLogin(cookieStore, firstLoginContent,
					httpclient, need);

			fetchOrder(cookieStore, httpclient);

		} finally {
			httpclient.close();
		}
	}

	private static void fetchOrder(BasicCookieStore cookieStore,
			CloseableHttpClient httpclient) throws IOException,
			ClientProtocolException {
		HttpGet httpget3 = new HttpGet(ORDER);
		httpget3.addHeader("User-Agent", User_Agent);
		CloseableHttpResponse response3 = httpclient.execute(httpget3);
		try {
			HttpEntity entity = response3.getEntity();

			System.out.println("ORDER get: " + response3.getStatusLine());
			// EntityUtils.consume(entity);
			String orderContent = EntityUtils.toString(entity);
			System.out.println(orderContent);
			System.out.println("Order cookies:");
			List<Cookie> cookies = cookieStore.getCookies();
			if (cookies.isEmpty()) {
				System.out.println("None");
			} else {
				for (int i = 0; i < cookies.size(); i++) {
					System.out.println("- " + cookies.get(i).toString());
				}
			}
		} finally {
			response3.close();
		}
	}

	private static void writeCheckImage(BasicCookieStore cookieStore,
			CloseableHttpClient httpclient, String firstLoginContent)
			throws IOException, ClientProtocolException, FileNotFoundException {

		HttpGet httpget4 = new HttpGet(
				buildImageURL(extractUUID(firstLoginContent)));
		httpget4.addHeader("User-Agent", User_Agent);
		httpget4.addHeader("Content-Type", "image/jpeg");
		httpget4.addHeader("Accept", "image/png,image/*;q=0.8,*/*;q=0.5");
		httpget4.addHeader("Accept-Encoding", "gzip, deflate");
		httpget4.addHeader("Accept-Language", "en-US,en;q=0.5");
		httpget4.addHeader("Connection", "keep-alive");
		httpget4.addHeader("Referer", "https://passport.jd.com/new/login.aspx");

		CloseableHttpResponse response4 = httpclient.execute(httpget4);
		try {
			HttpEntity entity = response4.getEntity();
			FileOutputStream fis = new FileOutputStream(CKIMAGE_FOLDER
					+ "jd.jpg");
			entity.writeTo(fis);
			fis.close();
			System.out.println("IMAGE get: " + response4.getStatusLine());
			System.out.println("Image cookies:");
			List<Cookie> cookies = cookieStore.getCookies();
			if (cookies.isEmpty()) {
				System.out.println("None");
			} else {
				for (int i = 0; i < cookies.size(); i++) {
					System.out.println("- " + cookies.get(i).toString());
				}
			}
		} finally {
			response4.close();
		}
	}

	private static boolean needAuthImage(BasicCookieStore cookieStore,
			CloseableHttpClient httpclient) throws URISyntaxException,
			IOException, ClientProtocolException {
		RequestBuilder rb = RequestBuilder.post().setUri(new URI(POST_AUTH));

		Map<String, String> kv = new HashMap<String, String>();
		kv.put("loginName", user);
		kv.put("User-Agent", User_Agent);
		kv.put("Content-Type",
				"application/x-www-form-urlencoded; charset=utf-8");
		kv.put("Cache-Control", "no-cache");
		kv.put("Pragma", "no-cache");
		kv.put("Referer", "https://passport.jd.com/new/login.aspx");
		kv.put("X-Requested-With", "XMLHttpRequest");
		Iterator<Entry<String, String>> it = kv.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> entry = it.next();
			rb.addParameter(entry.getKey(), entry.getValue());
		}
		String auth = "";
		HttpUriRequest authPostRQ = rb.build();
		CloseableHttpResponse response6 = httpclient.execute(authPostRQ);
		try {
			HttpEntity entity = response6.getEntity();

			System.out.println("Auth get: " + response6.getStatusLine());
			auth = EntityUtils.toString(entity);
			System.out.println("Auth:\r\n" + auth);

			System.out.println("Auth cookies:");
			List<Cookie> cookies = cookieStore.getCookies();
			if (cookies.isEmpty()) {
				System.out.println("None");
			} else {
				for (int i = 0; i < cookies.size(); i++) {
					System.out.println("- " + cookies.get(i).toString());
				}
			}
		} finally {
			response6.close();
		}
		return auth.indexOf("true") >= 0;
	}

	private static String firstLogin(BasicCookieStore cookieStore,
			CloseableHttpClient httpclient) throws IOException,
			ClientProtocolException {
		String loginContent;
		HttpGet httpget = new HttpGet(URL_LOGIN);
		httpget.addHeader("User-Agent", User_Agent);
		httpget.addHeader("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		httpget.addHeader("Accept-Encoding", "gzip, deflate");
		httpget.addHeader("Accept-Language", "en-US,en;q=0.5");
		httpget.addHeader("Connection", "keep-alive");
		CloseableHttpResponse response1 = httpclient.execute(httpget);
		try {
			HttpEntity entity = response1.getEntity();

			System.out.println("First Login form get: "
					+ response1.getStatusLine());
			loginContent = EntityUtils.toString(entity);
			System.out.println("Initial set of cookies:");
			List<Cookie> cookies = cookieStore.getCookies();
			if (cookies.isEmpty()) {
				System.out.println("None");
			} else {
				for (int i = 0; i < cookies.size(); i++) {
					System.out.println("- " + cookies.get(i).toString());
				}
			}
		} finally {
			response1.close();
		}
		return loginContent;
	}

	private static String doRealLogin(BasicCookieStore cookieStore,
			String loginContent, CloseableHttpClient httpclient, boolean need)
			throws URISyntaxException, IOException, ClientProtocolException {

		String passport = "";
		RequestBuilder rb = RequestBuilder.post().setUri(
				new URI(buildPostLoginURL(extractUUID(loginContent))));

		Map<String, String> kv = postParameters(loginContent);
		if (need == true) {
			kv.put("authcode",
					IOUtil.readContent(CKIMAGE_FOLDER + "ckimage.txt"));
		}
		Iterator<Entry<String, String>> it = kv.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> entry = it.next();
			rb.addParameter(entry.getKey(), entry.getValue());
		}
		HttpUriRequest login = rb.build();
		login.addHeader("User-Agent", User_Agent);
		login.addHeader("Referer", "https://passport.jd.com/new/login.aspx");
		login.addHeader("X-Requested-With", "XMLHttpRequest");
		login.addHeader("Pragma", "no-cache");
		login.addHeader("Content-Type",
				"application/x-www-form-urlencoded; charset=utf-8");
		login.addHeader("Connection", "keep-alive");
		login.addHeader("Accept-Encoding", "gzip, deflate");
		login.addHeader("Cache-Control", "no-cache");
		login.addHeader("Accept", "text/plain, */*; q=0.01");
		login.addHeader("Accept-Language", "en-US,en;q=0.5");
		login.addHeader("Pragma", "no-cache");

		CloseableHttpResponse response2 = httpclient.execute(login);
		try {
			HttpEntity entity = response2.getEntity();

			System.out.println("Login form get: " + response2.getStatusLine());
			passport = EntityUtils.toString(entity);
			System.out.println("passport:\r\n" + passport);

			System.out.println("Post logon cookies:");
			List<Cookie> cookies = cookieStore.getCookies();
			if (cookies.isEmpty()) {
				System.out.println("None");
			} else {
				for (int i = 0; i < cookies.size(); i++) {
					System.out.println("- " + cookies.get(i).toString());
				}
			}
		} finally {
			response2.close();
		}
		return passport;
	}
}
