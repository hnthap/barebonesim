package com.example.barebonesim;

import org.apache.commons.lang3.StringUtils;

public class InvalidVariableNameException extends Exception {

    @SuppressWarnings("unused")
    private InvalidVariableNameException() { }

    public InvalidVariableNameException(final String var, final String stmt) {
        super(String.format(
            "Invalid variable name \"%s\" in: %s",
            var, 
            stmt ));
    }

    public InvalidVariableNameException(final String var, final String[] stmt) {
        this(var, StringUtils.join(stmt, " "));
    }
    
}
