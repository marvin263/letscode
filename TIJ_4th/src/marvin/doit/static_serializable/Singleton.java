package marvin.doit.static_serializable;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 这个文件用来说明两个问题
 * 
 * 1. 即使你 private了构函，却依然不能保证 只有一个实例。因为 Serializable 是 “另一个构函，一个无需调用构函的构函”
 * 
 * 2. static 的 variable 是 不能 序列化的
 * 
 * @author g705346
 * 
 */
public class Singleton implements Serializable {
	private static final long serialVersionUID = 1L;
	private static String CLASS_VARIABLE = "INITIAL CLASS_FIELD";
	private static Singleton INSTANCE = new Singleton();
	private String instanceField = "INITIAL INSTANCE_FIELD";

	private Singleton() {
		System.out.println("In constructor");
	}

	public static Singleton getInstance() {
		return INSTANCE;
	}

	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss-SSS");
		String timestamp = sdf.format(new Date());
		StringBuilder sb = new StringBuilder();
		sb.append(timestamp);
		sb.append(", instanceField=" + instanceField);
		sb.append(", CLASS_VARIABLE=" + CLASS_VARIABLE);
		return sb.toString();
	}

	public static void saveObject(Singleton instance) throws Exception {
		String tmpFile = "Singleton.tmp";
		instance.instanceField = "save--instancefield";
		Singleton.CLASS_VARIABLE = "save--CLASS_VARIABLE";
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
				tmpFile));
		oos.writeObject(instance);
	}

	public static Singleton recoverObject() throws Exception {
		String tmpFile = "Singleton.tmp";
		// recover
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
				tmpFile));
		Singleton copy = (Singleton) ois.readObject();
		return copy;
	}

	// Marvin: 只运行 main2()，你会发现：CLASS_VARIABLE 确实没有 被序列化到文件中
	public static void main(String[] args) throws Exception {
		// main1();
		main2();
	}

	private static void main1() throws Exception {
		Singleton orgn = Singleton.getInstance();
		System.out.println("Original:\n" + orgn);
		saveObject(orgn);
		Thread.sleep(1500);
		// recover
		Singleton copy = recoverObject();
		System.out.println("Copy:\n" + copy);
	}

	private static void main2() throws Exception {
		// recover
		Singleton copy = recoverObject();
		System.out.println("Copy:\n" + copy);
	}
}
