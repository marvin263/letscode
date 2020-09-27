package com.tntrip.docases;

import java.util.ArrayList;
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
        ArrayList<Class<?>> declaredClasses = ClassUtil.getAllClassByInterface(EachCase.class);
        for (Class<?> dclCls : declaredClasses) {
            Class<?>[] allImpl = dclCls.getInterfaces();
            for (Class<?> aImpl : allImpl) {
                if (EachCase.class.equals(aImpl)) {
                    try {
                        EachCase ec = LsCase.class.equals(dclCls) ?
                                ((EachCase) dclCls.getConstructors()[0].newInstance(this))
                                : ((EachCase) dclCls.getConstructors()[0].newInstance());
                        
                        System.out.println(count + ". Adding: " + ec.prefix()[0]);
                        count++;
                        mapCases.put(ec.prefix()[0], ec);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
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


    public static void run(AbstractDoCases aec) {
        aec.endlesslyRun();
    }

    public static void main(String[] args) {
        //run(new AbstractDoCases());
    }
}
