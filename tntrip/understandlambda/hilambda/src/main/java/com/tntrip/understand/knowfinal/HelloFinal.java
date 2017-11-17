package com.tntrip.understand.knowfinal;

/**
 * Created by libing2 on 2016/2/23.
 */
public class HelloFinal {

}


class X {

}

class C {
    final X f;

    C() {
        f = null;
    }
}
/**
 * <pre>
 *
 * T1 调用C构函，给f赋值
 * 在C构函结束前，T1 不 允许任何其他线程 读取f引用，然后，T2读取f
 *
 * 那么，
 * C构函结束前，T1所作的任何写入 对于 T2 读取f 以及 读取f之后的任何操作
 *
 * 都确保是有序的，并且确保是可见的
 *
 * Marvin：注意：这里的 “任何写入”，不止是在C构函中的写入，还包括T1在构函之前执行的所有写入操作，对于T2读取f之后的操作，都是有序的，且是可见的
 *
 * </pre>
 */

