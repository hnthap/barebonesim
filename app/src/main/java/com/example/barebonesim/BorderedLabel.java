package com.example.barebonesim;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

public class BorderedLabel extends JLabel {
    
    public BorderedLabel(String text) {
        super(text);
        setAlignmentX(CENTER_ALIGNMENT);
        setFont(getFont().deriveFont(14.0f));
        setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 5));
    }

}
