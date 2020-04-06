package com.tntrip.focus;

import java.util.ArrayList;
import java.util.List;

public class P0474_OnesAndZeros {
    public int findMaxForm(String[] strs, int m, int n) {
        int[][] dp = new int[m + 1][n + 1];
        for (int k = 0; k < strs.length; k++) {
            String str = strs[k];
            int[] arr = consumed01(str);
            int consumed0 = arr[0];
            int consumed1 = arr[1];
            // 对于前k个元素
            for (int i = m; i >= consumed0; i--) {
                // 对于剩下 0, 1, 2, 3.... 个0的情况进行测试，但，是倒序测试的
                int left_0 = i - consumed0;
                for (int j = n; j >= consumed1; j--) {
                    // 对于剩下 0, 1, 2, 3.... 个1的情况进行测试，但，是倒序测试的
                    int left_1 = j - consumed1;
                    // 只考虑数组中前k个元素，对于i个0，j个1时
                    // dp[i][j]已经有值了。前(k-1)个元素时给它设置值了
                    dp[i][j] = Math.max(1 + dp[left_0][left_1], dp[i][j]);
                }
            }
        }

        return dp[m][n];
    }


    public EachStep findMaxddForm(String[] strs, int m, int n) {
        EachStep[][] dp = new EachStep[m + 1][n + 1];
        for (int i = 0; i < m + 1; i++) {
            for (int j = 0; j < n + 1; j++) {
                dp[i][j] = new EachStep();
            }
        }
        for (int k = 1; k <= strs.length; k++) {
            String str = strs[k - 1];
            int[] arr = consumed01(str);
            int consumed0 = arr[0];
            int consumed1 = arr[1];
            // 对于前k个元素
            for (int i = m; i >= consumed0; i--) {
                // 对于剩下 0, 1, 2, 3.... 个0的情况进行测试，但，是倒序测试的
                int left_0 = i - consumed0;
                for (int j = n; j >= consumed1; j--) {
                    // 对于剩下 0, 1, 2, 3.... 个1的情况进行测试，但，是倒序测试的
                    int left_1 = j - consumed1;
                    // 只考虑数组中前k个元素，对于i个0，j个1时
                    // dp[i][j]已经有值了。前(k-1)个元素时给它设置值了
                    EachStep ij = dp[i][j];
                    EachStep left = dp[left_0][left_1];
                    // 不要当前字符串
                    if (ij.maxFormByFar() >= (1 + left.maxFormByFar())) {

                    }
                    // 算上当前字符串的话，得到的串更多了
                    else {
                        ij.addKMaxForm(k, 1 + left.maxFormByFar());
                    }
                }
            }
        }

        return dp[m][n];
    }

    public static class EachStep {
        private static class KMaxForm {
            private final int k;
            private final int maxForm;

            @Override
            public String toString() {
                return "k=" + k +
                        ", maxForm=" + maxForm;
            }

            private KMaxForm(final int k, final int maxForm) {
                this.k = k;
                this.maxForm = maxForm;
            }
        }

        private List<KMaxForm> kMaxForms = new ArrayList<>();

        public int maxFormByFar() {
            int size = kMaxForms.size();
            return size >= 1 ? kMaxForms.get(size - 1).maxForm : 0;
        }

        public void addKMaxForm(final int k, final int maxForm) {
            kMaxForms.add(new KMaxForm(k, maxForm));
        }

        @Override
        public String toString() {
            return "{" + kMaxForms + '}';
        }
    }

    private int[] consumed01(String str) {
        int[] count01 = new int[2];
        char[] chars = str.toCharArray();
        for (char c : chars) {
            if (c == '0') {
                count01[0]++;
            } else if (c == '1') {
                count01[1]++;
            }
        }
        return count01;
    }

    public static void main(String[] args) {
        P0474_OnesAndZeros p = new P0474_OnesAndZeros();
        String[] strs1 = new String[]{"10", "0001", "111001", "1", "0"};
        int m1 = 5;
        int n1 = 3;
        System.out.println(p.findMaxddForm(strs1, m1, n1));//4

        String[] strs2 = new String[]{"10", "0", "1"};
        int m2 = 1;
        int n2 = 1;
        System.out.println(p.findMaxddForm(strs2, m2, n2));//2
    }
}
