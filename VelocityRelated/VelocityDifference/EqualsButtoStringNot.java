import java.io.Serializable;

public class EqualsButtoStringNot implements Serializable {
	private static final long serialVersionUID = 1L;

	public EqualsButtoStringNot() {
	}

	public boolean equals(Object obj) {
		boolean bSource = obj instanceof CopyOfEqualsButtoStringNot;
		return bSource;
	}

	public String toString() {
		return "We are the same, haha";
	}
}
