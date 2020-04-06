package com.tntrip.focus;

public class P0072_EditDistance {
    public enum Action {
        REPLACE {
            @Override
            public int[] prevPosition(String target, StringBuilder curFrom, int[] curPos) {
                int i = curPos[0], j = curPos[1];
                String before = curFrom.toString();
                curFrom.replace(i - 1, i, target.charAt(j - 1) + "");
                String after = curFrom.toString();
                String info = String.format("target=%s, action=%s, %s-->%s", target, this, before, after);
                System.out.println(info);
                return new int[]{i - 1, j - 1};
            }
        },
        DELETE {
            @Override
            public int[] prevPosition(String target, StringBuilder curFrom, int[] curPos) {
                int i = curPos[0], j = curPos[1];
                String before = curFrom.toString();
                curFrom.deleteCharAt(i - 1);
                String after = curFrom.toString();
                String info = String.format("target=%s, action=%s, %s-->%s", target, this, before, after);
                System.out.println(info);
                return new int[]{i - 1, j};
            }
        },
        INSERT {
            @Override
            public int[] prevPosition(String target, StringBuilder curFrom, int[] curPos) {
                int i = curPos[0], j = curPos[1];
                String before = curFrom.toString();
                curFrom.insert(i, target.charAt(j - 1));
                String after = curFrom.toString();
                String info = String.format("target=%s, action=%s, %s-->%s", target, this, before, after);
                System.out.println(info);
                return new int[]{i, j - 1};
            }
        },
        SKIP {
            @Override
            public int[] prevPosition(String target, StringBuilder curFrom, int[] curPos) {
                String before = curFrom.toString();
                String after = curFrom.toString();
                String info = String.format("target=%s, action=%s, %s-->%s", target, this, before, after);
                //System.out.println(info);
                int i = curPos[0], j = curPos[1];
                return new int[]{i - 1, j - 1};
            }
        };

        public abstract int[] prevPosition(String target, StringBuilder curFrom, int[] curPos);

    }

    public static class EachStep {
        /**
         * 加上当前的action，总共的action数量
         */
        final int alreadyDoneCount;
        final Action action;

        private EachStep(final int alreadyDoneCount, final Action action) {
            this.alreadyDoneCount = alreadyDoneCount;
            this.action = action;
        }

        public static EachStep create(int doneActionCount, Action lastAction) {
            return new EachStep(doneActionCount, lastAction);
        }
    }

    public EachStep[][] minDistance(String word1, String word2) {
        EachStep[][] dp = new EachStep[word1.length() + 1][word2.length() + 1];
        dp[0][0] = EachStep.create(0, Action.SKIP);
        // word1 太短了，给word1插入字符
        for (int i = 1; i < (word2.length() + 1); i++) {
            dp[0][i] = EachStep.create(i, Action.INSERT);
        }
        // word1 太长了，从word1中删除字符
        for (int i = 1; i < (word1.length() + 1); i++) {
            dp[i][0] = EachStep.create(i, Action.DELETE);
        }
        for (int m = 0; m < word1.length(); m++) {
            char c1 = word1.charAt(m);
            int i = m + 1;
            for (int n = 0; n < word2.length(); n++) {
                int j = n + 1;
                char c2 = word2.charAt(n);
                if (c1 == c2) {
                    dp[i][j] = EachStep.create(dp[i - 1][j - 1].alreadyDoneCount, Action.SKIP);
                } else {
                    dp[i][j] = chooseMin(
                            // 知道了si-->sj-1：那么si怎么到sj呢？
                            // si可以变为sj-1，为了让si变为sj，s[i]后面再插入一个字母（即si变为si+1）就可以了，即：si 多插入一个字符
                            EachStep.create(dp[i][j - 1].alreadyDoneCount + 1, Action.INSERT),
                            // 知道了si-1-->sj：那么si怎么到sj呢？要si多删除一个字母（即si变为si-1）就可以了，即：si 多删除一个字符
                            EachStep.create(dp[i - 1][j].alreadyDoneCount + 1, Action.DELETE),
                            // 知道了si-1-->sj-1：那么si怎么到sj呢？给s[i]赋值为s[j]就可以了，即：si 被替换一个字符
                            EachStep.create(dp[i - 1][j - 1].alreadyDoneCount + 1, Action.REPLACE)
                    );
                }
            }
        }
        return dp;
    }

    private EachStep chooseMin(EachStep... steps) {
        EachStep min = steps[0];
        for (int i = 1; i < steps.length; i++) {
            min = (min.alreadyDoneCount > steps[i].alreadyDoneCount) ? steps[i] : min;
        }
        return min;
    }

    public void showProgress(String from, String target) {
        StringBuilder curFrom = new StringBuilder(from);

        EachStep[][] dp = minDistance(from, target);

        int[] curPos = new int[]{from.length(), target.length()};
        EachStep curEs = dp[curPos[0]][curPos[1]];

        while (curEs.alreadyDoneCount > 0) {
            int[] prevPos = curEs.action.prevPosition(target, curFrom, curPos);
            if (prevPos == null) {
                break;
            }
            EachStep prevEs = dp[prevPos[0]][prevPos[1]];

            curPos = prevPos;
            curEs = prevEs;
        }
    }

    public static void main(String[] args) {
        P0072_EditDistance p = new P0072_EditDistance();

        p.showProgress("horse", "ros");

        p.showProgress("intention", "execution");

        String word1 = "议传达学央农村工作会议在习了习近平总书记日前在中央";
        String word2 = "议在议传达农村工习近平总书记日作会习了前在中央";
        p.showProgress(word1, word2);
    }
}
