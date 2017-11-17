
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class CodeForBrotherZeng {
    public static class Stu {
        public double age;
        public String name;

        public Stu(String name, double age) {
            this.name = name;
            this.age = age;
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        Stu stu1 = new Stu("foyd", 22.1);
        Stu stu2 = new Stu("teddy", 19.1);
        Stu stu3 = new Stu("dean", 26.1);
        Stu stu4 = new Stu("lucas", 19.1);
        Stu stu5 = new Stu("tina", 26.1);

        List<Stu> list = new ArrayList<Stu>();
        list.add(stu1);
        list.add(stu2);
        list.add(stu3);
        list.add(stu4);
        list.add(stu5);

        Comparator<Stu> comparator = new Comparator<Stu>() {
            public int compare(Stu p1, Stu p2) {//return必须是int，而str.age是double,所以不能直接return (p1.age-p2.age)
                if ((p1.age - p2.age) < 0)
                    return -1;
                else if ((p1.age - p2.age) > 0)
                    return 1;
                else return 0;
            }
        };
        //jdk 7sort有可能报错，
        //加上这句话:System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        //表示，使用以前版本的sort来排序
        Collections.sort(list, comparator);

        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i).age + "  " + list.get(i).name);
        }

    }

}