package com.github.luohaha.paxos.benchmark;

import java.io.IOException;
import com.github.luohaha.paxos.main.MyPaxos;

public class BMServer3 {
    public static void main(String[] args) {
        try {
            MyPaxos server = new MyPaxos("./conf/conf3.json");
            server.setGroupId(1, new BMCallback());
            server.start();
        } catch (IOException | InterruptedException | ClassNotFoundException e) {
            
            e.printStackTrace();
        }
    }
}
