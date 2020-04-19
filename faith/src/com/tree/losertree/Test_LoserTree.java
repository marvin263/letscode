package com.tree.losertree;

import com.tree.showtree.ShowStaticTreeFrame;
import com.util.CommUtil;

public class Test_LoserTree {
    public static void main(String[] args) {
        int ls[] = LoserTreeUtil.createLoserTree(CommUtil.genereateRandomArray(
                25, 101));
        LSNode root = LoserTreeUtil.createVisibleLoserTree(ls);

        ShowStaticTreeFrame.showTreeFrm(root, "Loser Tree");
    }
}
