package com.tntrip.focus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class P1025_DivisorGame {
    public enum ResultEnum {
        NOT_STARTED {
            @Override
            public ResultEnum incr() {
                throw new RuntimeException("do not incr from NOT_STARTED");
            }
        },
        WIN {
            @Override
            public ResultEnum incr() {
                return LOSE;
            }
        },
        LOSE {
            @Override
            public ResultEnum incr() {
                return WIN;
            }
        };

        public abstract ResultEnum incr();
    }

    public boolean divisorGame(int N) {
        ResultEnum[] result = new ResultEnum[1000 + 1];
        Arrays.fill(result, ResultEnum.NOT_STARTED);
        result[1] = ResultEnum.LOSE;
        ResultEnum rst = calcN(result, N);
        return rst == ResultEnum.WIN;
    }

    public ResultEnum calcN(ResultEnum[] result, int n) {
        if (result[n] != ResultEnum.NOT_STARTED) {
            return result[n];
        }
        List<Integer> factors = findFactors(n);
        for (Integer f : factors) {
            ResultEnum rst = calcN(result, (n - f)).incr();
            if (rst == ResultEnum.WIN) {
                result[n] = rst;
                return rst;
            }
        }
        result[n] = ResultEnum.LOSE;
        return ResultEnum.LOSE;
    }

    // 给定数字n，求余为0的所有因子
    private List<Integer> findFactors(int n) {
        List<Integer> factors = new ArrayList<>();
        factors.add(1);
        int middle = n >> 1;
        for (int i = 2; i <= middle; i++) {
            if (n % i == 0) {
                factors.add(i);
            }
        }
        return factors;
    }

    public static void main(String[] args) {
        P1025_DivisorGame p = new P1025_DivisorGame();
        int[] numbers = new int[]{1, 2, 3, 4, 1000};
        for (int n : numbers) {
            System.out.println(n + ": " + p.divisorGame(n));
        }
    }
}
