/**
 * Created by nuc on 2015/12/11.
 */
public class Singleton {
    private Singleton() {
    }

    //1. Good
    static class SingletonHolder {
        private static final Singleton instance = new Singleton();
    }

    public static Singleton getInstance() {
        return SingletonHolder.instance;
    }

    // 2. Good, but not lazily. But normally we can choose it
    private static Singleton INSTANCE2 = new Singleton();

    public static Singleton getInstance2() {
        return INSTANCE2;
    }

    //3. Bad
    private static Singleton INSTANCE1;

    public static Singleton getInstance1() {
        if (INSTANCE1 == null) {
            INSTANCE1 = new Singleton();
        }
        return INSTANCE1;
    }

    //4. Good(but looks scary). double-check. Item 71
    private static volatile Singleton INSTANCE3 = new Singleton();

    public static Singleton getInstance3() {
        Singleton local = INSTANCE3;
        if (local == null) { // first check (no locking)
            synchronized (Singleton.class) {
                local = INSTANCE3;
                if (local == null) { //second check (with locking)
                    INSTANCE3 = local = new Singleton();
                }
            }
        }
        return local;
    }
}

