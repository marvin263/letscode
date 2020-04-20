package com.basicsort.s05_noncomparison;

import com.basicsort.AbstractBasicSort;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * <pre>
 *
 *   第 11 种：  桶排序
 *   属于 非比较 排序(non comparison)
 *   stable, best--xxx, average--n+r, worst--n+r, memory--n+r, n<<2^k--yes
 *
 * </pre>
 *
 * @author marvin
 */
public class S_11_BucketSort_noncomparison extends AbstractBasicSort {

    @Override
    public int[] doSort(int start, int end, int[] array) {
        ArrayList<Integer> list = new ArrayList<>();
        Arrays.stream(array).forEach(list::add);
        return bucketSort(list, 3).stream().mapToInt(e -> e).toArray();
    }

    /**
     * 桶排序
     *
     * @param array
     * @param bucketSize
     * @return
     */
    public static ArrayList<Integer> bucketSort(ArrayList<Integer> array, int bucketSize) {
        if (array == null || array.size() < 2)
            return array;
        int max = array.get(0), min = array.get(0);
        // 找到最大值最小值
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i) > max)
                max = array.get(i);
            if (array.get(i) < min)
                min = array.get(i);
        }
        int bucketCount = (max - min) / bucketSize + 1;
        ArrayList<ArrayList<Integer>> bucketArr = new ArrayList<>(bucketCount);
        ArrayList<Integer> resultArr = new ArrayList<>();
        for (int i = 0; i < bucketCount; i++) {
            bucketArr.add(new ArrayList<Integer>());
        }
        for (int i = 0; i < array.size(); i++) {
            bucketArr.get((array.get(i) - min) / bucketSize).add(array.get(i));
        }
        for (int i = 0; i < bucketCount; i++) {
            if (bucketSize == 1) { // 如果带排序数组中有重复数字时  感谢 @见风任然是风 朋友指出错误
                for (int j = 0; j < bucketArr.get(i).size(); j++)
                    resultArr.add(bucketArr.get(i).get(j));
            } else {
                if (bucketCount == 1)
                    bucketSize--;
                ArrayList<Integer> temp = bucketSort(bucketArr.get(i), bucketSize);
                for (int j = 0; j < temp.size(); j++)
                    resultArr.add(temp.get(j));
            }
        }
        return resultArr;
    }

    public static void main(String[] args) {
        runTest(new S_11_BucketSort_noncomparison());
    }
}
