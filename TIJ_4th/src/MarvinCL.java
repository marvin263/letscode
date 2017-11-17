import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

public class MarvinCL extends ClassLoader {

	protected Class<?> findClass(String name) throws ClassNotFoundException {
		String fullPath = "/home/marvin/appcode/AppCode.class";
		try {
			byte[] byteContent = readFile2Bytes(new FileInputStream(fullPath));
			return defineClass("AppCode", byteContent, 0, byteContent.length);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private byte[] readFile2Bytes(FileInputStream fis) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int len = -1;
		while ((len = fis.read(buf)) != -1) {
			baos.write(buf, 0, len);
		}
		byte[] array = baos.toByteArray();
		baos.close();
		return array;
	}
}
