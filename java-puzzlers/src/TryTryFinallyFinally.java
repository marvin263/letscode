/**
 * Marvin:
 * 
 * From the Java Language Specification 14.20.2.:
 * 
 * If execution of the try block completes abruptly for any other reason R, then
 * the finally block is executed, and then there is a choice:
 * 
 * <p>
 * If the finally block completes normally, then the try statement completes
 * abruptly for reason R.
 * 
 * <p>
 * If the finally block completes abruptly for reason S, then the try statement
 * completes abruptly for reason S (and reason R is discarded).
 * 
 * 
 * 
 * 
 * 
 * 
 */
public class TryTryFinallyFinally {
	public static void main(String[] args) {
		try {
			try {
				System.out.print("A");
				throw new Exception("1");
			} catch (Exception e) {
				System.out.print("B");
				throw new Exception("2");
			} finally {
				System.out.print("C");
				throw new Exception("3");
			}
		} catch (Exception e) {
			System.out.print(e.getMessage());
		}
	}
}
