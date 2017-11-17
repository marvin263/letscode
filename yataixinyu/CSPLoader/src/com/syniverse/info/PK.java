package com.syniverse.info;

public class PK {
	public final String billingID;
	public final String subKey;

	private PK(String billingID, String subKey) {
		this.billingID = billingID;
		this.subKey = subKey;
	}

	public static PK create(String billingID, String subKey) {
		return new PK(billingID, subKey);
	}
	public static PK create(EachRowInfo erInfo) {
		return new PK(erInfo.billingID, erInfo.getSubscriberKey());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((billingID == null) ? 0 : billingID.hashCode());
		result = prime * result + ((subKey == null) ? 0 : subKey.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PK other = (PK) obj;
		if (billingID == null) {
			if (other.billingID != null)
				return false;
		} else if (!billingID.equals(other.billingID))
			return false;
		if (subKey == null) {
			if (other.subKey != null)
				return false;
		} else if (!subKey.equals(other.subKey))
			return false;
		return true;
	}

}
