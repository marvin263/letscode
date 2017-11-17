package tij4.innerclasses;

//: innerclasses/DotThis.java
// Qualifying access to the outer-class object.

public class DotThis {
	void f() {
		System.out.println("DotThis.f()");
	}

	public class Inner {
		public DotThis outer() {
			// Marvin: enclosing.this这样就得到了 enclosing instance
			return DotThis.this;
			// A plain "this" would be Inner's "this"
		}
	}

	public Inner inner() {
		return new Inner();
	}

	public static void main(String[] args) {
		DotThis dt = new DotThis();
		DotThis.Inner dti = dt.inner();
		dti.outer().f();
	}
} /*
 * Output: DotThis.f()
 */// :~
