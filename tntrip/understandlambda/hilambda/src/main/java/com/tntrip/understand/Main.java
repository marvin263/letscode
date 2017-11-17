package com.tntrip.understand;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {
    public static void main(String[] args) {
        JButton btn = new JButton();

        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("You just clicked me");
            }
        });

        btn.addActionListener((ActionEvent e) -> System.out.println("You just clicked me"));
    }
}
