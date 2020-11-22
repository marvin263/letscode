package com.tntrip.rwsocket;

import com.tntrip.understand.asynchc.NamedThreadFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 为了匹配 CPU 和 IO 的速率，可设计多个 Reactor（即 Selector 池）：<p>
 * <p>
 * 主 Reacotr 负责监听连接，然后将连接注册到 从 Reactor，将 I/O 转移了<br>
 * <p>
 * 从 Reacotr 负责通道 I/O 的读写，处理器可选择单线程或线程池
 * <p>
 * <b>注意这里的 Reactor 是 MultiReactor 的内部类</b>
 *
 * @author wskwbog
 * @see MultiReactor.Reactor
 */
public class MultiReactor {
    // Acceptor所使用的线程
    private final Executor MAIN_REACTOR_EXECUTOR = Executors.newFixedThreadPool(1, new NamedThreadFactory("Acceptor"));

    // Reactor所使用的线程
    private static final int SUB_REACTOR_COUNT = 2;
    private final Executor SUB_REACTOR_EXECUTOR = Executors.newFixedThreadPool(SUB_REACTOR_COUNT, new NamedThreadFactory("subReactor"));

    // Acceptor 接收连接。收到的连接 SocketChannel 注册到从 Reactor 上
    private Acceptor acceptor;

    // 从 Reactors，用于处理 I/O，可使用 BasicHandler 和  MultiThreadHandler 两种处理方式
    private final Reactor[] subReactors = new Reactor[SUB_REACTOR_COUNT];

    private int reqCount = 0;

    public MultiReactor(int port) {
        try {
            acceptor = new Acceptor(port);

            for (int i = 0; i < subReactors.length; i++) {
                subReactors[i] = new Reactor();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动主从 Reactor，初始化并注册 Acceptor 到主 Reactor
     */
    public void start() throws IOException {
        // 将 ServerSocketChannel 注册到 mainReactor
        MAIN_REACTOR_EXECUTOR.execute(acceptor);

        for (int i = 0; i < subReactors.length; i++) {
            SUB_REACTOR_EXECUTOR.execute(subReactors[i]);
        }
    }

    /**
     * 初始化并配置 ServerSocketChannel，注册到 mainReactor 的 Selector 上
     */
    class Acceptor implements Runnable {
        final ServerSocketChannel serverSocketChannel;
        final Selector selector;

        public Acceptor(int port) throws IOException {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(port));
            // 设置成非阻塞模式
            serverSocketChannel.configureBlocking(false);
            // 注册到 选择器 并设置处理 socket 连接事件
            SelectionKey selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            selectionKey.attach(this);
            System.out.println("Acceptor: Listening on port: " + port);
        }

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    int select = selector.select(2000);
                    if (select <= 0) {
                        continue;
                    }
                    // 接收连接，非阻塞模式下，没有连接直接返回 null
                    SocketChannel sc = serverSocketChannel.accept();
                    System.out.println(sc);
                    if (sc != null) {
                        Reactor reactor = chooseSubReactor();
                        reactor.addEvent(Reactor.InterestEvent.create(sc, SelectionKey.OP_READ,
                                new IOEventHandler(sc, reactor)));
                        System.out.println("Acceptor: " + sc.socket().getLocalSocketAddress() + " 注册到 subReactor-" + reqCount);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private Reactor chooseSubReactor() {
        final int i = Math.abs(this.reqCount++ % SUB_REACTOR_COUNT);
        return this.subReactors[i];
    }

    static class Reactor implements Runnable {
        // reactor所使用的selector
        final Selector selector;

        public Reactor() throws IOException {
            this.selector = Selector.open();
        }

        public static class InterestEvent {
            private SocketChannel sc;
            private int interestOps;

            private Object attach;

            public static InterestEvent create(SocketChannel sc, int interestOps, Object attach) {
                InterestEvent ie = new InterestEvent();
                ie.sc = sc;
                ie.interestOps = interestOps;
                ie.attach = attach;
                return ie;
            }
        }

        private final ConcurrentLinkedQueue<InterestEvent> events = new ConcurrentLinkedQueue<>();

        public void addEvent(InterestEvent e) {
            events.add(e);
            selector.wakeup();
        }
        private void setupSocketChannel() {
            Iterator<InterestEvent> it = events.iterator();
            while (it.hasNext()) {
                InterestEvent ie = it.next();
                it.remove();

                if (ie.attach!=null) {
                    try {
                        ie.sc.configureBlocking(false);
                        ie.sc.register(selector, ie.interestOps, ie.attach);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    SelectionKey selectionKey = ie.sc.keyFor(selector);
                    selectionKey.interestOps(ie.interestOps);
                }
            }
        }

        // normally in a new Thread
        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    // 阻塞，直到有通道事件就绪
                    int select = selector.select(2000);
                    setupSocketChannel();
                    if (select > 0) {
                        System.out.println("ddd");
                    } else {
                        continue;
                    }

                    // 拿到就绪通道 SelectionKey 的集合
                    Set<SelectionKey> keys = selector.selectedKeys();
                    System.out.println(keys.size());
                    for (SelectionKey eachKey : keys) {
                        dispatch(eachKey);
                    }
                    // 清空就绪通道的 key
                    keys.clear();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        void dispatch(SelectionKey k) {
            // 拿到通道注册时附加的对象
            IOEventHandler r = (IOEventHandler) (k.attachment());
            r.handleEvent();
        }
    }

    public static void main(String[] args) throws IOException {
        MultiReactor mr = new MultiReactor(10393);
        mr.start();
    }
}
