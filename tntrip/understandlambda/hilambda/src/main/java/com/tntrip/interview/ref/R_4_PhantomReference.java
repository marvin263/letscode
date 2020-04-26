package com.tntrip.interview.ref;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;

public class R_4_PhantomReference {
    public static class Gfg {
        //code 
        public void x() {
            System.out.println("R_4_PhantomReference --- Gfg");
        }

        /**
         * 被gc前，执行 该方法
         *
         * @throws Throwable
         */
        @Override
        protected void finalize() throws Throwable {
            System.out.println("R_4_PhantomReference -- finalize before gc");
            super.finalize();
        }
    }

    /**
     * 它的存在，对gc没有任何影响
     * <p>
     * 即将gc前，收集器把它放入 referenceQueue
     * 
     * 为一个对象设置 虚引用 关联的唯一目的只是：
     * 为了能在这个对象被 收集器 回收时 收到一个系统通知
     * 
     * <p>
     * Output:
     * GeeksforGeeks
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {
        //Strong Reference 
        Gfg g = new Gfg();
        g.x();

        //Creating reference queue 
        ReferenceQueue<Gfg> refQueue = new ReferenceQueue<>();

        //Creating Phantom Reference to Gfg-type object to which 'g'  
        //is also pointing. 
        PhantomReference<Gfg> phantomRef = new PhantomReference<>(g, refQueue);

        //Now, Gfg-type object to which 'g' was pointing 
        //earlier is available for garbage collection. 
        //But, this object is kept in 'refQueue' before  
        //removing it from the memory. 
        g = null;

//        System.gc();
//        System.out.println("Already done R_4_PhantomReference gc");
        
        
        Reference<? extends Gfg> r = refQueue.remove();
        //It shows NullPointerException.
        r.get().x();

        //It always returns null.  
        g = phantomRef.get();
        //It shows NullPointerException. 
        //g.x();
    }
}

