package org.apache.activemq.book.ch13;


import com.sun.tools.attach.VirtualMachine;

/**
 * Simple attach-on-demand client tool that loads the given agent into the given Java process.
 */
public class Attacher {
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("usage: java Attach <pid> <agent-jar-full-path> [<agent-args>]");
            System.exit(1);
        }
        // JVM is identified by process id (pid).
        VirtualMachine vm = VirtualMachine.attach(args[0]);
        String agentArgs = (args.length > 2) ? args[2] : null;
        // load a specified agent onto the JVM
        vm.loadAgent(args[1], agentArgs);
    }
}