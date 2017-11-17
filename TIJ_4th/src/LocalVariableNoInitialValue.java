public class LocalVariableNoInitialValue {
	// 和 1 搞，反转
	public static void main(String[] args) {
		int mask = 0xFFFFFFFF;// -1
		int a = 2;// 0000 0000, 0000 0000, 0000 0000,0000 0010
		System.out.println(a ^ mask);// 1111 1111,1111 1111,1111 1111,1111 1101
		
		// 我搽，原来可以直接取反。
		// 符号位 是一起取反 的（和 异或 1 疗效一样）
		System.out.println(~2);//3
		System.out.println(~(-3));//2
		
		// 1111 1111,1111 1111,1111 1111,1111 1101 就是 -3
		// 如下方法 求得正数 （减一，然后 按位取反）
		// 减一：1111 1111,1111 1111,1111 1111,1111 1100
		// 按位取反：0000 0000, 0000 0000, 0000 0000,0000 0011---->3
	}
}



