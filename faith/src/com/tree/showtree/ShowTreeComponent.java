package com.tree.showtree;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.tree.avl.AVLUtil;
import com.tree.commontree.TreeUtil;

public class ShowTreeComponent extends JComponent implements MouseListener {
    private static final long serialVersionUID = 1L;
    private int verticalGap;
    private int bottomLevelHorizontalGap;
    private int startX;
    private int startY;
    private FullTreeNode[] arrayFullTree;
    private FullTreeNode lastClickNode;

    static class FullTreeNode {
        public static int radius = 13;
        public VisibleNode node;
        public int X;
        public int Y;
        public boolean V = false;
    }

    public ShowTreeComponent(int verticalGap, int bottomLevelHorizontalGap,
            VisibleNode root, int startX, int startY) {
        this.verticalGap = verticalGap;
        this.bottomLevelHorizontalGap = bottomLevelHorizontalGap;
        this.startX = startX;
        this.startY = startY;
        addMouseListener(this);
        arrayFullTree = arbitrary2full(root);
    }

    public ShowTreeComponent(VisibleNode root) {
        this(65, 26, root, 20, 30);
    }

    public void installNewTree(VisibleNode root) {
        arrayFullTree = arbitrary2full(root);
        lastClickNode = null;
        this.repaint();
    }

    public VisibleNode getLastClickNode() {
        if (lastClickNode == null) {
            return null;
        }
        return lastClickNode.node;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics gCopy = g.create();

        int verticalGap = this.verticalGap;
        int bottomLevelHorizontalGap = this.bottomLevelHorizontalGap;
        int startX = this.startX;
        int startY = this.startY;

        calculateXY(verticalGap, bottomLevelHorizontalGap, arrayFullTree,
                startX, startY);
        drawTreeGraphic(gCopy, arrayFullTree, lastClickNode);

        gCopy.dispose();
    }

    private void calculateXY(int verticalGap, int bottomLevelHorizontalGap,
            FullTreeNode[] listFullTree, int startX, int startY) {
        if (listFullTree == null || listFullTree.length == 0) {
            return;
        }
        int totalLevel = TreeUtil.log2(listFullTree.length);// begins from 1
        // currentLevel begins from 1
        for (int currentLevel = totalLevel; currentLevel >= 1; currentLevel--) {
            int Y = startY + ((currentLevel - 1) * verticalGap);

            int leftestXInThisLevel = 0;
            int firstNodeIndexInThisLevel = TreeUtil.pow2(currentLevel - 1);
            // bottom level
            if (currentLevel == totalLevel) {
                leftestXInThisLevel = startX;
            }
            // NOT bottom level
            else {
                int leftChildIndex = firstNodeIndexInThisLevel * 2;
                int rightChildIndex = leftChildIndex + 1;
                FullTreeNode leftChild = listFullTree[leftChildIndex];
                FullTreeNode rightChild = listFullTree[rightChildIndex];
                leftestXInThisLevel = (leftChild.X + rightChild.X) >> 1;
            }

            int totalNodeCountInThisLevel = TreeUtil.pow2(currentLevel - 1);
            int currentLevelHorizonGap = bottomLevelHorizontalGap
                    * TreeUtil.pow2(totalLevel - currentLevel);
            for (int i = 0; i < totalNodeCountInThisLevel; i++) {
                FullTreeNode nodeInThisLevel = listFullTree[firstNodeIndexInThisLevel
                        + i];
                nodeInThisLevel.Y = Y;
                nodeInThisLevel.X = leftestXInThisLevel
                        + currentLevelHorizonGap * i;
            }
        }
    }

    protected void drawTreeGraphic(Graphics gCopy, FullTreeNode[] listFullTree,
            FullTreeNode lastClickNode) {
        drawAllTreeNode(gCopy, listFullTree);
        drawAllTreeLine(gCopy, listFullTree);
        drawArrow(gCopy, lastClickNode);
    }

    private void drawAllTreeNode(Graphics gCopy, FullTreeNode[] listFullTree) {
        Color outlineColor = null, fillColor = null, fontColor = null;
        outlineColor = Color.red;
        fillColor = Color.yellow;
        fontColor = Color.black;
        // draw each tree node
        for (int i = 1; i < listFullTree.length; i++) {
            FullTreeNode fullNode = listFullTree[i];
            if (fullNode.V == true) {
                // Yes, node's outlineColor is designated by the node itself
                if (fullNode.node instanceof VisibleColorNode) {
                    outlineColor = ((VisibleColorNode) fullNode.node).color();
                } else {
                    // use default outlineColor
                }
                drawEachTreeNode(gCopy, fullNode.X, fullNode.Y,
                        FullTreeNode.radius, outlineColor, fillColor,
                        fontColor, fullNode.node.presentation());
            }
        }
    }

    private void drawEachTreeNode(Graphics gCopy, int x, int y, int radius,
            Color outlineColor, Color fillColor, Color fontColor, String str) {
        int width = radius * 2;
        int height = width;
        // draw outline
        gCopy.setColor(outlineColor);
        gCopy.drawOval(x, y, width, height);

        // fill Oval(outline is kept)
        gCopy.setColor(fillColor);
        gCopy.fillOval(x + 1, y + 1, width - 2, height - 2);

        // draw string
        int strWidth = SwingUtilities.computeStringWidth(
                getFontMetrics(getFont()), str);
        int strAscent = getFontMetrics(getFont()).getAscent();
        gCopy.setColor(fontColor);
        gCopy.drawString(str, (x + (width >> 1) - (strWidth >> 1)), (y
                + (height >> 1) + (strAscent >> 1)));
    }

    protected void drawAllTreeLine(Graphics gCopy, FullTreeNode[] list) {
        Color lineColor = Color.black;
        // draw outline
        gCopy.setColor(lineColor);
        for (int nodeIndex = 1; nodeIndex < list.length / 2; nodeIndex++) {
            FullTreeNode cur = list[nodeIndex];
            if (cur.V == false) {
                continue;
            }
            int leftChildIndex = nodeIndex * 2;
            int rightChildIndex = leftChildIndex + 1;
            FullTreeNode leftChild = list[leftChildIndex];
            FullTreeNode rightChild = list[rightChildIndex];

            if (leftChild.V == true) {
                int[] x1y1x2y2 = lineStartEnd(cur.X + FullTreeNode.radius,
                        cur.Y + FullTreeNode.radius, leftChild.X
                                + FullTreeNode.radius, leftChild.Y
                                + FullTreeNode.radius);

                gCopy.drawLine(x1y1x2y2[0], x1y1x2y2[1], x1y1x2y2[2],
                        x1y1x2y2[3]);
            }

            if (rightChild.V == true) {
                int[] x1y1x2y2 = lineStartEnd(cur.X + FullTreeNode.radius,
                        cur.Y + FullTreeNode.radius, rightChild.X
                                + FullTreeNode.radius, rightChild.Y
                                + FullTreeNode.radius);
                gCopy.drawLine(x1y1x2y2[0], x1y1x2y2[1], x1y1x2y2[2],
                        x1y1x2y2[3]);
            }
        }
    }

    private int[] lineStartEnd(int x1, int y1, int x2, int y2) {
        int height = Math.abs(y1 - y2);
        int width = Math.abs(x1 - x2);

        double innerWSquare = ((width * width) * (FullTreeNode.radius * FullTreeNode.radius))
                / ((height * height) + (width * width));

        double innerHSquare = ((height * height) * (FullTreeNode.radius * FullTreeNode.radius))
                / ((height * height) + (width * width));

        int innerW = (int) Math.sqrt(innerWSquare);
        int innerH = (int) Math.sqrt(innerHSquare);

        int[] x1y1x2y2 = new int[4];

        // x1 left
        if (x1 < x2) {
            x1y1x2y2[0] = x1 + innerW;
            x1y1x2y2[2] = x2 - innerW;
        }
        // x2 left
        else {
            x1y1x2y2[0] = x1 - innerW;
            x1y1x2y2[2] = x2 + innerW;
        }

        // y1 top
        if (y1 < y2) {
            x1y1x2y2[1] = y1 + innerH;
            x1y1x2y2[3] = y2 - innerH;
        }
        // y2 top
        else {
            x1y1x2y2[1] = y1 - innerH;
            x1y1x2y2[3] = y2 + innerH;
        }
        return x1y1x2y2;
    }

    private void drawArrow(Graphics gCopy, FullTreeNode lastClickNode) {
        if (lastClickNode == null) {
            return;
        }
        int r = FullTreeNode.radius;
        int width = 2;
        int height = 30;

        int x = lastClickNode.X;
        int y = lastClickNode.Y;
        gCopy.setColor(Color.red);
        gCopy.drawRect(x + r - 1, y - height, width, height);
        gCopy.fillRect(x + r - 1, y - height, width, height);

        // arrow's left part
        gCopy.drawLine(x + r - 1, y, (x + r - 1 - 3), y - 10);
        // arrow's right part
        gCopy.drawLine(x + r + 1, y, (x + r + 1 + 3), y - 10);
    }

    public static FullTreeNode[] arbitrary2full(VisibleNode root) {
        if (root == null) {
            return new FullTreeNode[0];
        }
        int height = TreeUtil.H(root);
        int arrayLength = TreeUtil.pow2(height);
        FullTreeNode[] arrayFullTree = new FullTreeNode[arrayLength];
        for (int i = 0; i < arrayLength; i++) {
            arrayFullTree[i] = new FullTreeNode();
        }
        int index = 1;
        correspondingFullTreeIndex(root, index, arrayFullTree);
        return arrayFullTree;
    }

    private static void correspondingFullTreeIndex(VisibleNode node, int index,
            FullTreeNode[] arrayFullTree) {
        if (node != null) {
            arrayFullTree[index].node = node;
            arrayFullTree[index].V = true;
            correspondingFullTreeIndex(node.left(), index << 1, arrayFullTree);
            correspondingFullTreeIndex(node.right(), (index << 1) + 1,
                    arrayFullTree);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() != MouseEvent.BUTTON1) {
            return;
        }
        if (e.getClickCount() < 2) {
            return;
        }
        // left button, click 2 or more times
        FullTreeNode clickNode = findClickNode(e.getX(), e.getY());
        if (clickNode != null) {
            lastClickNode = clickNode;
            repaint();
        }
    }

    private FullTreeNode findClickNode(int x, int y) {
        for (int i = 1; i < arrayFullTree.length; i++) {
            FullTreeNode clickNode = arrayFullTree[i];
            if (clickNode.V == false) {
                continue;
            }
            if (insideSqure(clickNode, x, y)) {
                return clickNode;
            }
        }
        return null;
    }

    private boolean insideSqure(FullTreeNode clickNode, int x, int y) {
        int r = FullTreeNode.radius;
        return (x >= clickNode.X && x <= clickNode.X + 2 * r)
                && (y >= clickNode.Y && y <= clickNode.Y + 2 * r);
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    public static void test_One_Component() {
        Random r = new Random();
        int maxInt = 51;
        int nodeCount = 14;
        int[] array = new int[nodeCount];
        for (int i = 0; i < array.length; i++) {
            array[i] = r.nextInt(maxInt);
        }
        array = new int[] { 10, 20, 30, 40, 50, 60, 70, 80, 40, 50, 60, 70, 80,
                90, 32, 52, 54, 345, 63, 346, 63, 46, 63, 26 };
        VisibleNode root = AVLUtil.createAVLtree(array);

        int verticalGap = 65;
        int bottomLevelHorizontalGap = 26;
        int startX = 20;
        int startY = 30;

        JFrame frm = new JFrame();
        frm.getContentPane().add(
                new ShowTreeComponent(verticalGap, bottomLevelHorizontalGap,
                        root, startX, startY));
        frm.setVisible(true);
        frm.setSize(700, 500);
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void test_FullTree() {
        VisibleNode root = TreeUtil.createFullTree(5);

        int verticalGap = 65;
        int bottomLevelHorizontalGap = 30;
        int startX = 20;
        int startY = 30;

        JFrame frm = new JFrame();
        frm.getContentPane().add(
                new ShowTreeComponent(verticalGap, bottomLevelHorizontalGap,
                        root, startX, startY));
        frm.setVisible(true);
        frm.setSize(700, 500);
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        test_One_Component();
        // test_FullTree();
    }

}
