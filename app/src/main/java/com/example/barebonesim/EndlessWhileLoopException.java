package com.example.barebonesim;

public class EndlessWhileLoopException extends Exception {

    public EndlessWhileLoopException() {
        super("WHILE loop must have an END statement.");
    }

}
