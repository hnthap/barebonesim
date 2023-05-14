package com.example.barebonesim;

import java.util.List;
import java.util.ListIterator;

public class Parser {
    
    private Parser() { }

    public static AstNode parse(final List<String[]> tokens) 
        throws InvalidSyntaxException, InvalidVariableNameException, 
        EndlessWhileLoopException {

        if (tokens.size() == 1) {
            if (tokens.get(0)[0].length() == 0) {
                return new EmptyAstNode();
            } 
        }

        ListIterator<String[]> it = tokens.listIterator();

        return parse(it, true);

    }

    private static AstNode parse(ListIterator<String[]> it,
        boolean notInWhile) 
        throws InvalidSyntaxException, InvalidVariableNameException {

        AstNode root = new EmptyAstNode();

        AstNode node = root;
        boolean notStop = true; // stop = false
        while (it.hasNext() && notStop) {
            
            String[] stmt = it.next();
            if (stmt.length == 0) {
                continue;
            }

            switch (stmt[0]) {

                case "CLEAR":
                case "DECR":
                case "INCR":
                if (stmt.length != 2) {
                    throw new InvalidSyntaxException(stmt);
                }
                if (!VariableContainer.isValidVarName(stmt[1])) {
                    throw new InvalidVariableNameException(stmt[1], stmt);
                }
                switch (stmt[0]) {
                    case "CLEAR":
                    node.setNext(new ClearNode(stmt[1]));
                    break;
                    case "DECR":
                    node.setNext(new DecrNode(stmt[1]));
                    break;
                    default:
                    assert stmt[0] == "INCR";
                    node.setNext(new IncrNode(stmt[1]));
                }
                break;

                case "WHILE":
                if (stmt.length != 4) {
                    throw new InvalidSyntaxException(stmt);
                }
                if (!stmt[2].equals("NOT") || !stmt[3].equals("0")) {
                    throw new InvalidSyntaxException(stmt);
                }
                if (!VariableContainer.isValidVarName(stmt[1])) {
                    throw new InvalidVariableNameException(stmt[1], stmt);
                }
                AstNode inwhile = parse(it, false);
                if (inwhile == null) {
                    throw new InvalidSyntaxException("Hello, while in while");
                }
                node.setNext(new WhileNode(stmt[1], inwhile));
                break;

                case "END":
                notStop = false; // stop = true
                break;

                case "":
                node.setNext(new EmptyAstNode());
                break;

                default:
                throw new InvalidSyntaxException(stmt);

            }

            node = node.getNext();

        }

        if (it.hasNext() && notInWhile) {
            throw new InvalidSyntaxException("Hello, END");
        }

        return root;

    }

}
