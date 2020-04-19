package com.tree.showtree;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

public abstract class AbstractShowTreeFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    protected ShowTreeComponent previousTreeComp;
    protected VisibleNode previousRoot;

    protected ShowTreeComponent currentTreeComp;
    protected VisibleNode currentRoot;

    private JTextField textField;
    private JLabel noteLabel;

    public AbstractShowTreeFrame(VisibleNode root) {
        this.currentRoot = root;
        initComponent();
    }

    private void initComponent() {
        this.getContentPane().add(createCenterPanel(), BorderLayout.CENTER);
        this.getContentPane().add(createSouthPanel(), BorderLayout.SOUTH);
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(2, 1));

        previousTreeComp = new ShowTreeComponent(null);
        previousTreeComp.setBorder(new TitledBorder("Previous"));

        currentTreeComp = new ShowTreeComponent(currentRoot);
        currentTreeComp.setBorder(new TitledBorder("Current"));

        centerPanel.add(previousTreeComp);
        centerPanel.add(currentTreeComp);
        return centerPanel;
    }

    private JPanel createSouthPanel() {
        JPanel south = new JPanel();
        south.setLayout(new FlowLayout(FlowLayout.CENTER));
        JButton btnAdd = new JButton("Add");
        south.add(btnAdd);
        south.add(new JSeparator());
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clickAdd(e);
            }
        });
        JButton btnDelete = new JButton("Delete");
        south.add(btnDelete);
        south.add(new JSeparator());
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clickDelete(e);
            }
        });
        textField = new JTextField(5);
        south.add(textField);

        noteLabel = new JLabel();
        noteLabel.setForeground(Color.red);
        south.add(noteLabel);
        return south;
    }

    private void clickAdd(ActionEvent e) {
        String newValue = textField.getText();
        try {
            Integer.valueOf(newValue);
        } catch (NumberFormatException e1) {
            setNoteText("Digit only");
            return;
        }

        insertNode(Integer.valueOf(newValue));
        installNewTree("Added: " + newValue);
    }

    private void clickDelete(ActionEvent e) {
        VisibleNode node = currentTreeComp.getLastClickNode();
        if (node == null) {
            return;
        }

        deleteNode(node);
        installNewTree("Deleted: " + node.presentation());
    }

    private void installNewTree(String msg) {
        previousTreeComp.installNewTree(previousRoot);
        currentTreeComp.installNewTree(currentRoot);
        setNoteText(msg);
    }

    private void setNoteText(String msg) {
        noteLabel.setText(msg);
        textField.setSelectionStart(0);
        textField.setSelectionEnd(textField.getText().length());
        textField.requestFocus();
    }

    /**
     * Change both previousRoot and currentRoot;
     * 
     * @param newValue
     */
    public abstract void insertNode(int newValue);

    /**
     * Change both previousRoot and currentRoot;
     * 
     * @param nodeToBeDeleted
     */
    public abstract void deleteNode(VisibleNode nodeToBeDeleted);

    public static void showTreeFrm(AbstractShowTreeFrame frm, String frmTitle) {
        frm.setSize(700, 500);
        frm.setTitle(frmTitle);
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.setVisible(true);
    }
}
