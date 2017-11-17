class DogMarvin {
	public static void bark() {
		System.out.println("woof");
	}
}

class Basenji extends DogMarvin {
	// Marvin: 最应该注意其实在这里：
	// 和 instance method 一样：父子类的 static方法 签名完全一样
	// 和 instance method 不同：instance method override。但，static method 不存在
	// shadow的情况
	public static void bark() {
		System.out.println("I say nothing :(");
	}
}

public class Bark {
	public static void main(String args[]) {
		DogMarvin woofer = new DogMarvin();
		Basenji nipper = new Basenji();
		woofer.bark();// woof
		// Marvin: static的方法，都是 compile-time dispatch 的
		// 1. nipper如果cast成 DogMarvin-->woof
		// 2. nipper如果cast成 Basenji-->I say nothing :(
		((Basenji) nipper).bark();
		((DogMarvin) nipper).bark();
	}
}
