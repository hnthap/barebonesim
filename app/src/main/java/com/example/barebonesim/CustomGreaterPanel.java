package com.example.barebonesim;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class CustomGreaterPanel extends JPanel {

    public CustomGreaterPanel(String name, JLabel content) {

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(Box.createRigidArea(new Dimension(20, 20)));
        
        JLabel nameLabel = new JLabel();
        nameLabel.setText(name);
        nameLabel.setFont(nameLabel.getFont().deriveFont(16.0f));
        nameLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(nameLabel);

        JScrollPane bigScrollPane = new JScrollPane(content,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        bigScrollPane.setBorder(BorderFactory
            .createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5),
                BorderFactory.createLineBorder(Color.BLACK)));
        add(bigScrollPane);

    }
    
}
