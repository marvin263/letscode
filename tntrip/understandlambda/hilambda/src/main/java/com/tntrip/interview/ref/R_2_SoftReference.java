package com.tntrip.interview.ref;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

public class R_2_SoftReference {
    public static class Gfg {
        //code 
        public void x() {
            System.out.println("R_2_SoftReference --- Gfg");
        }

        /**
         * 被gc前，执行 该方法
         *
         * @throws Throwable
         */
        @Override
        protected void finalize() throws Throwable {
            System.out.println("R_2_SoftReference -- finalize before gc");
            super.finalize();
        }
    }

    /**
     * 某个对象 只存在 SoftReference 引用时
     * <p>
     * gc时 并不会 回收
     * <p>
     * 但，即将OOM前，就会把SoftReference回收掉
     *
     * @param args
     */
    public static void main(String[] args) throws Exception{
        // Strong Reference 
        Gfg g = new Gfg();

        // Creating Soft Reference to Gfg-type object to which 'g'  
        // is also pointing. 
        SoftReference<Gfg> softRef = new SoftReference<>(g);
        // Now, Gfg-type object to which 'g' was pointing 
        // earlier is available for garbage collection. 
        g = null;

        // You can retrieve back the object which 
        // has been weakly referenced. 
        // It successfully calls the method. 

        softRef.get().x();

        System.gc();
        Thread.sleep(1000);
        System.out.println("Already R_2_SoftReference gc");
        Thread.sleep(5000);
        softRef.get().x();
        
        
        System.out.println("Cost much memory");
        List<byte[]> ddd = new ArrayList<>();
        while (true) {
            ddd.add(new byte[5 * 1024 * 1024]);
        }
    }
}

