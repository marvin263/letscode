package net.jcip.examples;

import net.jcip.annotations.NotThreadSafe;

/**
 * UnsafeLazyInitialization
 * <p/>
 * Unsafe lazy initialization
 * 
 * @author Brian Goetz and Tim Peierls
 */
@NotThreadSafe
public class UnsafeLazyInitialization {
	private static Resource resource;

	// Marvin: This is actually safe if Resource is immutable
	public static Resource getInstance() {
		if (resource == null)
			resource = new Resource(); // unsafe publication
		return resource;
	}

	static class Resource {
	}
}
