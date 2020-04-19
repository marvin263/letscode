package com.stack.maze;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import com.util.CommUtil;
import com.util.IOUtil;

public class MazeMap {
    public final int Row_MAX_INDEX;
    public final int Col_MAX_INDEX;

    private Vertex[][] arrayV;

    public MazeMap(int rowMaxIndex, int colMaxIndex) {
        this.Row_MAX_INDEX = rowMaxIndex;
        this.Col_MAX_INDEX = colMaxIndex;
        arrayV = new Vertex[Row_MAX_INDEX + 1][Col_MAX_INDEX + 1];
        initializeMap(arrayV);
        writeMap2File();
    }

    private void initializeMap(Vertex[][] arrayV) {
        Random rdm = new Random();
        int rowCount = arrayV.length;
        for (int row = 0; row < rowCount; row++) {
            Vertex[] columns4OneRow = arrayV[row];
            int colCount = columns4OneRow.length;
            for (int col = 0; col < colCount; col++) {
                // entry
                if (row == 0 && col == 0) {
                    Vertex v = new Vertex(this, row, col, false);
                    // set entry's
                    v.fillPassivelyConsumedDirection(Direction.EAST);
                    arrayV[row][col] = v;
                }
                // outlet
                else if (row == rowCount - 1 && col == colCount - 1) {
                    Vertex v = new Vertex(this, row, col, false);
                    arrayV[row][col] = v;
                } else {
                    Vertex v = new Vertex(this, row, col, rdm.nextBoolean());
                    arrayV[row][col] = v;
                }
            }
        }
    }

    public void writeMap2File() {
        String mapfilename = (Row_MAX_INDEX + 1) + "_" + (Col_MAX_INDEX + 1)
                + "--" + System.currentTimeMillis() + ".txt";

        BufferedWriter bw = IOUtil.createBufferedWriter(
                IOUtil.getJarStayFolder_nologinfo(MazeMap.class) + "/"
                        + mapfilename, null, false);

        StringBuilder sb = new StringBuilder();
        String[] arrayEachRow = new String[Col_MAX_INDEX + 1 + 1];
        arrayEachRow[0] = "-1";
        for (int col = 1; col <= Col_MAX_INDEX + 1; col++) {
            arrayEachRow[col] = (col - 1) + "";
        }
        CommUtil.concat(sb, arrayEachRow, "\t", "\r\n");

        for (int row = 0; row <= Row_MAX_INDEX; row++) {
            arrayEachRow[0] = row + "";
            for (int col = 1; col <= Col_MAX_INDEX + 1; col++) {
                arrayEachRow[col] = blocked2str(arrayV[row][col - 1].blocked);
            }
            CommUtil.concat(sb, arrayEachRow, "\t", "\r\n");
        }
        try {
            bw.write(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        IOUtil.closeWriter(bw);

    }

    public MazeMap(String filename) {
        List<List<String>> listAllRow = createListAllRowByFile(filename);
        this.Row_MAX_INDEX = listAllRow.size() - 1;
        this.Col_MAX_INDEX = listAllRow.get(0).size() - 1 - 1;
        System.out.println("Row_MAX_INDEX=" + Row_MAX_INDEX
                + ", Col_MAX_INDEX=" + Col_MAX_INDEX);

        arrayV = new Vertex[Row_MAX_INDEX + 1][Col_MAX_INDEX + 1];

        initializeMapByList(arrayV, listAllRow);

    }

    private List<List<String>> createListAllRowByFile(String filename) {
        String parentPath = IOUtil.getJarStayFolder_nologinfo(MazeMap.class);
        String fullPath = parentPath + "/" + filename;
        System.out.println("map file fullPath: " + fullPath);
        BufferedReader br = IOUtil.createBufferedReader(fullPath, null);

        List<List<String>> listAllRow = new ArrayList<List<String>>();
        String line = null;
        boolean firstLine = true;
        try {
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                listAllRow.add(CommUtil.split(line, "\t"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        IOUtil.closeReader(br);
        return listAllRow;
    }

    private void initializeMapByList(Vertex[][] arrayV,
            List<List<String>> listAllRow) {
        int rowCount = arrayV.length;
        for (int row = 0; row < rowCount; row++) {
            List<String> listOneRowValues = listAllRow.get(row);
            // first col is not map value
            int colCount = listOneRowValues.size() - 1;
            for (int col = 0; col < colCount; col++) {
                boolean blocked = str2Blocked(listOneRowValues.get(col + 1));
                Vertex v = new Vertex(this, row, col, blocked);
                arrayV[row][col] = v;
                // entry
                if (row == 0 && col == 0) {
                    v.fillPassivelyConsumedDirection(Direction.EAST);
                }
            }
        }
    }

    // 0-->false--NOT blocked
    // 1-->true--blocked
    private boolean str2Blocked(String str) {
        if ("0".equals(str)) {
            return false;
        }
        if ("1".equals(str)) {
            return true;
        }
        throw new RuntimeException("0, 1 only for the map");
    }

    // false--NOT blocked-->0
    // true--blocked-->1
    private String blocked2str(boolean blocked) {
        return blocked ? "1" : "0";
    }

    public void printPath(Stack<Vertex> stack) {
        if (stack.size() == 0) {
            System.out.println("Oops, dead maze!!!");
            return;
        }
        System.out.println("Congratulation!!! Go through " + stack.size()
                + " vertices, and you get out of the maze");
        Stack<Vertex> another = new Stack<Vertex>();
        while (!stack.isEmpty()) {
            another.push(stack.pop());
        }
        int i = 0;
        while (!another.isEmpty()) {
            System.out.println(CommUtil.int2str(i, 3) + ": " + another.pop());
            i++;
        }
    }

    public void printMap() {
        for (int row = 0; row < arrayV.length; row++) {
            Vertex[] columns4OneRow = arrayV[row];
            System.out.println(Arrays.toString(columns4OneRow));
        }
    }

    private boolean vertexExist(int row, int col) {
        if (row == -1 || col == -1) {
            return false;
        }
        return true;
    }

    public Vertex getV(int row, int col) {
        if (vertexExist(row, col) == false) {
            return null;
        }
        return arrayV[row][col];
    }

    private boolean bExitVertex(Vertex v, int outRow, int outCol) {
        return v.col == outCol && v.row == outRow;
    }

    public Stack<Vertex> findPath(int entryRow, int entryCol, int outRow,
            int outCol) {
        Stack<Vertex> stack = new Stack<Vertex>();
        Vertex entry = getV(entryRow, entryCol);
        Vertex out = getV(outRow, outCol);
        // unavailable entry&&outlet
        if (entry.blocked || out.blocked) {
            return stack;
        }

        stack.push(entry);// entry's justActivelyConsumed is null
        if (entryRow == outRow && entryCol == outCol) {
            // only one Vertex, already found the path
            return stack;
        }

        while (!stack.isEmpty()) {
            Vertex curV = stack.peek();
            System.out.println("Currently processing: " + curV);
            // 1. good, find outlet
            if (bExitVertex(curV, outRow, outCol)) {
                break;
            }
            // NOT exit, come on, let's do hard work, marvin

            // 1. blocked
            if (curV.blocked) {
                stack.pop();
                continue;
            }
            // 2. nextV has already been discarded before
            if (curV.nonBlocked_but_has_been_discard()) {
                stack.pop();
                continue;
            }
            // 3. curV is good
            if (curV.blocked == false) {
                Vertex nextV = curV.nextVertex();
                // tried <b>curV's</b> all directions
                // but no direction is available
                if (nextV == null) {
                    // curV not blocked
                    // but after tried all direction, finally, we found no
                    // direction is available
                    // so curV is now been discard
                    stack.pop();//
                    continue;
                }
                // nextV NOT null
                else {
                    nextV.fillPassivelyConsumedDirection(curV
                            .getJustActivelyConsumed());
                    stack.push(nextV);
                }
            }
        }
        return stack;
    }

    public static void testMaze_By_File(String filename) {
        MazeMap maze = new MazeMap(filename);

        int entryRow = 0, entryCol = 0, outRow = maze.Row_MAX_INDEX, outCol = maze.Col_MAX_INDEX;

        maze.printMap();
        Stack<Vertex> sPath = maze.findPath(entryRow, entryCol, outRow, outCol);
        maze.printPath(sPath);
    }

    public static void testMaze_By_RandomMap(int rowCount, int colCount) {
        MazeMap maze = new MazeMap(rowCount - 1, colCount - 1);

        int entryRow = 0, entryCol = 0, outRow = maze.Row_MAX_INDEX, outCol = maze.Col_MAX_INDEX;

        maze.printMap();
        Stack<Vertex> sPath = maze.findPath(entryRow, entryCol, outRow, outCol);
        maze.printPath(sPath);
    }

    public static void main(String[] args) {
        String filename = "8_13--m.txt";
        MazeMap.testMaze_By_File(filename);

        // MazeMap.testMaze_By_RandomMap(8, 13);
    }
}
