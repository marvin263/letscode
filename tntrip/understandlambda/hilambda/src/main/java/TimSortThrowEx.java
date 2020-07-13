import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class TimSortThrowEx {
    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        int count = 32;
        while (true) {
            list.clear();
            for (int i = 0; i < count; i++) {
                list.add(new Random().nextInt(5));
            }
            try {
                list.sort(new Comparator<Integer>() {
                    @Override
                    public int compare(Integer o1, Integer o2) {
                        if (o1 >= o2) {
                            return 1;
                        } else {
                            return -1;
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(list);
                break;
            }
        }
    }
}
