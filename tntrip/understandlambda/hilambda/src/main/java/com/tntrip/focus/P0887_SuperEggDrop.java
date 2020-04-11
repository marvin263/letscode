package com.tntrip.focus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class P0887_SuperEggDrop {
    /**
     * firstly, if we have k eggs and s steps to detect a building with Q(k, s) floors,
     * <p>
     * secondly, we use 1 egg and 1 step to detect one floor,
     * if egg break, we can use (k-1) eggs and (s-1) step to detect with Q(k-1, s-1),
     * <p>
     * if egg isn't broken, we can use k eggs and (s-1) step to detect with Q(k, s-1),
     * So, Q(k, s) = 1 + Q(k, s-1) + Q(k-1, s-1);
     * <p>
     * dp[i] is max floors we can use i eggs and s step to detect.
     *
     * @param K
     * @param N
     * @return
     */
    public int superEggDropd(int K, int N) {
        int[] dp = new int[K + 1];
        int step = 0;
        for (; dp[K] < N; step++) {
            for (int i = K; i > 0; i--)
                dp[i] = (1 + dp[i] + dp[i - 1]);
        }
        return step;
    }

    public int superEggDrop(int K, int N) {
        int[][] result = new int[K + 1][N + 1];
        for (int[] row : result) {
            Arrays.fill(row, -1);
        }

        // 只有1个蛋，n层则需要n次
        for (int n = 0; n <= N; n++) {
            result[1][n] = n;
        }
        // 只有1层，无论几个蛋，总是需要1次
        for (int k = 0; k <= K; k++) {
            result[k][1] = 1;
        }

        // 只有0个蛋，总是需要0次
        Arrays.fill(result[0], 0);
        // 只有0层，总是需要0次
        for (int k = 0; k <= K; k++) {
            result[k][0] = 0;
        }

        return dp(result, K, N);
    }


    public int dp(int[][] result, int K, int N) {
        if (result[K][N] != -1) {
            return result[K][N];
        }

        int finalRst = Integer.MAX_VALUE;
        for (int n = 1; n <= N; n++) {
            finalRst = Math.min(
                    finalRst,
                    Math.max(dp(result, K - 1, n - 1), dp(result, K, N - n)) + 1
            );
        }
        result[K][N] = finalRst;
        //System.out.println("K=" + K + ", N=" + N + ", finalRst=" + finalRst);
        return finalRst;
    }


    public int ddddd(int K, int N) {
        // dp[i]
        // 给定i个蛋，消耗了S步骤，能探测到的最大楼层数 dp[i]

        int S = 1000;
        int[][] dp = new int[K + 1][S + 1];
        // dp[k][s]
        // 使用k个蛋，消耗s步，能探测最大楼层 dp[k][s]

        // 用1个蛋，1步 做探测
        //   碎：k-1个蛋，s-1步：还能再探测 dp[k-1][s-1]
        // 不碎：k  个蛋，s-1步：  还能再探测 dp[k][s-1]
        // dp[k][s] = 1 + 


        // 给定s个步骤
//        for (int s = 0; s < S; s++) {
//            for (int k = 1; k < K; k++) {
//                dp[k] = 1 + dp[k-1]+dp[k];
//            }
//        }
//        
//        
        int step = 0;
//        for (; dp[K] < N; step++) {
//            for (int i = K; i > 0; i--)
//                dp[i] = (1 + dp[i] + dp[i - 1]);
//        }
        return step;
    }

    public Node getExisted(int parentFloor, int floor, int eggs) {
        return null;
    }

    public static class Node {
        public static final int FLOORS = 6;
        public static final int NO_PARENT_FLOOR = -1;

        public final int parentFloor;

        /**
         * 楼层。当前就是要 站在该楼层上扔鸡蛋
         * <p>
         * 第1层、第2层、第3层...
         */
        public final int floor;

        /**
         * 站在当前楼层，手里持有的鸡蛋个数
         */
        public final int eggs;
        List<Node> left = new ArrayList<>();
        List<Node> right = new ArrayList<>();

        // 从该节点开始时，依然可用的eggs
        Node(int parentFloor, int floor, int eggs) {
            this.parentFloor = parentFloor;
            this.floor = floor;
            this.eggs = eggs;
        }

        public static Node create(int parentFloor, int floor, int eggs) {
            Node n = new Node(parentFloor, floor, eggs);
            return n;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return parentFloor == node.parentFloor &&
                    floor == node.floor &&
                    eggs == node.eggs;
        }

        @Override
        public int hashCode() {
            return Objects.hash(parentFloor, floor, eggs);
        }
    }


    public static void main(String[] args) {
        P0887_SuperEggDrop p = new P0887_SuperEggDrop();
        //System.out.println(p.superEggDropd(9000000, 100000000));//2
        System.out.println(p.superEggDrop(6, 127));//3
//        System.out.println(p.superEggDrop(3, 14));//4
//        System.out.println(p.superEggDrop(2, 100));//14
//        System.out.println(p.superEggDrop(5, 10000));//14
        
        
        
    }
}
