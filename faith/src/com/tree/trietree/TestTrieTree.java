package com.tree.trietree;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.util.IOUtil;

public class TestTrieTree {
	static Pattern ptn = Pattern.compile("\\w*");

	public static void main(String[] args) throws Exception {
		TrieBranch root = TrieBranch.createBranchNode();
		ArrayList<TrieLeaf> resultList = new ArrayList<TrieLeaf>();

		String[] arrayFullPath = new String[] {
				"/home/marvin/Eden/gitworkspace/letscode/StudyTree/testfile.txt",
				"/home/marvin/testddd/letscode/GoodLuck/src/com/syniverse/goodluckprovider/GoodLuckFrame.java" };

		for (String fullPath : arrayFullPath) {
			BufferedReader br = IOUtil.createBufferedReader(fullPath, null);
			String line = null;
			while ((line = br.readLine()) != null) {
				Matcher m = ptn.matcher(line.toLowerCase());
				while (m.find()) {
					TrieTreeUtil.addEachWord(root, m.group(), resultList);
				}
			}
			IOUtil.closeReader(br);
		}

		Collections.sort(resultList);
		for (Iterator<TrieLeaf> it = resultList.iterator(); it.hasNext();) {
			TrieLeaf tl = (TrieLeaf) it.next();
			System.out.println(tl);
		}
	}
}
