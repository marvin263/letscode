package tij4.io;

//: io/StoreCADState.java
// Saving the state of a pretend CAD system.
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

abstract class Shape implements Serializable {
	public static final int RED_1 = 1, BLUE_2 = 2, GREEN_3 = 3;
	private int xPos, yPos;

	public abstract void setColor(int newColor);

	public abstract int getColor();

	public Shape(int xVal, int yVal) {
		xPos = xVal;
		yPos = yVal;
	}

	private static int counter = 0;

	public static Shape randomFactory() {
		Random rand = new Random();
		int xVal = rand.nextInt(100);
		int yVal = rand.nextInt(100);
		switch (counter++ % 3) {
		default:
		case 0:
			return new Circle(xVal, yVal);
		case 1:
			return new Square(xVal, yVal);
		case 2:
			return new Line(xVal, yVal);
		}
	}

	public String toString() {
		return getClass().getSimpleName() + " color[" + getColor() + "] xPos["
				+ xPos + "] yPos[" + yPos + "]\n";
	}
}

class Circle extends Shape {
	// Marvin: 恢复时，不动 static 变量
	// 1. 如果 <clinit> 尚未运行（即，被反序列化的类还没有被load--link--initialize.<clinit>），则运行
	// <clinit>。
	// 在 <clinit> 中，static 可能被改变
	// 2. 如果 <clinit> 已经运行过，static 保持原来的值
	private static int color = RED_1;

	public Circle(int xVal, int yVal) {
		super(xVal, yVal);
	}

	public void setColor(int newColor) {
		color = newColor;
	}

	public int getColor() {
		return color;
	}
}

class Square extends Shape {
	private static int color;

	// Marvin: 构函内的 static 值，在 发序列化时 不会触及
	public Square(int xVal, int yVal) {
		super(xVal, yVal);
		color = RED_1;
	}

	public void setColor(int newColor) {
		color = newColor;
	}

	public int getColor() {
		return color;
	}
}

class Line extends Shape {
	private static int color = BLUE_2;

	public static void serializeStaticState(ObjectOutputStream os)
			throws IOException {
		os.writeInt(99);
	}

	// Marvin: 这里是赖皮。就是把一个数字 送与Line.color 啦
	public static void deserializeStaticState(ObjectInputStream os)
			throws IOException {
		color = os.readInt();
	}

	public Line(int xVal, int yVal) {
		super(xVal, yVal);
	}

	public void setColor(int newColor) {
		color = newColor;
	}

	public int getColor() {
		return color;
	}
}

public class StoreCADState {
	public static void main(String[] args) throws Exception {
		// Marvin: List<? extends/super Shape> 才符合 PECS 原则。注意看，这个可不一样哦
		List<Class<? extends Shape>> shapeTypes = new ArrayList<Class<? extends Shape>>();
		// Add references to the class objects:
		shapeTypes.add(Circle.class);
		shapeTypes.add(Square.class);
		shapeTypes.add(Line.class);

		List<Shape> shapes = new ArrayList<Shape>();
		// Make some shapes:
		for (int i = 0; i < 3; i++) {
			Shape s = Shape.randomFactory();
			// Set all the static colors to GREEN:
			s.setColor(Shape.GREEN_3);
			shapes.add(s);
		}

		// Save the state vector:
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(
				"CADState.out"));
		// 1. Marvin: 写入 shpeTypes, xxx.class
		out.writeObject(shapeTypes);
		// 2. Marvin: 写入 一个int值
		Line.serializeStaticState(out);
		// 3. Marvin: 写入 shapes
		// 4. Marvin: 请转到： RecoverCADState.java，你会发现，反序列化时：read 和
		// write的顺序保持严格一致
		out.writeObject(shapes);
		// Display the shapes:
		System.out.println(shapes);
	}
}