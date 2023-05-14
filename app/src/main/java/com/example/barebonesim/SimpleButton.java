package com.example.barebonesim;

import java.awt.event.ActionListener;

import javax.swing.BorderFactory;

public class SimpleButton extends CustomButton {

    public SimpleButton(String arg0, ActionListener actionListener, String cmd) {
        super(arg0, actionListener, cmd);
        setToolTipText(cmd);
        
        setFont(getFont().deriveFont(18.0f));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setContentAreaFilled(false);
    }
    
}
