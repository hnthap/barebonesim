package com.example.barebonesim;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class Scanner {

    private Scanner() { }

    public static List<String[]> scan(final String source) {

        List<String[]> tokens = new LinkedList<>();
        
        String[] raws = StringUtils.normalizeSpace(source)
            .toUpperCase().split(";| DO ");
        for (final String stmt : raws) {
            tokens.add(StringUtils.normalizeSpace(stmt).split(" "));
        }

        return tokens;

    }
    
}
