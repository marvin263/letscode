package tij4.strings;

//: strings/Conversion.java
import java.math.BigInteger;
import java.util.Formatter;

public class Conversion {
	public static void main(String[] args) {
		Formatter f = new Formatter(System.out);
		/** 1. 输出 char **/
		char u = 'a';
		System.out.println("u = 'a'");
		f.format("s: %s\n", u);// 字符串
		// f.format("d: %d\n", u); // 简直不能更好了。char 不能作为int输出
		f.format("c: %c\n", u);// unicode输出
		f.format("b: %b\n", u);// boolean输出
		// f.format("f: %f\n", u);// char 不能 作为浮点数输出的
		// f.format("e: %e\n", u);
		// f.format("x: %x\n", u);
		Object obj = new Object();
		System.out.println(Integer.toHexString(obj.hashCode()));
		f.format("h: %h\n", obj);

		/** 2. 输出 int **/
		int v = 121;
		System.out.println("v = 121");
		f.format("d: %d\n", v);
		f.format("c: %c\n", v);// int可以输出 unicode 啊
		f.format("b: %b\n", v);
		f.format("s: %s\n", v);
		// f.format("f: %f\n", v);// int 不能 作为浮点数输出的
		// f.format("e: %e\n", v);
		f.format("x: %x\n", v);
		f.format("h: %h\n", v);// int竟然也能按着 hashcode 来输出

		/** 3. 输出 BigInteger **/
		BigInteger w = new BigInteger("50000000000000");
		System.out.println("w = new BigInteger(\"50000000000000\")");
		f.format("d: %d\n", w);
		// f.format("c: %c\n", w);// BigInteger不能 作为 unicode 输出
		f.format("b: %b\n", w); // boolean/Boolean外的所有的类型；非null，则总是true
		f.format("s: %s\n", w);
		// f.format("f: %f\n", w); // BigInteger也不能作为 浮点数 输出的
		// f.format("e: %e\n", w);
		f.format("x: %x\n", w);
		f.format("h: %h\n", w);

		/** 4. 输出 double **/
		double x = 179.543;
		System.out.println("x = 179.543");
		// f.format("d: %d\n", x); //很好，非常好。double 不能作为int输出（int也不能作为double输出）
		// f.format("c: %c\n", x);// double也不能 作为 unicode 输出
		f.format("b: %b\n", x);
		f.format("s: %s\n", x);
		f.format("f: %f\n", x);
		f.format("e: %e\n", x);
		// f.format("x: %x\n", x);//很好，非常好。double不能作为 16进制 int输出
		f.format("h: %h\n", x);

		/** 5. 输出 任意对象 **/
		Conversion y = new Conversion();
		System.out.println("y = new Conversion()");
		// f.format("d: %d\n", y);
		// f.format("c: %c\n", y);
		f.format("b: %b\n", y);
		f.format("s: %s\n", y);// 当然，任何对象，都可以输出为 字符串
		// f.format("f: %f\n", y);
		// f.format("e: %e\n", y);
		// f.format("x: %x\n", y);
		f.format("h: %h\n", y);

		/** 6. 输出 boolean **/
		boolean z = false;
		System.out.println("z = false");
		// f.format("d: %d\n", z);//很好，非常好。boolean不能作为 int
		// f.format("c: %c\n", z);
		f.format("b: %b\n", z);
		f.format("s: %s\n", z);
		// f.format("f: %f\n", z);
		// f.format("e: %e\n", z);
		// f.format("x: %x\n", z);
		f.format("h: %h\n", z);
	}
}
