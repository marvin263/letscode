package com.tntrip.understand.targettyping;

import com.tntrip.understand.Album;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Created by nuc on 2015/11/19.
 */
public class ContextsTargetType {
    public static void eightContextHaveTargetType() {
        // 1. 变量声明（variable declaration）
        Comparator<String> c; // 变量声明上下文的 target type 是 Comparable<String>

        // 2. 赋值（assignment）
        c = (s1, s2) -> s1.compareToIgnoreCase(s2); // 赋值上下文的 target type 是 Comparable<String>
        c = String::compareToIgnoreCase;

        // 3. 数组赋值 Array initializer
        // 数组赋值上下文的 target type。这个 target type 是从数组的类型推导出来的
        FileFilter[] ffArray = new FileFilter[]{f -> f.canRead(), f -> f.exists(), File::isFile};


        // 4. 方法参数 method/constructor argument
        // Lambda expressions themselves provide target types for their bodies,
        // in this case by deriving that type from the outer target type.
        // This makes it convenient to write functions that return other functions:
        Supplier<Runnable> spl = () -> () -> System.out.println("Hi");
    }

    /**
     * lambda body的返回值 可以用来确定 lambda本身的 参数类型
     * （好像是这个意思，还没大搞明白）
     */
    public static void lambdaBodyProvideInfo2Compiler() {
        List<Album> albums = new ArrayList<>();

        Stream<List<Album.Track>> stringStream =
                // Stream<Album>
                albums.stream()
                        // 1. map方法返回 Stream<R>
                        // 2. Function<T, R>. 入参T 就是Album（要将 stream中流出来的每一个Album 元素转换成 R）
                        // 3. R 我们自己订。Function返回啥，外层 map 就返回啥
                        // Function<Album, List<Album.Track>> ====== album->album.tracks
                        .<List<Album.Track>>map(album -> album.tracks);


    }

    public static void castExplicitlyProvidesLambdaTargetType() {
        //Error: Target type of a lambda convertion must be an interface
        // 这也说明一个问题，lambda本身的类型，也可以叫做 "target type"
        // Object obj1 = ()-> System.out.println("Hello");

        Object obj = (Runnable) () -> System.out.println("Hello");
    }

    public static void main(String[] args) {
        lambdaBodyProvideInfo2Compiler();
    }
}
