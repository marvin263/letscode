import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by nuc on 2016/1/2.
 */
public class Permutation {

    public String getPermutation(int n, int k) {
        int[] elements = new int[n];
        for (int i = 0; i < elements.length; i++) {
            elements[i] = i + 1;
        }
        List<int[]> listOldAlready = generateNewPermutation(elements);
        int[] indicx = listOldAlready.get(k - 1);

        StringBuilder sb = new StringBuilder();
        for (int i : indicx) {
            sb.append(elements[i]);
        }
        return sb.toString();
    }

    private List<int[]> generateNewPermutation(int[] elements) {
        List<int[]> listOldAlready = Collections.emptyList();
        for (int i = 0; i < elements.length; i++) {
            listOldAlready = generateNewPermutationByAddingNewElement(listOldAlready, i);
        }
        return listOldAlready;
    }

    private List<int[]> generateNewPermutationByAddingNewElement(List<int[]> listOldAlready, int aNewIndex) {
        List<int[]> listNewAlready = new ArrayList<>();
        for (int[] ints : listOldAlready) {
            listNewAlready.addAll(insertInGap(ints, aNewIndex));
        }
        listNewAlready.addAll(prependInHeader(listOldAlready, aNewIndex));
        return listNewAlready;
    }

    private List<int[]> insertInGap(int[] prevSeq, int aNewIndex) {
        int len = prevSeq.length;
        List<int[]> listSeqs = new ArrayList<>(len);
        for (int i = len - 1; i >= 0; i--) {
            int[] newSeq = new int[len + 1];
            System.arraycopy(prevSeq, 0, newSeq, 0, i + 1);
            newSeq[i + 1] = aNewIndex;
            System.arraycopy(prevSeq, i + 1, newSeq, i + 2, len - i - 1);
            listSeqs.add(newSeq);
        }
        return listSeqs;
    }

    private List<int[]> prependInHeader(List<int[]> listOldAlready, int aNewIndex) {
        if (listOldAlready.isEmpty()) {
            List<int[]> list = new ArrayList<>(1);
            list.add(new int[]{aNewIndex});
            return list;
        }
        List<int[]> list = new ArrayList<>(listOldAlready.size());
        for (int[] ints : listOldAlready) {
            int[] newSeq = new int[ints.length + 1];
            System.arraycopy(ints, 0, newSeq, 1, ints.length);
            newSeq[0] = aNewIndex;
            list.add(newSeq);
        }
        return list;
    }

    public static void main(String[] args) {
        //System.out.println(new Permutation().getPermutation(9, 273815));
    }
}
