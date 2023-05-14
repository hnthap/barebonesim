package com.example.barebonesim;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public abstract class AstNode {

    protected abstract void setNext(AstNode node);

    protected abstract AstNode getNext();

    protected abstract void evaluate();

    public String toStringWithTab(int tabCount) {
        String result = "";
        for (int i = 0; i < tabCount; ++i) {
            result += "    ";
        }
        return result + toString();
    }

    public static String travelToEnd(AstNode tree) {
        String source = "";
        AstNode node = tree;
        while (node != null) {
            if (!(node instanceof EmptyAstNode)) {
                source += node.toStringWithTab(0);
            }
            node = node.getNext();
        }
        return source;
    }
    
}

class EmptyAstNode extends AstNode {

    private AstNode next = null;

    public EmptyAstNode() { }

    @Override
    public void setNext(AstNode node) {
        this.next = node;
    }

    @Override
    public AstNode getNext() {
        return next;
    }

    @Override
    public void evaluate() {
        
    }

    @Override
    public String toString() {
        return ";\n";
    }
    
}

class IncrNode extends AstNode {

    private final String VAR;
    private AstNode next = null;

    public IncrNode(final String var) {
        this.VAR = var;
    }

    @Override 
    public void setNext(AstNode node) {
        this.next = node;
    }

    @Override 
    public AstNode getNext() {
        return next;
    }

    @Override 
    public void evaluate() {
        Optional<Integer> value = Compiler.getOutput().getValue(VAR);
        if (value.isEmpty()) {
            Compiler.getOutput().setValue(VAR, 1);
        } else if (value.get() + 1 > 0) {
            Compiler.getOutput().setValue(VAR, value.get() + 1);
        }
    }

    @Override
    public String toString() {
        return String.format("incr %s;\n", VAR);
    }
    
}

class DecrNode extends AstNode {

    private final String VAR;
    private AstNode next = null;

    public DecrNode(final String var) {
        this.VAR = var;
    }

    @Override 
    public void setNext(AstNode node) {
        this.next = node;
    }

    @Override 
    public AstNode getNext() {
        return next;
    }

    @Override 
    public void evaluate() {
        Optional<Integer> value = Compiler.getOutput().getValue(VAR);
        if (value.isEmpty()) {
            Compiler.getOutput().setValue(VAR, 0);
        } else if (value.get() != 0) {
            Compiler.getOutput().setValue(VAR, value.get() - 1);
        }
    }

    @Override
    public String toString() {
        return String.format("decr %s;\n", VAR);
    }
 
}

class ClearNode extends AstNode {

    private final String VAR;
    private AstNode next = null;

    public ClearNode(final String var) {
        this.VAR = var;
    }

    @Override 
    public void setNext(AstNode node) {
        this.next = node;
    }

    @Override 
    public AstNode getNext() {
        return next;
    }

    @Override 
    public void evaluate() {
        Compiler.getOutput().setValue(VAR, 0);
    }

    @Override
    public String toString() {
        return String.format("clear %s;\n", VAR);
    }
 
}

class WhileNode extends AstNode {

    private final String CONDITION_VAR;
    private AstNode statementList;
    private AstNode next = null;

    public WhileNode(final String conditionVar, AstNode actions) {
        this.CONDITION_VAR = conditionVar;
        this.statementList = actions;
    }

    @Override
    public void setNext(AstNode node) {
        this.next = node;
    }

    @Override
    public AstNode getNext() {
        return next;
    }

    @Override
    public void evaluate() {
        Optional<Integer> condition = Compiler.getOutput()
            .getValue(CONDITION_VAR);
        if (condition.isEmpty()) {
            return ;
        } // If CONDITION_VAR doesn't hold a value, do nothing
        
        AstNode statement;
        while (condition.get() != 0 && Compiler.isSafelyCompiled()) {
            statement = statementList;
            while (statement != null) {
                statement.evaluate();
                statement = statement.getNext();
            }
            condition = Compiler.getOutput().getValue(CONDITION_VAR);
        }
    }

    private Object[] convertStatementsToArray(int tabCount) {
        List<String> result = new LinkedList<>();
        AstNode node = statementList;
        while (node != null) {
            if (!(node instanceof EmptyAstNode)) {
                result.add(node.toStringWithTab(tabCount + 1));
            }
            node = node.getNext();
        }
        return result.toArray();
    }

    @Override
    public String toString() {
        String res = String.format("while %s not 0 do\n", CONDITION_VAR);
        AstNode node = statementList;
        while (node != null) {
            if (!(node instanceof EmptyAstNode)) {
                res += node.toString();
            }
            node = node.getNext();
        }
        res += "end;\n";
        return res;
    }

    @Override
    public String toStringWithTab(int tabCount) {
        String result = "";
        for (int i = 0; i < tabCount; ++i) {
            result += "    ";
        }
        result += String.format("while %s not 0 do\n", CONDITION_VAR);
        Object[] statements = convertStatementsToArray(tabCount);
        for (final Object statement : statements) {
            result += (String)statement;
        }
        for (int i = 0; i < tabCount; ++i) {
            result += "    ";
        }
        result += "end;\n";
        return result;
    }

}
