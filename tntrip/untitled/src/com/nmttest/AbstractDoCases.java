package com.nmttest;

import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public abstract class AbstractDoCases {
    private Map<String, EachCase> mapCases = new TreeMap<>();

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
                            System.out.println(count + ". Adding: " + ec.prefix()[0]);
                            count++;
                            mapCases.put(ec.prefix()[0], ec);
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

        String[] prefix();
    }

    public class LsCase implements EachCase {
        @Override
        public void doOnLine(String line) {
            StringBuilder sb = new StringBuilder();
            int i = 1;
            for (Map.Entry<String, EachCase> entry : mapCases.entrySet()) {
                sb.append(String.format("%02d", i))
                        .append(": ")
                        .append(String.format("%-7s", (entry.getValue().prefix()[0]) + ","))
                        .append(entry.getValue().prefix()[1])
                        .append("\n");
                i++;
            }
            System.out.print(sb.toString());
        }

        @Override
        public String[] prefix() {
            return new String[]{"ls", "List all the cases"};
        }
    }

    public class ExitCase implements EachCase {
        @Override
        public void doOnLine(String line) {
            System.exit(0);
        }

        @Override
        public String[] prefix() {
            return new String[]{"exit", "Exit"};
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
        public String[] prefix() {
            return new String[]{"gc", "gc"};
        }
    }


    public static void run(AbstractDoCases aec) {
        aec.endlesslyRun();
    }

    public static void main(String[] args) {
        //run(new AbstractDoCases());
    }
}
