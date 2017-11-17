/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.openjdk.jmh.samples;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 10, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 10, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Threads(1)
@OperationsPerInvocation(10000)
@State(Scope.Thread)
public class LockElision_InstanceField {

    public static final String Server = "-server";
    public static final String Biased_DELAY = "-XX:BiasedLockingStartupDelay=0";

    public static final String Biased_ON = "-XX:+UseBiasedLocking";
    public static final String Biased_OFF = "-XX:-UseBiasedLocking";

    public static final String Escape_ON = "-XX:+DoEscapeAnalysis";
    public static final String Escape_OFF = "-XX:-DoEscapeAnalysis";

    public static final String ELIMINATE_ON = "-XX:+EliminateLocks";
    public static final String ELIMINATE_OFF = "-XX:-EliminateLocks";

    private SynchSB syncsb = new SynchSB();
    private UnsyncSB unsyncsb = new UnsyncSB();


    @Benchmark
    @Fork(jvmArgsAppend = {Biased_OFF, Escape_OFF, ELIMINATE_OFF}, jvmArgsPrepend = {Server, Biased_DELAY}, value = 1)
    public StringBuilder sycn_NoBiased_NoEscape_NoEliminate_0() {
        int count = 10000;
        for (int i = 0; i < count; i++) {
            syncsb.append("abc");
            syncsb.delete(0, syncsb.length());
        }
        return syncsb.sb;
    }

    @Benchmark
    @Fork(jvmArgsAppend = {Biased_OFF, Escape_OFF, ELIMINATE_ON}, jvmArgsPrepend = {Server, Biased_DELAY}, value = 1)
    public StringBuilder sycn_NoBiased_NoEscape_Eliminate_1() {
        int count = 10000;
        for (int i = 0; i < count; i++) {
            syncsb.append("abc");
            syncsb.delete(0, syncsb.length());
        }
        return syncsb.sb;
    }

    @Benchmark
    @Fork(jvmArgsAppend = {Biased_OFF, Escape_ON, ELIMINATE_OFF}, jvmArgsPrepend = {Server, Biased_DELAY}, value = 1)
    public StringBuilder sycn_NoBiased_Escape_NoEliminate_2() {
        int count = 10000;
        for (int i = 0; i < count; i++) {
            syncsb.append("abc");
            syncsb.delete(0, syncsb.length());
        }
        return syncsb.sb;
    }

    @Benchmark
    @Fork(jvmArgsAppend = {Biased_OFF, Escape_ON, ELIMINATE_ON}, jvmArgsPrepend = {Server, Biased_DELAY}, value = 1)
    public StringBuilder sycn_NoBiased_Escape_Eliminate_3() {
        int count = 10000;
        for (int i = 0; i < count; i++) {
            syncsb.append("abc");
            syncsb.delete(0, syncsb.length());
        }
        return syncsb.sb;
    }

    @Benchmark
    @Fork(jvmArgsAppend = {Biased_ON, Escape_OFF, ELIMINATE_OFF}, jvmArgsPrepend = {Server, Biased_DELAY}, value = 1)
    public StringBuilder sycn_Biased_NoEscape_NoEliminate_4() {
        int count = 10000;
        for (int i = 0; i < count; i++) {
            syncsb.append("abc");
            syncsb.delete(0, syncsb.length());
        }
        return syncsb.sb;
    }

    @Benchmark
    @Fork(jvmArgsAppend = {Biased_ON, Escape_OFF, ELIMINATE_ON}, jvmArgsPrepend = {Server, Biased_DELAY}, value = 1)
    public StringBuilder sycn_Biased_NoEscape_Eliminate_5() {
        int count = 10000;
        for (int i = 0; i < count; i++) {
            syncsb.append("abc");
            syncsb.delete(0, syncsb.length());
        }
        return syncsb.sb;
    }

    @Benchmark
    @Fork(jvmArgsAppend = {Biased_ON, Escape_ON, ELIMINATE_OFF}, jvmArgsPrepend = {Server, Biased_DELAY}, value = 1)
    public StringBuilder sycn_Biased_Escape_NoEliminate_6() {
        int count = 10000;
        for (int i = 0; i < count; i++) {
            syncsb.append("abc");
            syncsb.delete(0, syncsb.length());
        }
        return syncsb.sb;
    }

    @Benchmark
    @Fork(jvmArgsAppend = {Biased_ON, Escape_ON, ELIMINATE_ON}, jvmArgsPrepend = {Server, Biased_DELAY}, value = 1)
    public StringBuilder sycn_Biased_Escape_Eliminate_7() {
        int count = 10000;
        for (int i = 0; i < count; i++) {
            syncsb.append("abc");
            syncsb.delete(0, syncsb.length());
        }
        return syncsb.sb;
    }

    /*******************************************************************************************************/


    @Benchmark
    @Fork(jvmArgsAppend = {Biased_OFF, Escape_OFF, ELIMINATE_OFF}, jvmArgsPrepend = {Server, Biased_DELAY}, value = 1)
    public StringBuilder unsycn_NoBiased_NoEscape_NoEliminate_0() {
        int count = 10000;
        for (int i = 0; i < count; i++) {
            unsyncsb.append("abc");
            unsyncsb.delete(0, unsyncsb.length());
        }
        return unsyncsb.sb;
    }

    @Benchmark
    @Fork(jvmArgsAppend = {Biased_OFF, Escape_OFF, ELIMINATE_ON}, jvmArgsPrepend = {Server, Biased_DELAY}, value = 1)
    public StringBuilder unsycn_NoBiased_NoEscape_Eliminate_1() {
        int count = 10000;
        for (int i = 0; i < count; i++) {
            unsyncsb.append("abc");
            unsyncsb.delete(0, unsyncsb.length());
        }
        return unsyncsb.sb;
    }

    @Benchmark
    @Fork(jvmArgsAppend = {Biased_OFF, Escape_ON, ELIMINATE_OFF}, jvmArgsPrepend = {Server, Biased_DELAY}, value = 1)
    public StringBuilder unsycn_NoBiased_Escape_NoEliminate_2() {
        int count = 10000;
        for (int i = 0; i < count; i++) {
            unsyncsb.append("abc");
            unsyncsb.delete(0, unsyncsb.length());
        }
        return unsyncsb.sb;
    }

    @Benchmark
    @Fork(jvmArgsAppend = {Biased_OFF, Escape_ON, ELIMINATE_ON}, jvmArgsPrepend = {Server, Biased_DELAY}, value = 1)
    public StringBuilder unsycn_NoBiased_Escape_Eliminate_3() {
        int count = 10000;
        for (int i = 0; i < count; i++) {
            unsyncsb.append("abc");
            unsyncsb.delete(0, unsyncsb.length());
        }
        return unsyncsb.sb;
    }

    @Benchmark
    @Fork(jvmArgsAppend = {Biased_ON, Escape_OFF, ELIMINATE_OFF}, jvmArgsPrepend = {Server, Biased_DELAY}, value = 1)
    public StringBuilder unsycn_Biased_NoEscape_NoEliminate_4() {
        int count = 10000;
        for (int i = 0; i < count; i++) {
            unsyncsb.append("abc");
            unsyncsb.delete(0, unsyncsb.length());
        }
        return unsyncsb.sb;
    }

    @Benchmark
    @Fork(jvmArgsAppend = {Biased_ON, Escape_OFF, ELIMINATE_ON}, jvmArgsPrepend = {Server, Biased_DELAY}, value = 1)
    public StringBuilder unsycn_Biased_NoEscape_Eliminate_5() {
        int count = 10000;
        for (int i = 0; i < count; i++) {
            unsyncsb.append("abc");
            unsyncsb.delete(0, unsyncsb.length());
        }
        return unsyncsb.sb;
    }

    @Benchmark
    @Fork(jvmArgsAppend = {Biased_ON, Escape_ON, ELIMINATE_OFF}, jvmArgsPrepend = {Server, Biased_DELAY}, value = 1)
    public StringBuilder unsycn_Biased_Escape_NoEliminate_6() {
        int count = 10000;
        for (int i = 0; i < count; i++) {
            unsyncsb.append("abc");
            unsyncsb.delete(0, unsyncsb.length());
        }
        return unsyncsb.sb;
    }

    @Benchmark
    @Fork(jvmArgsAppend = {Biased_ON, Escape_ON, ELIMINATE_ON}, jvmArgsPrepend = {Server, Biased_DELAY}, value = 1)
    public StringBuilder unsycn_Biased_Escape_Eliminate_7() {
        int count = 10000;
        for (int i = 0; i < count; i++) {
            unsyncsb.append("abc");
            unsyncsb.delete(0, unsyncsb.length());
        }
        return unsyncsb.sb;
    }

    /*
     * ============================== HOW TO RUN THIS TEST: ====================================
     *
     * Note JMH honors the default annotation settings. You can always override
     * the defaults via the command line or API.
     *
     * You can run this test:
     *
     * a) Via the command line:
     *    $ mvn clean install
     *    $ java -jar target/benchmarks.jar JMHSample_20
     *
     * b) Via the Java API:
     *    (see the JMH homepage for possible caveats when running from IDE:
     *      http://openjdk.java.net/projects/code-tools/jmh/)
     */

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(LockElision_InstanceField.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }

}
