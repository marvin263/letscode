package com.syniverse.info;

/**
 * PK of T_PCS_XREF_GRP_SUB
 * 
 * @author g705346
 * 
 */
public class PK {
	public final long groupID;
	public final int subkeyType;
	public final String subkey;

	private PK(final long groupID, final int subkeyType, final String subkey) {
		this.groupID = groupID;
		this.subkeyType = subkeyType;
		this.subkey = subkey;
	}

	public static PK create(final long groupID, final int subkeyType,
			final String subkey) {
		return new PK(groupID, subkeyType, subkey);
	}

	public static PK create(final long groupID, EachRowInfo erInfo) {
		return create(groupID, erInfo.getSubkeyType(), erInfo.subkey);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (groupID ^ (groupID >>> 32));
		result = prime * result + ((subkey == null) ? 0 : subkey.hashCode());
		result = prime * result + subkeyType;
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
		if (groupID != other.groupID)
			return false;
		if (subkey == null) {
			if (other.subkey != null)
				return false;
		} else if (!subkey.equals(other.subkey))
			return false;
		if (subkeyType != other.subkeyType)
			return false;
		return true;
	}

}
