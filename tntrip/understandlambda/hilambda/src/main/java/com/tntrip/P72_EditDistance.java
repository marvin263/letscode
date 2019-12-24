package com.tntrip;

public class P72_EditDistance {
    public enum Action {
        REPLACE {
            @Override
            public int[] prevPosition(int[] curPos, StringBuilder curSb, String target) {
                showProgress("Before: ", curPos, curSb, target);
                // 删除最后一个
                curSb.deleteCharAt(curSb.length() - 1);
                showProgress("After: ", curPos, curSb, target);
                return new int[]{curPos[0] - 1, curPos[1] - 1};
            }
        },
        REMOVE {
            @Override
            public int[] prevPosition(int[] curPos, StringBuilder curSb, String target) {
                showProgress("Before: ", curPos, curSb, target);
                // 删除最后一个
                curSb.deleteCharAt(curSb.length() - 1);
                showProgress("After: ", curPos, curSb, target);
                return new int[]{curPos[0] - 1, curPos[1]};
            }
        },
        INSERT {
            @Override
            public int[] prevPosition(int[] curPos, StringBuilder curSb, String target) {
                showProgress("Before: ", curPos, curSb, target);
                // 插入
                curSb.insert(curPos[0], target.charAt(curPos[1]));
                showProgress("After: ", curPos, curSb, target);
                return new int[]{curPos[0], curPos[1]};
            }
        },
        SKIP {
            @Override
            public int[] prevPosition(int[] curPos, StringBuilder curSb, String target) {
                showProgress("Before: ", curPos, curSb, target);
                // 删除最后一个
                curSb.deleteCharAt(curSb.length() - 1);
                showProgress("After: ", curPos, curSb, target);
                return new int[]{curPos[0] - 1, curPos[1] - 1};
            }
        };

        public abstract int[] prevPosition(int[] curPos, StringBuilder curSb, String target);

        public void showProgress(String note, int[] curPos, StringBuilder curSb, String target) {
            System.out.println(note + "curPos=[" + curPos[0] + "," + curPos[1] + "], curSb=" + curSb.toString() + ", curAction = " + this);
        }
    }

    public static class EachStep {
        int doneActionCount;
        Action lastAction;
    }

    public int minDistance(String word1, String word2) {
        EachStep[][] dp = new EachStep[word1.length() + 1][word2.length() + 1];
        return -1;
    }

    public static void main(String[] args) {
        P72_EditDistance p = new P72_EditDistance();
        System.out.println(p.minDistance("", ""));
    }
}
