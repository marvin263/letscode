import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Callable;

/**
 * Created by nuc on 2015/11/24.
 */
public class InnerClass {
    private static Runnable staticAnonymous = new Runnable() {
        @Override
        public void run() {
            System.out.println("staticAnonymous");
        }
    };
    private Runnable r = new Runnable() {
        @Override
        public void run() {
            System.out.println("anonymous");
        }
    };

    public void instanceMethod() {
        System.out.println(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return null;
            }
        });
        class LocalI {
        }
    }

    public static void staticMethod() {
        System.out.println(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        class LocalS {
        }
    }

    private static class YesImStatic {
        public void hithere() {
            System.out.println("Hi There");
        }
    }

    private class ImInstance {
        public void hithere() {
            System.out.println("Hi There");
        }
    }


    public static void main(String[] args) {
    }
}
