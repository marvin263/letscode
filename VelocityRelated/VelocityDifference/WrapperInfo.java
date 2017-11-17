import java.io.Serializable;

public class WrapperInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	public String str;
	public int intValue;
	public boolean bValue;

	public WrapperInfo(String str, int intValue, boolean bValue) {
		this.str = str;
		this.intValue = intValue;
		this.bValue = bValue;
	}

	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}

	public int getIntValue() {
		return intValue;
	}

	public void setIntValue(int intValue) {
		this.intValue = intValue;
	}

	public boolean isbValue() {
		return bValue;
	}

	public void setbValue(boolean bValue) {
		this.bValue = bValue;
	}
	// @Override
	// public String toString() {
	// return "abc";
	// }
}
