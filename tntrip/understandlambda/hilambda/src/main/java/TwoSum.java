import java.util.HashMap;

/**
 * Created by nuc on 2016/1/2.
 */
public class TwoSum {
    public int[] twoSum_1(int[] nums, int target) {
        int[] rst = new int[2];
        int len = nums.length;
        for (int i = 0; i < len; i++) {
            rst[0] = i + 1;
            int a = nums[i];
            int b = target - a;
            for (int k = i + 1; k < len; k++) {
                if (nums[k] == b) {
                    rst[1] = k + 1;
                    return rst;
                }
            }
        }
        return rst;
    }

    public int[] twoSum(int[] nums, int target) {
        int[] rst = new int[2];
        int len = nums.length;
        HashMap<Integer, Integer> map = value2Index(nums);
        for (int i = 0; i < len; i++) {
            int b = target - nums[i];
            Integer bIndex = map.get(b);
            if (bIndex != null && bIndex > i) {
                rst[0] = i + 1;
                rst[1] = bIndex + 1;
                return rst;
            }
        }
        return rst;
    }

    private HashMap<Integer, Integer> value2Index(int[] nums) {
        int len = nums.length;
        HashMap<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < len; i++) {
            int a = nums[i];
            map.put(a, i);
        }
        return map;
    }

    public static void main(String[] args) {
        int[] nums = {3, 2, 4};
        int target = 6;
        new TwoSum().twoSum(nums, target);
    }
}
