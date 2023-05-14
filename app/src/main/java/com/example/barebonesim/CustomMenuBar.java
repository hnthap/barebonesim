package com.example.barebonesim;

import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

public class CustomMenuBar extends JMenuBar {

    public CustomMenuBar(ActionListener actionListener) {
        super();

        JMenu menu;
        JMenuItem menuItem;

        menu = new JMenu("File");
        menuItem = new JMenuItem("New File");
        menuItem.addActionListener(actionListener);
        menu.add(menuItem);
        menuItem = new JMenuItem("Open File");
        menuItem.addActionListener(actionListener);
        menu.add(menuItem);
        menu.add(new JSeparator());
        menuItem = new JMenuItem("Save");
        menuItem.addActionListener(actionListener);
        menu.add(menuItem);
        menuItem = new JMenuItem("Save As");
        menuItem.addActionListener(actionListener);
        menu.add(menuItem);
        menu.add(new JSeparator());
        menuItem = new JMenuItem("Close File");
        menuItem.addActionListener(actionListener);
        menu.add(menuItem);
        menu.add(new JSeparator());
        menuItem = new JMenuItem("Exit");
        menuItem.addActionListener(actionListener);
        menu.add(menuItem);

        this.add(menu);

        menu = new JMenu("Edit");
        menuItem = new JMenuItem("Cut");
        menuItem.addActionListener(actionListener);
        menu.add(menuItem);
        menuItem = new JMenuItem("Copy");
        menuItem.addActionListener(actionListener);
        menu.add(menuItem);
        menuItem = new JMenuItem("Paste");
        menuItem.addActionListener(actionListener);
        menu.add(menuItem);

        this.add(menu);

        menu = new JMenu("Run");
        menuItem = new JMenuItem("Add Input");
        menuItem.addActionListener(actionListener);
        menu.add(menuItem);
        menuItem = new JMenuItem("Change Input");
        menuItem.addActionListener(actionListener);
        menu.add(menuItem);
        menuItem = new JMenuItem("Remove Input");
        menuItem.addActionListener(actionListener);
        menu.add(menuItem);
        menuItem = new JMenuItem("Get Input from Output");
        menuItem.setActionCommand("Get Input From Output");
        menuItem.addActionListener(actionListener);
        menu.add(menuItem);
        menuItem = new JMenuItem("Get Input from CSV");
        menuItem.setActionCommand("Get Input From CSV");
        menuItem.addActionListener(actionListener);
        menu.add(menuItem);
        menuItem = new JMenuItem("Extract Output to CSV");
        menuItem.setActionCommand("Extract to CSV");
        menuItem.addActionListener(actionListener);
        menu.add(menuItem);
        menu.add(new JSeparator());
        menuItem = new JMenuItem("Run");
        menuItem.addActionListener(actionListener);
        menu.add(menuItem);
        menuItem = new JMenuItem("Reload");
        menuItem.addActionListener(actionListener);
        menu.add(menuItem);
        menuItem = new JMenuItem("Beautify");
        menuItem.addActionListener(actionListener);
        menu.add(menuItem);
        menu.add(new JSeparator());
        menuItem = new JMenuItem("Set Time Limit");
        menuItem.addActionListener(actionListener);
        menu.add(menuItem);
        menuItem = new JMenuItem("Set Time Limit As Default");
        menuItem.addActionListener(actionListener);
        menu.add(menuItem);

        this.add(menu);

        menu = new JMenu("Snippets");
        menuItem = new JMenuItem("Snippets");
        menuItem.addActionListener(actionListener);
        menu.add(menuItem);

        this.add(menu);

        menu = new JMenu("Help");
        menuItem = new JMenuItem("Help");
        menuItem.addActionListener(actionListener);
        menu.add(menuItem);

        this.add(menu);
    }
    
}
