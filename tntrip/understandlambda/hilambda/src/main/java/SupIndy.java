import com.tntrip.understand.util.ReflectUtils;

import java.util.Date;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by nuc on 2015/11/24.
 */
public class SupIndy {
    public Date theIndy(long hours, String min) {
        BiFunction<Long, Long, String> func = (first, second) -> {
            return String.valueOf(first + second + hours) + min + getCurTime() + getRandom();
        };

        ReflectUtils.printMembers(this);
        String str = func.apply(8L, 9L);
        return new Date();
    }


    public String getCurTime() {
        return String.valueOf(System.currentTimeMillis() + new Random().nextLong());
    }

    public String getRandom() {
        return String.valueOf(new Random().nextLong());
    }


    public void notCapture() {
        Function<String, Date> func = (str) -> new Date(Long.parseLong(str));
        Date d = func.apply((System.currentTimeMillis() + 1000L) + "");
        System.out.println(d);
    }

    public static void main(String[] args) {
        SupIndy aa = new SupIndy();
        aa.theIndy(12L, "marvin");
        aa.notCapture();
    }
}
