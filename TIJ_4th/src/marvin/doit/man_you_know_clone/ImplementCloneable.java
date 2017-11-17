package marvin.doit.man_you_know_clone;

public class ImplementCloneable implements Cloneable {
	private int i;
	private String str;

	public ImplementCloneable() {
		i = 1;
		str = "I'm String";
		System.out.println("In constructor");
	}

	@Override
	public String toString() {
		return "i=" + i + ", str=" + str;
	}

	@Override
	public ImplementCloneable clone() {
		ImplementCloneable result = null;
		
		try {
			result = (ImplementCloneable) super.clone();
		} catch (CloneNotSupportedException e) {
		}
		
		return result;
	}

	public static void main(String[] args) {
		System.out.println("Original");
		ImplementCloneable orgn = new ImplementCloneable();
		System.out.println(orgn);
		System.out.println();

		ImplementCloneable aCopy = (ImplementCloneable) orgn.clone();
		System.out.println("Output a copy");
		System.out.println(aCopy);
	
	}
}
