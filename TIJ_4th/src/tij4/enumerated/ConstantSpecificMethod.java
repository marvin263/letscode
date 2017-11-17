package tij4.enumerated;

//: enumerated/ConstantSpecificMethod.java
import java.text.DateFormat;
import java.util.Date;

public enum ConstantSpecificMethod {
	DATE_TIME {
		String getInfo() {
			return DateFormat.getDateInstance().format(new Date());
		}
	},
	CLASSPATH {
		String getInfo() {
			return System.getenv("CLASSPATH");
		}
	},
	VERSION {
		String getInfo() {
			return System.getProperty("java.version");
		}
	};
	// Marvin: 定义的抽象方法，就必须得 在 每个 enum instance 中去实现
	// table drove code
	abstract String getInfo();

	public static void main(String[] args) {
		for (ConstantSpecificMethod csm : ConstantSpecificMethod.values()) {
			// 当然，这里的 cms 就是 enum实例
			System.out.println(csm.getInfo());
		}
	}
}
