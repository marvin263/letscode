package effective2.Chapter2.Item3.serializable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

// Serializable singleton with public final field - Page 18
public class Elvis implements Serializable {
	public static final Elvis INSTANCE = new Elvis();

	private Elvis() {
	}

	public void leaveTheBuilding() {
		System.out.println("Whoa baby, I'm outta here!");
	}

	 private Object readResolve() {
	 // Return the one true Elvis and let the garbage collector
	 // take care of the Elvis impersonator.
	 return INSTANCE;
	 }
	// Marvin： Singleton 必须得实现 Serializable。
	// 则，危险就来了，因为 deserialize 会创建出 另一个 完全不一样的 实例
	// 解决办法，就是加上 readResolve
	public static void main(String[] args) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(Elvis.INSTANCE);
		oos.close();

		ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
		ObjectInputStream ois = new ObjectInputStream(bis);
		Elvis deepCopied = (Elvis) ois.readObject();
		ois.close();

		System.out.println("Original: " + Elvis.INSTANCE);
		System.out.println("deepCopied: " + deepCopied);
	}
}
