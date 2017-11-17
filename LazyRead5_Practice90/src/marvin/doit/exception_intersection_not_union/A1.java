package marvin.doit.exception_intersection_not_union;

import java.io.IOException;

/**
 * Marvin: 继承时，对于 checked exception（即，method declaration中明确 throws的 exception）
 * 
 * 只能越来越少（intersection, not the union）
 * 
 * 正式的说法是这样的：
 * 
 * The set of checked exceptions that a method can throw is the intersection of
 * the sets of checked exceptions that it is declared to throw in all applicable
 * types, not the union
 * 
 * @author g705346
 * 
 */
public interface A1 extends A0 {
	// Marvin: 这里 throws 出去的，只能越来越少
	public void method() throws IOException;
}
