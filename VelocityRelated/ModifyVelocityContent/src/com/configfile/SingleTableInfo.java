package com.configfile;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.io.FileUtil;

public class SingleTableInfo {
	// Column name's separator in Tables.xml
	public static final String COLUMN_SEPARATOR_COMMA = ",";

	public String tableName;
	public String primaryKeys;
	public String velocityColumns;

	public String[] getArrayVelocityColumns() {
		return velocityColumns.split(COLUMN_SEPARATOR_COMMA);
	}

	public String[] getArrayPrimaryKeys() {
		return primaryKeys.split(COLUMN_SEPARATOR_COMMA);
	}

	public String assembleSelectSql() {
		StringBuffer sb = new StringBuffer();
		sb.append("select ");
		sb.append(primaryKeys);
		sb.append(COLUMN_SEPARATOR_COMMA);
		sb.append(velocityColumns);
		sb.append(" from ");
		sb.append(tableName);
		return sb.toString();
	}

	public static ArrayList<SingleTableInfo> getAllSingleTableInfo() {
		ArrayList<SingleTableInfo> list = new ArrayList<SingleTableInfo>();
		Document doc = FileUtil.getDocument(getConfigFullPath());
		// All <Table></Table> elements
		NodeList tableNodeList = doc.getElementsByTagName("Table");
		for (int i = 0; i < tableNodeList.getLength(); i++) {
			Element eleTable = (Element) tableNodeList.item(i);
			// table name
			String tableName = eleTable.getAttribute("name");
			SingleTableInfo stInfo = new SingleTableInfo();
			list.add(stInfo);
			stInfo.tableName = tableName;
			// <Table></Table>'s all children node list, including text node
			NodeList tableChildrenList = eleTable.getChildNodes();
			for (int m = 0; m < tableChildrenList.getLength(); m++) {
				// <Table></Table>'s each child, including text node
				Node oneNode = tableChildrenList.item(m);
				if (oneNode.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				Element ele = (Element) oneNode;
				if ("PrimaryKeys".equals(ele.getTagName())) {
					stInfo.primaryKeys = ele.getAttribute("names");
				} else if ("VelocityColumns".equals(ele.getTagName())) {
					stInfo.velocityColumns = ele.getAttribute("names");
				}
			}
		}
		return list;
	}

	private static String getConfigFullPath() {
		String fullPath = GetConfigFileFolder.getConfigFileFolder()
				+ "/Tables.xml";
		return fullPath;
	}

	public static void main(String[] args) {
		ArrayList list = getAllSingleTableInfo();
		System.out.println(list);
	}
}
