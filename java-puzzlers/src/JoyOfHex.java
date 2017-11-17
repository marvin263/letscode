public class JoyOfHex {
    public static void main(String[] args) {
    	//Marvin: 妈的，hex, oct 竟然自动带有符号位
        System.out.println(
            Long.toHexString(0x100000000L + 0xcafebabe));
    }
}
