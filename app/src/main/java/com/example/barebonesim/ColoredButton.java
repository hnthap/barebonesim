package com.example.barebonesim;

import java.awt.Color;
import java.awt.event.ActionListener;

public class ColoredButton extends CustomButton {

    public ColoredButton(String arg0, ActionListener actionListener, 
        String cmd) {

        super(arg0, actionListener, cmd);
        setFont(getFont().deriveFont(14.0f));
        setBackground(Color.decode("0x0E86D4"));
        setForeground(Color.WHITE);
        setBorderPainted(false);
        setAlignmentX(CENTER_ALIGNMENT);
    }
    
}
