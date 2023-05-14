package com.example.barebonesim;

import java.awt.event.ActionListener;

import javax.swing.JButton;

public class CustomButton extends JButton {

    public CustomButton(String arg0, ActionListener actionListener, String cmd) {
        super(arg0);
        addActionListener(actionListener);
        setActionCommand(cmd);
    }
    
}
