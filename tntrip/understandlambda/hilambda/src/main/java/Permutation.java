import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by nuc on 2016/1/2.
 */
public class Permutation {
    /**
     * @param elements
     * @param fromIndex, inclusive
     * @param endIndex,  inclusive
     * @param <E>
     * @return
     */
    public static <E> List<List<E>> p(E[] elements, int fromIndex, int endIndex) {
        if (!(endIndex >= 0 && endIndex < elements.length) &&
                !(fromIndex >= endIndex)) {
            throw new RuntimeException(String.format("Require: fromIndex >= endIndex && endIndex > elements.length. Actually: fromIndex=%d, endIndex=%d, elements.length=%d", fromIndex, endIndex, elements.length));
        }
        List<List<E>> result = new ArrayList<>((int) fact(endIndex - fromIndex + 1));
        doP(elements, result, fromIndex, endIndex);
        System.out.println(String.format("Element count: %d, permutation count: %d", (endIndex - fromIndex + 1), result.size()));
        for (List<E> row : result) {
            System.out.println(row);
        }
        return result;
    }

    private static <E> void doP(E[] elements, List<List<E>> result, int fromIndex, int endIndex) {
        if (fromIndex > endIndex) {
            return;
        }
        List<List<E>> newResult = new ArrayList<>();
        if (result.isEmpty()) {
            List<E> row = new LinkedList<>();
            newResult.add(row);
            row.add(elements[fromIndex]);
        } else {
            for (List<E> row : result) {
                for (int i = 0; i <= row.size(); i++) {
                    List<E> copyRow = new LinkedList<>(row);
                    newResult.add(copyRow);
                    copyRow.add(i, elements[fromIndex]);
                }
            }
        }
        result.clear();
        result.addAll(newResult);

        doP(elements, result, (fromIndex + 1), endIndex);
    }

    public static long fact(long n) {
        if (n < 0L) {
            throw new RuntimeException(String.format("Require: n > 0. Actually: n=%d", n));
        }
        if (n == 0L) {
            return 1L;
        }
        long rst = 1L;
        for (long i = 1L; i <= n; i++) {
            rst *= i;
        }
        return rst;

    }

    public static void main(String[] args) {
        String[] arr = new String[]{"a", "b", "c", "d", "e", "f"};
        p(arr, 0, 5);
    }

}
