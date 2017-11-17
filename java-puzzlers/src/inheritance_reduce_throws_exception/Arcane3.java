package inheritance_reduce_throws_exception;
import java.io.IOException;
import java.sql.SQLException;

//Marvin: 父子类时，子类 override一个方法，“此方法在祖先类中出现多次，且每个祖先类抛出的异常可能不同”，那么，此子类中应该抛出什么样的 异常 呢？
// 答案是： 所有祖先类的 “焦急即可”，交集有三个，三个里面的任意组合即可（不抛出也行）。但，如果不是这三个里面的异常，则 compile-error
interface Type1 {
	void f() throws CloneNotSupportedException, IOException,
			InterruptedException, ClassNotFoundException;
}

interface Type2 {
	void f() throws SQLException, InterruptedException, ClassNotFoundException;
}

interface Type3 extends Type1, Type2 {
}

public class Arcane3 implements Type3 {
	public void f() throws ClassNotFoundException {
		System.out.println("Hello world");
	}

	public static void main(String[] args) throws Exception {
		Type3 t3 = new Arcane3();
		t3.f();
	}
}
