package com.example.barebonesim;

import org.apache.commons.lang3.StringUtils;

public class InvalidSyntaxException extends Exception {

    public InvalidSyntaxException() {
        super("Invalid syntax.");
    }

    public InvalidSyntaxException(final String stmt) {
        super("Invalid syntax: " + stmt);
    }

    public InvalidSyntaxException(final String stmt[]) {
        this(StringUtils.join(stmt, " "));
    }
    
}
