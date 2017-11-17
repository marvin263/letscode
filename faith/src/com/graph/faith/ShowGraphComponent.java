package com.graph.faith;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.tools.Tool;

public class ShowGraphComponent extends JComponent {
	private static final long serialVersionUID = 1L;
	public static final int ROOT_ALIGN_left = 0;
	public static final int ROOT_ALIGN_middle = 1;
	public static final int INDENT_X = 20;
	public static final int INDENT_Y = 20;

	public static int ROOT_ALIGN = ROOT_ALIGN_middle;
	private List<Level> listLevel;
	private final int maxLevelWidth;

	public ShowGraphComponent(List<Level> listLevel) {
		this.listLevel = listLevel;
		maxLevelWidth = getMaxLevelWidth();
	}

	private int getMaxLevelWidth() {
		int max = -1;
		for (Level lv : listLevel) {
			int lvWidth = lv.calcLevelWidth();
			if (lvWidth > max) {
				max = lvWidth;
			}
		}
		return max;
	}

	private int calcLevelStartX(Level lv) {
		if (ROOT_ALIGN == ROOT_ALIGN_left) {
			return INDENT_X;
		}
		if (ROOT_ALIGN == ROOT_ALIGN_middle) {
			int lvWidht = lv.calcLevelWidth();
			return ((maxLevelWidth - lvWidht) / 2) + INDENT_X;
		}
		return 0;

	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics gCopy = g.create();

		drawLevels(gCopy);

		gCopy.dispose();
	}

	private void drawLevels(Graphics gCopy) {
		for (int i = 0; i < listLevel.size(); i++) {
			Level lv = listLevel.get(i);
			int x = calcLevelStartX(lv);
			int y = INDENT_Y + (Level.block_v_gap + Level.block_height) * i
					+ Level.block_v_gap;
			drawEachLevel(gCopy, lv, x, y);
		}
	}

	private void drawEachLevel(Graphics gCopy, Level lv, int levelX, int levelY) {
		int blockX = levelX;
		int blockY = levelY;
		for (int i = 0; i < lv.listBlock.size(); i++) {
			Block b = lv.listBlock.get(i);
			drawEachBlock(gCopy, blockX, blockY, b);
			blockX += b.calcBlockWidth() + Level.block_h_gap;
		}
	}

	private void drawEachBlock(Graphics gCopy, int blockX, int blockY, Block b) {
		int blockWidth = b.calcBlockWidth();
		gCopy.setColor(Color.red);
		// draw block
		gCopy.drawRect(blockX, blockY, blockWidth, Level.block_height);
		drawVerticalLines(gCopy, blockX, blockY, b);
		drawCircles(gCopy, blockX, blockY, b);
		drawNodeString(gCopy, blockX, blockY, b);
	}

	private void drawVerticalLines(Graphics gCopy, int blockX, int blockY,
			Block b) {
		Color orgn = gCopy.getColor();
		Color refColor = Color.red;
		Color nodeColor = Color.red;
		gCopy.setColor(refColor);
		int firstRefX = blockX + Level.ref_width;
		int firstNodeX = blockX + Level.ref_plus_node_width;
		int y1 = blockY;
		int y2 = blockY + Level.block_height;
		for (int i = 0; i < b.listNode.size(); i++) {
			int refX = firstRefX + (Level.ref_plus_node_width * i);
			gCopy.setColor(refColor);
			gCopy.drawLine(refX, y1, refX, y2);

			int nodeX = firstNodeX + (Level.ref_plus_node_width * i);
			gCopy.setColor(nodeColor);
			gCopy.drawLine(nodeX, y1, nodeX, y2);
		}
		gCopy.setColor(orgn);
	}

	private void drawCircles(Graphics gCopy, int blockX, int blockY, Block b) {
		Color orgn = gCopy.getColor();
		Color circleColor = Color.black;
		gCopy.setColor(circleColor);
		int firstX = blockX + (Level.ref_width / 2) - Level.cicle_radius;
		int y = blockY + (Level.block_height / 2) - Level.cicle_radius;

		if (b.listNode.size() > 0) {
			for (int i = 0; i < b.listNode.size() + 1; i++) {
				int x = firstX + (Level.ref_plus_node_width * i);
				gCopy.fillOval(x, y, Level.cicle_radius * 2,
						Level.cicle_radius * 2);
			}
		}
		gCopy.setColor(orgn);
	}

	private void drawNodeString(Graphics gCopy, int blockX, int blockY, Block b) {
		Color orgn = gCopy.getColor();
		Color fontColor = Color.black;
		gCopy.setColor(fontColor);
		int firstRefX = blockX + Level.ref_width;
		int y = blockY;

		for (int i = 0; i < b.listNode.size(); i++) {
			String str = b.listNode.get(i).reprentation();
			// draw string
			int strWidth = SwingUtilities.computeStringWidth(
					getFontMetrics(getFont()), str);
			int strAscent = getFontMetrics(getFont()).getAscent();

			int x = firstRefX + (Level.ref_plus_node_width * i);
			gCopy.drawString(str, (x + (Level.node_width - strWidth) / 2),
					(y + (Level.block_height + strAscent) / 2));
		}
		gCopy.setColor(orgn);
	}

	public static void test_FullTree() {
		List<Level> listLevel = new ArrayList<Level>();
		Level lv0 = new Level();
		System.out.println(Arrays.toString(Toolkit.getDefaultToolkit()
				.getFontList()));
		Font.getFont("Monospaced");
		Block b0_0 = new Block();
		Block b0_1 = new Block();
		lv0.listBlock.add(b0_0);
		lv0.listBlock.add(b0_1);

		b0_0.listNode.add(new VisibleGNode());

		b0_1.listNode.add(new VisibleGNode());

		Level lv1 = new Level();
		Block b1_0 = new Block();
		Block b1_1 = new Block();
		lv1.listBlock.add(b1_0);
		lv1.listBlock.add(b1_1);

		b1_0.listNode.add(new VisibleGNode());
		b1_0.listNode.add(new VisibleGNode());
		b1_0.listNode.add(new VisibleGNode());
		b1_0.listNode.add(new VisibleGNode());
		b1_0.listNode.add(new VisibleGNode());

		b1_1.listNode.add(new VisibleGNode());
		b1_1.listNode.add(new VisibleGNode());
		b1_1.listNode.add(new VisibleGNode());
		b1_1.listNode.add(new VisibleGNode());
		b1_1.listNode.add(new VisibleGNode());
		b1_1.listNode.add(new VisibleGNode());
		b1_1.listNode.add(new VisibleGNode());
		b1_1.listNode.add(new VisibleGNode());
		b1_1.listNode.add(new VisibleGNode());

		Level lv2 = new Level();
		Block b2_0 = new Block();
		Block b2_1 = new Block();
		lv2.listBlock.add(b2_0);
		lv2.listBlock.add(b2_1);

		b2_0.listNode.add(new VisibleGNode());

		b2_1.listNode.add(new VisibleGNode());
		b2_1.listNode.add(new VisibleGNode());
		b2_1.listNode.add(new VisibleGNode());
		b2_1.listNode.add(new VisibleGNode());
		b2_1.listNode.add(new VisibleGNode());
		b2_1.listNode.add(new VisibleGNode());

		listLevel.add(lv0);
		listLevel.add(lv1);
		listLevel.add(lv2);
		JFrame frm = new JFrame();
		frm.getContentPane().add(new ShowGraphComponent(listLevel));
		frm.setVisible(true);
		frm.setSize(1100, 500);
		frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		test_FullTree();
	}

}
