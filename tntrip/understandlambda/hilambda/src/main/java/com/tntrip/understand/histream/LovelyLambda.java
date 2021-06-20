package com.tntrip.understand.histream;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author libin
 * @Date 2021/6/19
 */
public class LovelyLambda {
    private static final String[] PRODUCT_NAMES = new String[]{"AAA", "BBB", "CCC", "DDD", "EEE", "FFF"};

    public static class Order {
        private int ordId;
        private List<OrderItem> items = new ArrayList<>();

        private void addItem(OrderItem item) {
            items.add(item);
        }

        @Override
        public String toString() {
            return ordId + " -- " + items;
        }

        public int getOrdId() {
            return ordId;
        }

        public Order setOrdId(int ordId) {
            this.ordId = ordId;
            return this;
        }

        public List<OrderItem> getItems() {
            return items;
        }

        public Order setItems(List<OrderItem> items) {
            this.items = items;
            return this;
        }
    }

    public static class OrderItem {
        private int itemId;
        private String productName;
        private int parentOrdId;

        public String getProductName() {
            return productName;
        }

        public OrderItem setProductName(String productName) {
            this.productName = productName;
            return this;
        }

        public int getItemId() {

            return itemId;
        }

        public OrderItem setItemId(int itemId) {
            this.itemId = itemId;
            return this;
        }

        public int getParentOrdId() {
            return parentOrdId;
        }

        public OrderItem setParentOrdId(int parentOrdId) {
            this.parentOrdId = parentOrdId;
            return this;
        }

        @Override
        public String toString() {
            return "{" +
                    "parentOrdId=" + parentOrdId +
                    ", itemId=" + itemId +
                    ", productName='" + productName + '\'' +
                    '}';
        }
    }

    private static List<Order> createOrders() {
        List<Order> orders = new ArrayList<>();
        Random rdm = new SecureRandom();
        int ordCount = 500;
        int eachItemCount = 3;

        for (int i = 0; i < ordCount; i++) {
            Order ord = new Order();
            orders.add(ord);
            ord.ordId = i + 1;
            for (int j = 0; j < eachItemCount; j++) {
                OrderItem oi = new OrderItem();
                ord.addItem(oi);
                oi.parentOrdId = i;
                oi.itemId = j + 1;
                oi.productName = PRODUCT_NAMES[rdm.nextInt(PRODUCT_NAMES.length)];
            }
        }

        for (Order ord : orders) {
            System.out.println(ord);
        }
        System.out.println();
        return orders;
    }


    private static void original2New() {
        List<OrderItem> orderItems = createOrders().stream()
                .flatMap(e -> e.items.stream())
                .collect(Collectors.toList());

        long b1 = System.currentTimeMillis();
        Map<Boolean, List<OrderItem>> attrMap = orderItems.stream()
                .map(a -> (OrderItem) a)
                .collect(Collectors.groupingBy(
                        OrderItem::getProductName,
                        LinkedHashMap::new,
                        Collectors.toMap(
                                OrderItem::getItemId,
                                Function.identity(),
                                BinaryOperator.maxBy(Comparator.comparingLong(OrderItem::getItemId)))))
                .values()
                .stream()
                .flatMap(b -> b.values().stream())
                .filter(LovelyLambda::filterKeyAndGoodsId)
                .collect(Collectors.partitioningBy(c -> c.parentOrdId > 2));
        List<OrderItem> hasAttr = attrMap.get(true);
        List<OrderItem> noAttr = attrMap.get(false);

        long b2 = System.currentTimeMillis();
        Map<Boolean, List<OrderItem>> attrMap2 = orderItems.stream()
                .map(a -> (OrderItem) a)
                .collect(Collectors.groupingBy(
                        OrderItem::getProductName,
                        LinkedHashMap::new,
                        Collectors.groupingBy(
                                OrderItem::getItemId,
                                Collectors.collectingAndThen(
                                        Collectors.toList(),
                                        e -> e.stream().max(Comparator.comparingInt(OrderItem::getItemId)).get())  //寻找合适的item_id
                        )))
                .values()
                .stream()
                .flatMap(b -> b.values().stream())
                .filter(LovelyLambda::filterKeyAndGoodsId)
                .collect(Collectors.partitioningBy(c -> c.parentOrdId > 2));
        List<OrderItem> hasAttr2 = attrMap2.get(true);
        List<OrderItem> noAttr2 = attrMap2.get(false);

        System.out.println(b2 - b1);
        System.out.println(System.currentTimeMillis() - b2);
    }

    private static boolean filterKeyAndGoodsId(OrderItem d) {
        return true;
    }

    private static void testFlatMap() {
        List<Order> orders = createOrders();
        List<OrderItem> collect = orders.stream().flatMap(e -> e.items.stream()).collect(Collectors.toList());
        for (OrderItem item : collect) {
            System.out.println(item);
        }
    }

    public static void main(String[] args) {
        //testFlatMap();
        original2New();
    }

}




