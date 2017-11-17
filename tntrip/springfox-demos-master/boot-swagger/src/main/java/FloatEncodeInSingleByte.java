/**
 * Created by nuc on 2017/3/4.
 */
public class FloatEncodeInSingleByte {
    public static void main(String[] args) {
        esUsedFieldNorm(43);
    }

    public static void esUsedFieldNorm(int termCountInField) {
        double d = 1.0 / Math.sqrt(termCountInField);
        float f = (float) d;
        System.out.println(f);
        byte b = SmallFloat.floatToByte315(f);
        float bf = SmallFloat.byte315ToFloat(b);
        System.out.println(bf);
    }
}
