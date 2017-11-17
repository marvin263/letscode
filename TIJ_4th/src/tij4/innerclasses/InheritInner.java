package tij4.innerclasses;

//: innerclasses/InheritInner.java
// Inheriting an inner class.

class WithInner {
	class Inner {
	}
}

// Marvin: 只继承自 内部类，挺好，嘻嘻。（注意：wi.new WithInner.Inner()无法编译哦，亲）
public class InheritInner extends WithInner.Inner {
	// ! InheritInner() {} // Won't compile
	InheritInner() {
	}
	
	
	

	InheritInner(WithInner wi) {
		wi.super();
	}

	public static void main(String[] args) {
		WithInner wi = new WithInner();
		InheritInner ii = new InheritInner(wi);
	}
} // /:~
