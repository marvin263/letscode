package net.jcip.examples;

/**
 * StuffIntoPublic
 * <p>
 * Unsafe publication
 *
 * @author Brian Goetz and Tim Peierls
 */
public class StuffIntoPublic {
    public Holder holder;

    public void initialize() {
        holder = new Holder(42);
    }

    public static class AssignmentTask implements Runnable {
        private StuffIntoPublic sip;

        public AssignmentTask(StuffIntoPublic sip) {
            this.sip = sip;
        }

        @Override
        public void run() {
            sip.initialize();
        }
    }

    public static class AssertTask implements Runnable {
        private StuffIntoPublic sip;

        public AssertTask(StuffIntoPublic sip) {
            this.sip = sip;
        }

        @Override
        public void run() {
            while (sip.holder != null) {
                sip.holder.assertSanity();// 会抛出异常
            }
        }
    }

    public static void main(String[] args) {
        StuffIntoPublic sip = new StuffIntoPublic();
        Thread assertThread = new Thread(new AssignmentTask(sip));
        Thread assignmentThread = new Thread(new AssignmentTask(sip));
        assertThread.start();
        assignmentThread.start();
    }
}
