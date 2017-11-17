package com.syniverse.info;

import java.util.ArrayList;

import com.syniverse.common.CommUtil;

/**
 * hashCode() and equals() only take <code>subkey</code> into account
 * 
 */
public class SubkeyAndTypeInfo {
	public final String subkey;
	public final ArrayList<Integer> listSubkeytype = new ArrayList<Integer>(3);

	public SubkeyAndTypeInfo(final String subkey) {
		this.subkey = subkey;
	}

	public boolean notExist() {
		return listSubkeytype.size() == 0;
	}

	public boolean moreThan1Type() {
		return listSubkeytype.size() > 1;
	}

	public boolean oneType_nice() {
		return listSubkeytype.size() == 1;
	}

	public int getSubkeytype() {
		if (oneType_nice() == true) {
			return listSubkeytype.get(0).intValue();
		}
		throw new RuntimeException(
				CommUtil.format(
						"Cannot fetch subketype for subkey={0}, listSubkeytype.size()={1}",
						subkey, listSubkeytype.size()));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((subkey == null) ? 0 : subkey.hashCode());
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
		SubkeyAndTypeInfo other = (SubkeyAndTypeInfo) obj;
		if (subkey == null) {
			if (other.subkey != null)
				return false;
		} else if (!subkey.equals(other.subkey))
			return false;
		return true;
	}

}
