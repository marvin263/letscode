package tij4.generics;
//: generics/ErasureAndInheritance.java

class GenericBase<T> {
  private T element;
  public void set(T arg) { arg = element; }
  public T get() { return element; }
}

class Derived1<T> extends GenericBase<T> {}

class Derived2 extends GenericBase {} // No warning

// class Derived3 extends GenericBase<?> {}
// Strange error:
//   unexpected type found : ?
//   required: class or interface without bounds	

public class ErasureAndInheritance {
  @SuppressWarnings("unchecked")
  public static void main(String[] args) {
	// Derived1 is a raw type. References to generic type Derived1<T> should be parameterized
	Derived1 d1 = new Derived1();
	Derived2 d2 = new Derived2();
    Object obj = d2.get();
    d2.set(obj); // Warning here!
  }
} ///:~
