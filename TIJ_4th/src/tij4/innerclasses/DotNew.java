package tij4.innerclasses;

//: innerclasses/DotNew.java
// Creating an inner class directly using the .new syntax.

public class DotNew {
	public class Inner {
	}

	public static void main(String[] args) {
		DotNew dn = new DotNew();
		// Marvin：直接创建 InnerClass的对象。
		// 既然要关联到 enclosing instance，于是语法就是这样的：enclosingInsts.new InnerName();
		DotNew.Inner dni = dn.new Inner();
		DotNew.Inner dni2 = dn.new DotNew.Inner();
	}
} // /:~
