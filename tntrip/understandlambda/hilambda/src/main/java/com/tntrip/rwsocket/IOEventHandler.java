package com.tntrip.rwsocket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    private static final Executor WORKER_POOL = Executors.newFixedThreadPool(1);

    // 定义服务的逻辑状态
    public static final int READING = 0, SENDING = 1;
    // 缓存每次读取的内容
    private final StringBuilder request = new StringBuilder();

    private static final int MAXIN = 1024;
    private static final int MAXOUT = 1024;

    protected SocketChannel sc;
    protected MultiReactor.Reactor reactor;

    protected ByteBuffer numArray = ByteBuffer.allocate(50);

    protected ByteBuffer input = ByteBuffer.allocate(MAXIN);
    protected ByteBuffer output = ByteBuffer.allocate(MAXOUT);

    protected int state = READING;

    public IOEventHandler(SocketChannel sc, MultiReactor.Reactor reactor) {
        this.sc = sc;
        this.reactor = reactor;
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
        } catch (Exception ex) {
            ex.printStackTrace();
            // 关闭连接
            try {
                sc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void read() throws Exception {
        // 为什么要同步？
        // 保护 input ByteBuffer 不会重置和状态的可见性
        // 应该是这样
        synchronized (lock) {
            input.clear();
            int n = sc.read(input);
            if (inputIsComplete(n)) {
                // 发送交给 Reactor 触发
                state = SENDING;
                reactor.addEvent(MultiReactor.Reactor.InterestEvent.create(sc, SelectionKey.OP_WRITE, null));
                // 这里需要唤醒 Selector，因为当把处理交给 workpool 时，Reactor 线程已经阻塞在 select() 方法了， 注意
                // 此时该通道感兴趣的事件还是 OP_READ，这里将通道感兴趣的事件改为 OP_WRITE
                // 如果不唤醒的话，就只能在 下次select 返回时才能有响应了，当然了也可以在 select 方法上设置超时
            } else {

            }
        }
    }

    protected void doWrite() throws Exception {
        long begin = System.currentTimeMillis();
        byte[] bytes = Files.readAllBytes(Paths.get("/home/marvin/eden/happydocases/rsp.txt"));
        //byte[] bytes = Files.readAllBytes(Paths.get("d://rsp.txt"));
        //byte[] bytes = Files.readAllBytes(Paths.get(this.getClass().getResource("/rsp.txt").toURI()));
        boolean $mode = false;
        for (int i = 0; i < bytes.length; i++) {
            byte b = bytes[i];

            if (b == (byte) '$') {
                $mode = true;
                numArray.clear();
                continue;
            }

            if (!$mode) {
                if (b == '\r') {
                    continue;
                }
                if (b == '\n') {
                    output.put((byte) '\r');
                    output.put((byte) '\n');
                    continue;
                }
                output.put(b);
            } else {
                if (b == (byte) '{') {
                } else if (b == (byte) '}') {
                    $mode = false;

                    numArray.flip();
                    String strNum = new String(numArray.array(), 0, numArray.limit(), StandardCharsets.UTF_8);
                    int num = Integer.parseInt(strNum);
                    numArray.clear();

                    //写出
                    letwrite();
                    Thread.sleep(num);

                } else {
                    numArray.put(b);
                }
            }
        }

        //写出
        letwrite();
    }

    private void letwrite() throws IOException {
        output.flip();
        if (output.hasRemaining()) {
            int limit = output.limit();
            long begin = System.currentTimeMillis();
            int written = sc.write(output);
            System.out.println("output.limit=" + limit + ", written=" + written + ", writecost:" + (System.currentTimeMillis() - begin));
        }
        output.clear();
    }


    protected void send() throws Exception {
        doWrite();
        // 检查连接是否处理完毕，是否断开连接
        if (outputIsComplete(1)) {
            sc.close();
        } else {
            // 否则继续读取
            state = READING;
            reactor.addEvent(MultiReactor.Reactor.InterestEvent.create(sc, SelectionKey.OP_READ, null));
        }
    }

    char[] DELIMITER = new char[]{'\r', '\n', '\r', '\n'};

    /**
     * 当读取到 \r\n 时表示结束
     *
     * @param read 读取的字节数，-1 通常是连接被关闭，0 非阻塞模式可能返回
     * @throws IOException
     */
    protected boolean inputIsComplete(int read) throws Exception {
        if (read > 0) {
            input.flip(); // 切换成读取模式
            while (input.hasRemaining()) {
                char c = (char) input.get();
                request.append(c);
                System.out.println(request.length());
                // 读取到了 \r\n\r\n 读取结束
                if (done()) {
                    state = SENDING;
                    System.out.println("Reading done: " + request.toString());
                    request.delete(0, request.length());
                    return true;
                }
            }
        }
        return false;
    }

    private boolean done() {
        int length = request.length();
        if (length < 4) {
            return false;
        }

        boolean done = true;
        int first = length - 4;
        for (int i = 0; i < DELIMITER.length && (first + i < length); i++) {
            if (DELIMITER[i] != request.charAt(first + i)) {
                done = false;
                break;
            }
        }
        return done;
    }


    /**
     * 当用户输入了一个空行，表示连接可以关闭了
     */
    protected boolean outputIsComplete(int written) {
        if (written <= 0) {
            // 用户只敲了个回车， 断开连接
            return false;
        }

        // 清空旧数据，接着处理后续的请求
        output.clear();
        return false;
    }

    public static void main(String[] args) throws Exception {
        IOEventHandler dd = new IOEventHandler(null, null);
        dd.doWrite();
    }
}
