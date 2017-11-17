package com.graph;

public class Dijkstra {
    // public static final int NOT_CONNECTED = Integer.MAX_VALUE;
    //
    // public static void dfdf(Weight[][] matrix, int orgnStart, int[]
    // relayStart) {
    // int totalCount = matrix.length;
    //
    // WeightAndVertices[] finalResult = new WeightAndVertices[matrix.length];
    // Set<Integer> excluded = new HashSet<Integer>();
    // excluded.add(Integer.valueOf(orgnStart));
    //
    // int[] minWeight = GraphUtil.minWeightDest(orgnStart, excluded, matrix);
    //
    // if (minWeight.length == 0) {
    // return;// orgnStart is unreachable
    // }
    // finalResult[1] = WeightAndVertices.createWeightAndVertices(Distance
    // .createDistance(matrix[orgnStart][0].v(), false));
    // CommUtil.addAll(finalResult[1].vertices, minWeight);
    // CommUtil.addAll(excluded, minWeight);
    //
    // for (int i = 0; i < finalResult.length; i++) {
    // WeightAndVertices lastWAV = finalResult[i - 1];
    // }
    //
    // }
    //
    // public static List<FromToWeight> singleHopMins(int[] relayStart,
    // Set<Integer> excluded, Weight[][] matrix) {
    // List<FromToWeight> listAll = new ArrayList<FromToWeight>();
    //
    // for (int aStart : relayStart) {
    // FromToWeight ftw = singleHopMin(aStart, excluded, matrix);
    // if (ftw != null) {
    // listAll.add(ftw);
    // }
    // }
    //
    // if (listAll.size() == 0) {
    // return new ArrayList<FromToWeight>(0);
    // }
    //
    // List<FromToWeight> listMin = new ArrayList<FromToWeight>();
    // FromToWeight minW = listAll.get(0);
    //
    // for (FromToWeight fwt : listAll) {
    // if (fwt.w.v() == minW.w.v()) {
    // listMin.add(fwt);
    // } else if (fwt.w.v() < minW.w.v()) {
    // listMin.clear();
    // listMin.add(fwt);
    // minW = fwt;
    // }
    // // aW.v() > minW.weight()
    // else {
    // // do nothing
    // }
    // }
    // return listMin;
    // }
    //
    // public static FromToWeight singleHopMin(int start, Set<Integer> excluded,
    // Weight[][] matrix) {
    // Weight[] arrayW = matrix[start];
    // Weight minW = arrayW[start];
    //
    // List<Integer> indices = new ArrayList<Integer>();
    // indices.add(Integer.valueOf(start));
    //
    // for (int i = 0; i < arrayW.length; i++) {
    // Integer I = Integer.valueOf(i);
    // Weight aW = arrayW[i];
    // if (i == start || aW.NA() || excluded.contains(I)) {
    // continue;
    // }
    // if (aW.v() == minW.v()) {
    // indices.add(I);
    // } else if (aW.v() < minW.v()) {
    // indices.clear();
    // indices.add(I);
    // minW = aW;
    // }
    // // aW.v() > minW.weight()
    // else {
    // // do nothing
    // }
    // }
    // if (indices.size() == 0) {
    // return null;
    // }
    // FromToWeight ftw = new FromToWeight(start, arrayW[indices.get(0)
    // .intValue()]);
    // ftw.to.addAll(indices);
    // return ftw;
    // }

    // public static int[] minWeightDest(int start, Set<Integer> excluded,
    // Weight[][] matrix) {
    // Weight[] arrayW = matrix[start];
    // Weight minW = arrayW[start];
    //
    // List<Integer> list = new ArrayList<Integer>();
    // list.add(Integer.valueOf(start));
    //
    // for (int i = 0; i < arrayW.length; i++) {
    // Integer I = Integer.valueOf(i);
    // Weight aW = arrayW[i];
    // if (i == start || aW.NA() || excluded.contains(I)) {
    // continue;
    // }
    // if (aW.v() == minW.v()) {
    // list.add(I);
    // } else if (aW.v() < minW.v()) {
    // list.clear();
    // list.add(I);
    // minW = aW;
    // }
    // // aW.v() > minW.weight()
    // else {
    // // do nothing
    // }
    // }
    // int[] indices = new int[list.size()];
    // for (int i = 0; i < list.size(); i++) {
    // indices[i] = list.get(i).intValue();
    // }
    // return indices;
    // }

}
