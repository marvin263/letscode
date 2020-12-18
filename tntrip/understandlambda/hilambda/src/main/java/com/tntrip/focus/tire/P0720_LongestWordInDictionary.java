package com.tntrip.focus.tire;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class P0720_LongestWordInDictionary {
    public static class TrieNode {
        public TrieNode parent;
        public int parentCellIdx;
        public Cell[] cells;

        public TrieNode(TrieNode parent, int parentCellIdx) {
            this.parent = parent;
            this.parentCellIdx = parentCellIdx;
        }

        public TrieNode setChar(char c, boolean terminatorC) {
            cells = cells == null ? new Cell[26] : cells;

            int idx = toIdx(c);
            cells[idx] = cells[idx] == null ? new Cell(idx) : cells[idx];

            if (terminatorC) {
                cells[idx].asTerminatorCount++;
            }
            return cells[idx].child;
        }

        /**
         * 在当前这个 node 中， 某些 cell 已经是 某个词的终结符了。
         * 这些cell下的 child节点 才 *有可能* 后续被继续使用
         * <p>
         * 注意：这些child节点本身，可能并不包含词的终结符，所以是rawCandidate
         *
         * @return
         */
        public List<TrieNode> rawCandidates() {
            if (cells == null) {
                return Collections.emptyList();
            }

            List<TrieNode> list = new ArrayList<>();
            for (Cell cell : cells) {
                if (cell != null && cell.asTerminatorCount > 0) {
                    list.add(cell.child);
                }
            }
            return list;
        }

        public int toIdx(char c) {
            return (int) c - (int) 'a';
        }

        public char toChar(int idx) {
            return (char) ((int) 'a' + idx);
        }

        public class Cell {
            public int asTerminatorCount = 0;
            public TrieNode child;

            public Cell(int cellIdx) {
                child = new TrieNode(TrieNode.this, cellIdx);
            }
        }
    }

    public TrieNode buildTree(String[] words) {
        TrieNode root = new TrieNode(null, 0);

        for (String str : words) {
            TrieNode targetNode = root;

            char[] chars = str.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                targetNode = targetNode.setChar(chars[i], i == chars.length - 1);
            }
        }
        return root;
    }

    public String findLongestWord(String[] words) {
        TrieNode root = buildTree(words);
        TrieNode deepestNode = deepestNode(root);
        return buildLongestWord(deepestNode);
    }

    /**
     * 该node中的某个cell确实是 某个word的终结符
     *
     * @param n
     * @return
     */
    private boolean nodeContainsWordTerminator(TrieNode n) {
        if (n == null || n.cells == null) {
            return false;
        }
        for (TrieNode.Cell cell : n.cells) {
            if (cell != null && cell.asTerminatorCount > 0) {
                return true;
            }
        }
        return false;
    }

    private TrieNode deepestNode(TrieNode root) {
        List<TrieNode> curLevel = new ArrayList<>();
        curLevel.add(root);

        List<TrieNode> nextLevel = new ArrayList<>();
        while (true) {
            nextLevel.clear();
            for (TrieNode cur : curLevel) {
                nextLevel.addAll(cur.rawCandidates());
            }
            if (nextLevelIsEmpty(nextLevel)) {
                break;
            }
            curLevel.clear();
            curLevel.addAll(nextLevel);
        }

        for (TrieNode n : curLevel) {
            if (nodeContainsWordTerminator(n)) {
                return n;
            }
        }
        return null;
    }

    private boolean nextLevelIsEmpty(List<TrieNode> nextLevel) {
        for (TrieNode n : nextLevel) {
            if (nodeContainsWordTerminator(n)) {
                return false;
            }
        }
        return true;
    }


    public String buildLongestWord(TrieNode n) {
        if (n == null) {
            return "";
        }

        int whichCell = -1;
        for (int i = 0; i < n.cells.length; i++) {
            if (n.cells[i] != null && n.cells[i].asTerminatorCount > 0) {
                whichCell = i;
                break;
            }
        }
        
        if (whichCell == -1) {
            throw new RuntimeException("Should never been here");
        }

        List<Character> list = new ArrayList<>();
        while (n != null) {
            list.add(n.toChar(whichCell));
            whichCell = n.parentCellIdx;
            n = n.parent;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = list.size() - 1; i >= 0; i--) {
            sb.append(list.get(i));
        }
        return sb.toString();
    }

    public String longestWord(String[] words) {
        String s = findLongestWord(words);
        return s;
    }

    public static void main(String[] args) {
        P0720_LongestWordInDictionary p = new P0720_LongestWordInDictionary();
        // "yyd"
        String[] words = new String[]{"n", "j", "sl", "yyd", "slo", "jk", "jkdt", "y", "yy"};
        System.out.println(p.longestWord(words));

        // apple
        //String[] words2 = new String[]{"a", "banana", "app", "appl", "ap", "apply", "apple"};
        //System.out.println(p.longestWord(words2));
    }
}
