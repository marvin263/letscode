package marvin.doit.propagate_to_constructor;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * 两个事情： 1. instance initializers run before constructor bodies
 * 
 * 2. Any exception thrown by instance initializers PROPAGATE to constructor
 * 
 * 
 * @author g705346
 * 
 */
public class Empty {
	private FileInputStream fis = new FileInputStream("aaa");

	public Empty()
//	 throws IOException
	{

	}
}
