package marvin.doit.clone_shallow_copy;

public class ShallowCopyClone implements Cloneable {
	public StringBuffer sb = new StringBuffer("Java");

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public String toString() {
		return getClass().getName() + "@" + Integer.toHexString(hashCode())
				+ ", " + sb;
	}

	public static void main(String[] args) throws Exception {
		ShallowCopyClone orgn = new ShallowCopyClone();
		ShallowCopyClone cloneCopy = (ShallowCopyClone) orgn.clone();

		System.out.println(orgn);
		System.out.println(cloneCopy);

		orgn.sb.append("C++");
		System.out.println(orgn);
		System.out.println(cloneCopy);
	}
}
