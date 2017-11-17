import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * Created by nuc on 2015/11/22.
 */
public class Indy {
    public String theIndy(int passedIn) {
        Function<Integer, String> func = (i) -> String.valueOf(i + passedIn);
        String str = func.apply(1);

        List<String> lst = Arrays.asList("1", "2", "3", "4", "5");
        //1. 对于其中的元素，应用这个函数
        // 最终结果的类型不变；有可能没有结果（如果输入为空）
        String aa = lst.stream().reduce((x, y) -> (Integer.parseInt(x) + Integer.parseInt(y)) + "").get();
        System.out.println(aa);

        //2. 对于其中的元素，应用这个函数
        // 最终结果的类型不变；但，有默认值
        String bb = lst.stream().reduce("100", (x, y) -> (Integer.parseInt(x) + Integer.parseInt(y)) + "");
        System.out.println(bb);

        //3. 对于其中的元素，应用这个函数
        // 更一般的形式
        int cc = lst.stream().
                reduce
                        (1000, //identity
                                (partialRst, y) -> partialRst + Integer.parseInt(y), //accumulator
                                (partialRst1, partialRst2) -> partialRst1 + partialRst2 //combiner
                        );
        System.out.println(cc);


        return str;
    }

    public String mutable_reduce() {
        List<String> lst = Arrays.asList("I ", "have ", "incredible ", "super ", "power!!!");

        //However, we might not be happy about the performance
        String immutableWay = lst.stream().reduce((x, y) -> x.concat(y)).get();
        System.out.println(immutableWay);

        // accumulate the results into a mutable container (that is StringBuilder)
        StringBuilder sb = lst.stream().
                collect
                        (
                                StringBuilder::new,//Supplier
                                (rstContainer, e) -> rstContainer.append(e),//accumulator
                                (rstContainer1, rstContainer2) -> rstContainer1.append(rstContainer2)//combiner
                        );
        System.out.println(sb.toString());


        return "";
    }

    public static void main(String[] args) {
        Indy aa = new Indy();
        aa.mutable_reduce();
//        aa.theIndy(10);
    }
}
