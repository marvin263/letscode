package com.tntrip.interview.ref;

public class R_1_StrongReference {
    public static class Gfg {
        //code 
        public void x() {
            System.out.println("R_1_StrongReference --- Gfg");
        }

        /**
         * 被gc前，执行 该方法
         *
         * @throws Throwable
         */
        @Override
        protected void finalize() throws Throwable {
            System.out.println("R_1_StrongReference -- finalize before gc");
            super.finalize();
        }
    }

    /**
     * Output:
     * <p>
     * GeeksforGeeks
     * GeeksforGeeks
     *
     * @param args
     */
    public static void main(String[] args) {
        //Strong Reference - by default 
        Gfg g = new Gfg();

        //Now, object to which 'g' was pointing earlier is  
        //eligible for garbage collection. 
        g = null;
        System.gc();
    }
}

