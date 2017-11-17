package com.tntrip.understand;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by nuc on 2015/11/17.
 */
public class InferByReturnValue {
    public static void returnWhat() {
        ExecutorService es = Executors.newFixedThreadPool(5);
        es.submit(() -> {
            try {
                Scanner scanner = new Scanner(new FileReader("file.txt"));
                while(scanner.hasNext()){
                    String line = scanner.nextLine();
                    process(line);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //return null;
        });
    }

    private static void process(String line) {
        System.out.println(line);
    }

    public static void main(String[] args) {
        returnWhat();
    }
}
