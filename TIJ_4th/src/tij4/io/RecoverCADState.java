package tij4.io;

//: io/RecoverCADState.java
// Restoring the state of the pretend CAD system.
// {RunFirst: StoreCADState}
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.List;

public class RecoverCADState {
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		// 如果没有这一句，deserialize时，<clinit>会将 Circle.color设为 1
		// 加上这一句，在deserialize前，就加载并初始化了类Circle, color==1
		// 紧接着，修改 color==705346
		// deserialize就不需要 <clinit>啦（下面代码已经使得<clinit>执行过了），于是，最终就是 705346
		new Circle(1, 2).setColor(705346);
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(
				"CADState.out"));
		// Read in the same order they were written:
		// 1. Marvin: 读出 shapeTypes, xxx.class
		List<Class<? extends Shape>> shapeTypes = (List<Class<? extends Shape>>) in
				.readObject();
		// 2. Marvin: 读出 一个 int。这是赖皮，强制 将一个值 写入了 Line.Color
		Line.deserializeStaticState(in);
		// 3. Marvin: 读出 shapes
		// 4. Marvin: 反序列化时，这里read的顺序，和 StoreCADState.java 中 write 的顺序保持严格一致
		List<Shape> shapes = (List<Shape>) in.readObject();
		// Marvin: 最终的结论是这样的：
		// deserialize时，根本不理睬 static class variable。 序列化，绝不理睬 static class
		// variable
		// 只是，如果 被恢复的类 尚未 load-link(verify, prepare, resolve)-initialize，
		// 则 在 initialize，也就是 <clinit> 中，设置 static 的正确值（基于类型的 初始值，在
		// prepare时被设置）。again，这是类加载自己的事情，
		// 和 序列化/反序列化 毫无关系
		System.out.println(shapes);
	}
}