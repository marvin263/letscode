import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Created by nuc on 2015/11/20.
 */
public class CompileOK {
    public static void shouldBeOK() {
        List<String> ls =
                Collections.checkedList(new ArrayList<>(), String.class);

        boolean flag = new Random().nextInt() % 2 == 0;
        Set<Integer> si = flag ? Collections.singleton(23) : Collections.emptySet();
    }

    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        list.add(0);
        list.add(2);
        list.add(5);
        list.add(4);
        list.add(3);
        list.add(0);
        list.add(2);
        list.add(5);
        list.add(4);
        list.add(3);
        list.add(0);
        list.add(2);
        list.add(5);
        list.add(4);
        list.add(2);
        list.add(5);
        list.add(4);
        list.add(3);
        list.add(0);
        list.add(2);
        list.add(5);
        list.add(4);
        list.add(2);
        list.add(5);
        list.add(4);
        list.add(3);
        list.add(0);
        list.add(2);
        list.add(5);
        list.add(4);
        list.add(2);
        list.add(5);
        list.add(4);
        list.add(3);
        list.add(0);
        list.add(2);
        list.add(5);
        list.add(4);
        list.add(2);
        list.add(5);
        list.add(4);
        list.add(3);
        list.add(0);
        list.add(2);
        list.add(5);
        list.add(4);
        list.add(3);
        list.add(5);
        list.add(4);
        list.add(3);
        list.add(5);
        list.add(4);
        list.add(3);

        list.sort((o1, o2) -> (o1.intValue() < o2.intValue() ? -1 : 1));

        list.sort(Integer::compare);

        Integer[] array =
                {0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 1, 1, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 2, 1, 0, 0, 0, 2, 30, 0, 3};

//        Arrays.sort(ARRAY);

        System.out.println(list);
    }
}
