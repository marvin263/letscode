package com.tree.trietree;

public class TrieLeaf implements TrieNode, Comparable<TrieLeaf> {
	public String word;
	public int occurrence;
	public int consumedIndex;

	private TrieLeaf() {
		this.occurrence = 0;
		this.consumedIndex = 0;
	}

	public static TrieLeaf createLeafNode() {
		return new TrieLeaf();
	}

	@Override
	public String toString() {
		return "TrieLeaf [word=" + word + ", occurrence=" + occurrence
				+ ", consumedIndex=" + consumedIndex + "]";
	}

	@Override
	public int compareTo(TrieLeaf another) {
		if (this.occurrence == another.occurrence) {
			return this.word.compareTo(another.word);
		}
		return another.occurrence - this.occurrence;
	}

	@Override
	public boolean bLeaf() {
		return true;
	}

}
