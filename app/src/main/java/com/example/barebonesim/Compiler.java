package com.example.barebonesim;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JOptionPane;

public class Compiler {

    private static AstNode tree = new EmptyAstNode();
    
    private static VariableContainer input = new VariableContainer();
    private static VariableContainer output = new VariableContainer();

    private static String latestCode = "";

    private static AtomicBoolean safelyCompiled = new AtomicBoolean(false);

    private Compiler() {
        // Do nothing
    }

    public static VariableContainer getInput() {
        return input;
    }

    public static void setInputAsOutput() {
        input = output.clone();
    }

    public static VariableContainer getOutput() {
        return output;
    }

    public static void setTree(AstNode node) {
        tree = node;
        System.gc();
    }

    public static void resetTree() {
        setTree(null);
    }

    public static boolean treeIsNull() {
        return (tree != null);
    }

    public static final boolean isSafelyCompiled() {
        return safelyCompiled.get();
    }

    public static void setSafelyCompiled(boolean safe) {
        safelyCompiled.set(safe);
    }

    public static void compile() {
        output = input.clone();
        AstNode node = tree;
        while (node != null && safelyCompiled.get()) {
            node.evaluate();
            node = node.getNext();
        }
    }

    public static boolean compile(int timeoutMiliSeconds) {

        safelyCompiled.set(true);
        ExecutorService executor = Executors.newCachedThreadPool();
        Callable<Object> task = new Callable<Object>() {
            @Override
            public Object call() {
                compile();
                return null;
            }
        };
        Future<Object> future = executor.submit(task);
        boolean good = false;
        try {
            future.get(timeoutMiliSeconds, TimeUnit.MILLISECONDS);
            good = true;
        } catch (TimeoutException | InterruptedException |
            ExecutionException e
        ) {

        } finally {
            safelyCompiled.set(false);
            future.cancel(true);
        }
        executor.shutdownNow();
        return good;

    }

    public static String beautify(final String source) {

        if (source.equals(latestCode)) {
            return null;
        }
        tree = null;
        try {
            tree = Parser.parse(Scanner.scan(source));
        } catch (InvalidSyntaxException | InvalidVariableNameException |
            EndlessWhileLoopException e) {
            
            JOptionPane.showMessageDialog(null,
                e.getMessage(),
                e.getClass().getSimpleName(),
                JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return latestCode = AstNode.travelToEnd(tree);

    }
}
