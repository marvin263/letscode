package tij4.strings;

import java.util.Calendar;
import java.util.Formatter;
import java.util.GregorianCalendar;

/**
 * Formatter测试
 * 
 * @author leizhimin 2009-7-16 16:31:02
 */
public class TestFormatter {

	public static void main(String[] args) {
		// %[argument_index$][flags][width][.precision]conversion
		Formatter f1 = new Formatter(System.out);
		// 格式化输出字符串和数字
		f1.format("1. 格式化输出：%s %d\n", "marvin string", 1235);
		// 日期的格式化
		Calendar c = new GregorianCalendar();
		f1.format("2. 当前日期:%1$tY-%1$tm-%1$te\n", c);
		// 2$：取第二个参数
		// -: 指定为左对齐，默认右对齐
		// 5：最大输出宽度为20,不够会补空格，实际若超过则全部输出
		// .2：在此表示输出参数2的最大字符数量，如果是浮点数字，则表示小数部分显示的位数
		// s ：表示输入参数是字符串
		// 注意，输出是：3. AB    abcdefg
		f1.format("3. %2$-5.2s %1$2s\n", "abcdefg", "ABCEDEFG");

		// 将格式化的结果存储到字符串
		String fs = String.format("4. 身高体重(%.2f , %d)\n", 173.2, 65);
		System.out.println(fs);

		f1.close();
	}
}
