package com.example.barebonesim;

import java.io.File;
import java.util.Optional;

import javax.swing.JFileChooser;

public class CustomFileChooser extends JFileChooser {

    public CustomFileChooser(Optional<String> dir) {
        super();
        if (dir.isPresent()) {
            setCurrentDirectory(new File(dir.get()));
        }
    }

    public CustomFileChooser(Optional<String> dir, String arg) {
        super(arg);
        if (dir.isPresent()) {
            setCurrentDirectory(new File(dir.get()));
        }
    }
    
    public Optional<String> getDirectoryAsOptional() {
        return Optional.of(getCurrentDirectory().getAbsolutePath());
    }
    
}
