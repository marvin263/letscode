import java.util.Date;
import java.util.function.Function;

public class SynthesizeWhat {
    public void m() {
        Function<String, Date> func = (str) -> new Date(Long.parseLong(str));
        func.apply("1450009989591");
    }

    public static void main(String[] args) {
        new SynthesizeWhat().m();
    }
}