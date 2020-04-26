package com.tntrip.interview.ref;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class R_3_WeakReference {
    public static class Gfg {
        //code 
        public void x() {
            System.out.println("R_3_WeakReference --- Gfg");
        }

        /**
         * 被gc前，执行 该方法
         *
         * @throws Throwable
         */
        @Override
        protected void finalize() throws Throwable {
            System.out.println("R_3_WeakReference -- finalize before gc");
            super.finalize();
        }
    }

    /**
     * 某个对象 只存在 WeakReference 引用时
     * <p>
     * 只要gc，就回收 WeakReference 引用
     * <p>
     * <p>
     * Output:
     * <p>
     * GeeksforGeeks
     * GeeksforGeeks
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {
        // Strong Reference 
        Gfg g = new Gfg();

        // Creating Weak Reference to Gfg-type object to which 'g'  
        // is also pointing. 
        WeakReference<Gfg> weakRef = new WeakReference<>(g);

        //Now, Gfg-type object to which 'g' was pointing earlier 
        //is available for garbage collection. 
        //But, it will be garbage collected only when JVM needs memory. 
        g = null;

        // You can retrieve back the object which 
        // has been weakly referenced. 
        // It successfully calls the method. 
        weakRef.get().x();
        
        System.gc();
        Thread.sleep(1000);
        System.out.println("Already R_3_WeakReference gc");
        Thread.sleep(5000);

        weakRef.get().x();

        List<byte[]> ddd = new ArrayList<>();
        while (true) {
            ddd.add(new byte[5 * 1024 * 1024]);
        }
    }
}

