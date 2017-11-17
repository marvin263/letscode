/**
 * 两件事情需要搞清楚
 * 
 * 1、double 和 float 在计算机中是如何表示的
 * 
 * 2、移位运算（这个都喊了 n 年了）
 * 
 * 【1】二进制浮点对于货币计算是非常不适合的，因为它不可能将0.1——或者10的其它任何次负幂——精确表示为一个长度有限的二进制小数
 * 
 * 【2】更一般地说，问题在于并不是所有的小数都可以用二进制浮点数来精确表示的
 * 
 * 【3】一定要用BigDecimal(String)构造器，而千万不要用BigDecimal(double)。后一个构造器将用它的参数的“精确”
 * 值来创建一个实例：new BigDecimal(.1)将返回一个表示0
 * .100000000000000055511151231257827021181583404541015625的BigDecimal
 * 
 * @author mli2
 * 
 */
public class DoubleAdd {
	public static void main(String[] args) {
		double d1 = 12.69D; 
		double d2 = -1.31D;
		double d3 = d1 + d2;
		System.out.println(d3);
	}
}
