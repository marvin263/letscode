package com.configfile;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.io.FileUtil;

public class LoginInfo {
	public String driverClassName;
	public String url;
	public String username;
	public String password;

	public static LoginInfo getLoginInfo() {
		LoginInfo loginInfo = new LoginInfo();
		Document doc = FileUtil.getDocument(getConfigFullPath());
		// All <property> elements
		NodeList propNodeList = doc.getElementsByTagName("property");
		for (int i = 0; i < propNodeList.getLength(); i++) {
			// A <property> element
			Element ele = (Element) propNodeList.item(i);
			String name = ele.getAttribute("name");
			String value = ele.getAttribute("value");

			if ("driverClassName".equals(name)) {
				loginInfo.driverClassName = value;
			} else if ("url".equals(name)) {
				loginInfo.url = value;
			} else if ("username".equals(name)) {
				loginInfo.username = value;
			} else if ("password".equals(name)) {
				loginInfo.password = value;
			}
		}
		return loginInfo;
	}

	private static String getConfigFullPath() {
		String fullPath = GetConfigFileFolder.getConfigFileFolder()
				+ "/Login.xml";
		return fullPath;
	}
	public static void main(String[] args) {
		LoginInfo loginInfo = getLoginInfo();
		System.out.println(loginInfo);
	}
}
