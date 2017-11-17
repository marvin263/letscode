package effective2.Chapter3.Item11;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;

public class Shadow_or_Deep implements Cloneable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Date d = new Date();

	public String toString() {
		return d.toLocaleString();
	}

	public static void main(String[] args) throws Exception {
		serializable_deep_copy();
		clone_shadow_copy();
	}

	public static void serializable_deep_copy() throws Exception {
		Shadow_or_Deep original = new Shadow_or_Deep();
		System.out.println("Original: " + original);

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(original);
		oos.close();

		original.d.setDate(2015 - 1900);
		System.out.println("After serialize, change original to: " + original);

		Shadow_or_Deep restored = (Shadow_or_Deep) new ObjectInputStream(
				new ByteArrayInputStream(bos.toByteArray())).readObject();
		System.out.println("restored: " + restored);
	}

	public static void clone_shadow_copy() {
		Shadow_or_Deep original = new Shadow_or_Deep();
		System.out.println("Original: " + original);
		Shadow_or_Deep newCloned = original.clone();
		System.out.println("newCloned: " + newCloned);

		original.d.setDate(2015 - 1900);
		System.out.println("After change original, original: " + original);
		System.out.println("After change original, newCloned: " + newCloned);

	}

	public Shadow_or_Deep clone() {
		Shadow_or_Deep newCloned = null;
		try {
			newCloned = (Shadow_or_Deep) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return newCloned;
	}

}
