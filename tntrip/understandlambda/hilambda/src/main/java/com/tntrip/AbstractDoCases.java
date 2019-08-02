package com.tntrip;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public abstract class AbstractDoCases {
    private Map<String, EachCase> mapCases = new HashMap<>();

    private void endlesslyRun() {
        addCases();
        Scanner scn = new Scanner(System.in);
        while (true) {
            String line = scn.nextLine();
            boolean match = false;
            for (Map.Entry<String, EachCase> entry : mapCases.entrySet()) {
                if (line.startsWith(entry.getKey())) {
                    match = true;
                    try {
                        entry.getValue().doOnLine(line);
                    } catch (Exception e) {
                        System.out.println("Exception happened. prefix=" + line);
                        e.printStackTrace();
                    }
                    break;
                }
            }
            if (!match) {
                System.out.println("Hits nothing\n");
            }
        }
    }

    private void addCases() {
        int count = 1;
        Class<?> cur = this.getClass();
        while (AbstractDoCases.class.isAssignableFrom(cur)) {
            Class<?>[] declaredClasses = cur.getDeclaredClasses();
            for (Class<?> dclCls : declaredClasses) {
                Class<?>[] allImpl = dclCls.getInterfaces();
                for (Class<?> aImpl : allImpl) {
                    if (EachCase.class.equals(aImpl)) {
                        try {
                            EachCase ec = (EachCase) dclCls.getConstructors()[0].newInstance(this);
                            System.out.println(count + ". Adding: " + ec.prefix());
                            count++;
                            mapCases.put(ec.prefix(), ec);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
            if (cur == AbstractDoCases.class) {
                break;
            }
            cur = cur.getSuperclass();
        }
    }


    public interface EachCase {
        void doOnLine(String line);

        String prefix();
    }

    public class ExitCase implements EachCase {
        @Override
        public void doOnLine(String line) {
            System.exit(0);
        }

        @Override
        public String prefix() {
            return "exit";
        }
    }

    public class QuitCase implements EachCase {
        @Override
        public void doOnLine(String line) {
            System.exit(0);
        }

        @Override
        public String prefix() {
            return "quit";
        }
    }


    public class GcCase implements EachCase {
        @Override
        public void doOnLine(String line) {
            System.gc();
            System.out.println("gc done");
            System.out.println();
        }

        @Override
        public String prefix() {
            return "gc";
        }
    }


    public static void run(AbstractDoCases aec) {
        aec.endlesslyRun();
    }

    public static void main(String[] args) {
        //run(new AbstractDoCases());
    }
}
