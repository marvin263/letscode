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

        public static EachStep create(int doneActionCount, Action lastAction) {
            EachStep es = new EachStep();
            es.doneActionCount = doneActionCount;
            es.lastAction = lastAction;
            return es;
        }
    }

    public int minDistance(String word1, String word2) {
        EachStep[][] dp = new EachStep[word1.length() + 1][word2.length() + 1];
        dp[0][0] = EachStep.create(0, Action.SKIP);
        for (int i = 1; i < (word2.length() + 1); i++) {
            dp[0][i] = EachStep.create(i, Action.INSERT);
        }
        for (int i = 1; i < (word1.length() + 1); i++) {
            dp[i][0] = EachStep.create(i, Action.REMOVE);
        }
        for (int m = 0; m < word1.length(); m++) {
            char c1 = word1.charAt(m);
            int i = m + 1;
            for (int n = 0; n < word2.length(); n++) {
                int j = n + 1;
                char c2 = word2.charAt(n);
                if (c1 == c2) {
                    dp[i][j] = EachStep.create(dp[i - 1][j - 1].doneActionCount, Action.SKIP);
                } else {
                    dp[i][j] = chooseMin(
                            EachStep.create(dp[i][j - 1].doneActionCount + 1, Action.INSERT),
                            EachStep.create(dp[i - 1][j].doneActionCount + 1, Action.REMOVE),
                            EachStep.create(dp[i - 1][j - 1].doneActionCount + 1, Action.REPLACE));
                }
            }
        }


        return dp[word1.length()][word2.length()].doneActionCount;
    }

    private EachStep chooseMin(EachStep... steps) {
        EachStep min = steps[0];
        for (int i = 1; i < steps.length; i++) {
            min = (min.doneActionCount > steps[i].doneActionCount) ? steps[i] : min;
        }
        return min;
    }

    public static void main(String[] args) {
        P72_EditDistance p = new P72_EditDistance();
        // 3
        System.out.println(p.minDistance("horse", "ros"));
        // 5
        System.out.println(p.minDistance("intention", "execution"));
    }
}
