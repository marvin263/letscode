package com.tntrip.rwsocket;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 单线程基本处理器，I/O 的读写以及业务的处理均由 Reactor 线程完成
 *
 * @author tongwu.net
 * @see SimpleReactor
 */
public class IOEventHandler implements Runnable {

    private final Object lock = new Object();

    private static Executor WORKER_POOL = Executors.newFixedThreadPool(5);

    // 定义服务的逻辑状态
    public static final int READING = 0, SENDING = 1, CLOSED = 2;
    // 缓存每次读取的内容
    private final StringBuilder request = new StringBuilder();

    private static final int MAXIN = 1024;
    private static final int MAXOUT = 1024;

    protected SocketChannel sc;
    protected SelectionKey selectionKey;

    protected ByteBuffer input = ByteBuffer.allocate(MAXIN);
    protected ByteBuffer output = ByteBuffer.allocate(MAXOUT);

    protected int state = READING;

    public IOEventHandler(SocketChannel sc, SelectionKey selectionKey) {
        this.sc = sc;
        this.selectionKey = selectionKey;
    }

    public void handleEvent() {
        WORKER_POOL.execute(this);
    }

    @Override
    public void run() {
        try {
            // 此时通道已经准备好读取字节
            if (state == READING) {
                read();
            }
            // 此时通道已经准备好写入字节
            else if (state == SENDING) {
                send();
            }
        } catch (IOException ex) {
            // 关闭连接
            try {
                selectionKey.channel().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void read() throws IOException {
        // 为什么要同步？Processor 线程处理时通道还有可能有读事件发生
        // 保护 input ByteBuffer 不会重置和状态的可见性
        // 应该是这样
        synchronized (lock) {
            input.clear();
            int n = sc.read(input);
            if (inputIsComplete(n)) {

                // 发送交给 Reactor 触发
                state = SENDING;
                selectionKey.interestOps(SelectionKey.OP_WRITE);

                // 这里需要唤醒 Selector，因为当把处理交给 workpool 时，Reactor 线程已经阻塞在 select() 方法了， 注意
                // 此时该通道感兴趣的事件还是 OP_READ，这里将通道感兴趣的事件改为 OP_WRITE
                // 如果不唤醒的话，就只能在 下次select 返回时才能有响应了，当然了也可以在 select 方法上设置超时
                selectionKey.selector().wakeup();
            }
        }
    }

    protected void send() throws IOException {
        int written = -1;
        // 切换到读取模式，判断是否有数据要发送
        output.flip();
        if (output.hasRemaining()) {
            written = sc.write(output);
        }

        // 检查连接是否处理完毕，是否断开连接
        if (outputIsComplete(written)) {
            selectionKey.channel().close();
        } else {
            // 否则继续读取
            state = READING;
            // 把提示发到界面
            sc.write(ByteBuffer.wrap("\r\nreactor> ".getBytes()));
            selectionKey.interestOps(SelectionKey.OP_READ);
        }
    }

    /**
     * 当读取到 \r\n 时表示结束
     *
     * @param bytes 读取的字节数，-1 通常是连接被关闭，0 非阻塞模式可能返回
     * @throws IOException
     */
    protected boolean inputIsComplete(int bytes) throws IOException {
        if (bytes > 0) {
            input.flip(); // 切换成读取模式
            while (input.hasRemaining()) {
                byte ch = input.get();

                if (ch == 3) { // ctrl+c 关闭连接
                    state = CLOSED;
                    return true;
                } else if (ch == '\r') { // continue
                } else if (ch == '\n') {
                    // 读取到了 \r\n 读取结束
                    state = SENDING;
                    return true;
                } else {
                    request.append((char) ch);
                }
            }
        } else if (bytes == -1) {
            // -1 客户端关闭了连接
            throw new EOFException();
        } else {
        } // bytes == 0 继续读取
        return false;
    }


    /**
     * 当用户输入了一个空行，表示连接可以关闭了
     */
    protected boolean outputIsComplete(int written) {
        if (written <= 0) {
            // 用户只敲了个回车， 断开连接
            return true;
        }

        // 清空旧数据，接着处理后续的请求
        output.clear();
        request.delete(0, request.length());
        return false;
    }
}
