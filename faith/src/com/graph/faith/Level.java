package com.graph.faith;

import java.util.ArrayList;
import java.util.List;

public class Level {
    public static final int cicle_radius = 5;
    public static final int block_height = 30;
    public static final int block_v_gap = 60;
    public static final int ref_width = 12;
    public static final int block_h_gap = 70 + ref_width;
    public static final int node_width = 50;
    public static final int ref_plus_node_width = ref_width + node_width;

    public List<Block> listBlock = new ArrayList<Block>();

    public int calcLevelWidth() {
        if (listBlock.size() == 0) {
            return 0;
        }

        int levelWidth = 0;
        for (Block b : listBlock) {
            levelWidth += (b.calcBlockWidth() + block_h_gap);
        }
        levelWidth = levelWidth - block_h_gap;
        return levelWidth;
    }
}
