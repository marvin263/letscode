package effective2.Chapter5.Item23;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Raw {
    // Uses raw type (List) - fails at runtime! - Page 112
    public static void main(String[] args) {
//    	numElementsInCommon(new HashSet<String>(), new HashSet<Integer>());
//
//    	List<String> strings = new ArrayList<String>();
//        unsafeAdd(strings, new Integer(42));
//        String s = strings.get(0); // Compiler-generated cast
//
//        List listRaw = strings;
//        listRaw.add(new Integer(42));
//        
//        List<?> listWildcard = strings;
////        listWildcard.add(new Integer(42));
        System.out.println(2<<4);
    }

    private static void unsafeAdd(List list, Object o) {
        list.add(o);
    }

    // Use of raw type for unknown element type - don't do this! - Page 113
    static int rawNumElementsInCommon(Set s1, Set s2) {
        int result = 0;
        for (Object o1 : s1)
            if (s2.contains(o1))
                result++;
        return result;
    }

    // Unbounded wildcard type - typesafe and flexible - Page 113
    static int numElementsInCommon(Set<?> s1, Set<?> s2) {
        int result = 0;
        for (Object o1 : s1)
            if (s2.contains(o1))
                result++;
        return result;
    }
}

