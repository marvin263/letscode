package com.tntrip;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Created by gejinfeng on 2017/12/2.
 * sos
 *
 * @author gejinfeng
 * @date 2017/12/2
 */
public class TTT {

    private Map<Integer, Integer> cache = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        String s = "{\"a\":\"av\", \"b\":\"bv\"}";
        System.out.println(s);
        System.out.println();

    }

    public int fibonaacci(Integer i) {
        System.out.println(".....");
        if (i == 0 || i == 1) {
            return i;
        }
        // Java 8 Map接口中新增方法
        // 首先判断缓存MAP中是否存在指定key的值，如果不存在，会自动调用mappingFunction(key)
        // 计算key的value，然后将key = value
        // 放入到缓存Map,java8会使用thread-safe的方式从cache中存取记录。
        return cache.computeIfAbsent(i, new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer key) {
                // 函数式接口,key = i,就是传过来的key,函数中的第一个参数
                System.out.println("Compute fibonaacci " + key);
                return TTT.this.fibonaacci(key - 1) + TTT.this.fibonaacci(key - 2);
            }
        });
    }

}
