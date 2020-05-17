package com.search;

public class BinarySearch extends AbstractBasicSearch {

    @Override
    public int doSearch(int start, int end, int[] array) {
        return binarySearch(array, start, end + 1, 25);
    }


    /**
     * Searches a range of the specified array of ints for the specified value using the binary search algorithm.
     * <p>
     * The range must be sorted (as by the sort(int[], int, int)} method prior to making this call.
     * <p>
     * If it is not sorted, the results are undefined.
     * <p>
     * If the range contains multiple elements with the specified value, there is no guarantee which one will be found.
     *
     * @param a         the array to be searched
     * @param fromIndex the index of the first element (inclusive) to be searched
     * @param toIndexExclusive   the index of the last element (exclusive) to be searched
     * @param key       the value to be searched for
     * @return index of the search key, if it is contained in the array within the specified range;
     * <p>
     * otherwise, <tt>(-(<i>insertion point</i>) - 1)</tt>.
     * <p>
     * The <i>insertion point</i> is defined as the point at which the key would be inserted into the array:
     * the index of the first element in the range greater than the key, or
     * <tt>toIndexExclusive</tt> if all elements in the range are less than the specified key.
     * <p>
     * Note that this guarantees that the return value will be >= 0 if and only if the key is found.
     * @throws IllegalArgumentException       if {@code fromIndex > toIndexExclusive}
     * @throws ArrayIndexOutOfBoundsException if {@code fromIndex < 0 or toIndexExclusive > a.length}
     * @since 1.6
     */
    public static int binarySearch(int[] a, int fromIndex, int toIndexExclusive,
                                   int key) {
        rangeCheck(a.length, fromIndex, toIndexExclusive);
        return binarySearch0(a, fromIndex, toIndexExclusive, key);
    }

    public static int binarySearch(int[] a, int key) {
        return binarySearch0(a, 0, a.length, key);
    }

    // Like public version, but without range checks.
    private static int binarySearch0(int[] a, int fromIndex, int toIndexExclusive,
                                     int key) {
        int low = fromIndex;
        int high = toIndexExclusive - 1;
        // high同样是 inclusive 哦
        while (low <= high) {
            int mid = (low + high) >>> 1;
            int midVal = a[mid];

            if (midVal < key)
                low = mid + 1;
            else if (midVal > key)
                high = mid - 1;
            else
                return mid; // key found
        }
        return -(low + 1);  // key not found.
    }

    private static void rangeCheck(int arrayLength, int fromIndex, int toIndexExclusive) {
        if (fromIndex > toIndexExclusive) {
            throw new IllegalArgumentException(
                    "fromIndex(" + fromIndex + ") > toIndexExclusive(" + toIndexExclusive + ")");
        }
        if (fromIndex < 0) {
            throw new ArrayIndexOutOfBoundsException(fromIndex);
        }
        if (toIndexExclusive > arrayLength) {
            throw new ArrayIndexOutOfBoundsException(toIndexExclusive);
        }
    }

    public static void main(String[] args) {
        runTest(new BinarySearch());
    }
}
