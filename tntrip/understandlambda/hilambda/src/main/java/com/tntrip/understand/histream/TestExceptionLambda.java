package com.tntrip.understand.histream;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * lambda执行中的异常，当然是正常抛出
 * <p>
 * lambda和异常不对付只在一个地方：那就是 FunctionInterface不能存在 checked exception。仅仅这一点而已
 *
 * @Author libing2
 * @Date 2021/6/19
 */
public class TestExceptionLambda {

    public static void main(String[] args) {
        TestExceptionLambda texla = new TestExceptionLambda();
        texla.testException();
    }

    private void testException() {
        mDoIt((e) -> mNothing());

        mDoIt((e) -> {
            try {
                mCheckedException();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        mDoIt((e) -> {
            try {
                mIOException();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    private void mNothing() {
        System.out.println("LovelyLambda.mNothing");
    }

    private void mCheckedException() throws Exception {
        throw new Exception("LovelyLambda.mCheckedException");

    }

    private void mIOException() throws IOException {
        System.out.println("LovelyLambda.mIOException");
        throw new IOException("LovelyLambda.mIOException");
    }

    private <T> void mDoIt(Consumer<T> consumerFunc) {
        consumerFunc.accept(null);
    }
}
