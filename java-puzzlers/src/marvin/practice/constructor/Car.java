package marvin.practice.constructor;

/**
 * Marvin: 简单总结就一句话“instance initializer抛啥 check exception，构函要给人家擦屁股”
 * 
 */
public class Car {
	private static Class engineClass = null;
	private Engine engine = (Engine) engineClass.newInstance();

	// Marvin：instance initializer的所有exception都会被 propagate到constructor
	// 所以说，如果人家有check exception，你构函就要负责擦屁股
	public Car() throws InstantiationException, IllegalAccessException {
		//构函是empty的，却依然需要抛出两个异常
	}
}

class Engine {

}
