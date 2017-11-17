import java.io.Serializable;

public class CopyOfEqualsButtoStringNot implements Serializable {
	private static final long serialVersionUID = 1L;

	public CopyOfEqualsButtoStringNot() {
	}

	public boolean equals(Object obj) {
		boolean bSource = obj instanceof EqualsButtoStringNot;
		return bSource;
	}

	public String toString() {
		return "We are the same, haha";
	}
}
