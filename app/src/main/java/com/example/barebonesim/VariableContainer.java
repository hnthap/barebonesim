package com.example.barebonesim;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

public class VariableContainer {

    private HashMap<String, Integer> vars = new HashMap<>();

    public VariableContainer() { }

    public Optional<Integer> getValue(String varName) {
        varName = varName.toUpperCase().trim();
        if (vars.containsKey(varName)) {
            return Optional.of(vars.get(varName));
        }
        return Optional.empty();
    }

    public void setValue(final String varName, final int value) {
        vars.put(varName.toUpperCase().trim(), value);
    }

    public void removeVar(String varName) {
        if (varName == null) {
            return ;
        }
        varName = varName.toUpperCase().trim();
        if (vars.containsKey(varName)) {
            vars.remove(varName);
        }
    }

    public boolean isEmpty() {
        return vars.isEmpty();
    }

    public Object[] getVarList() {
        return vars.keySet().toArray();
    }

    public void clear() {
        vars.clear();
    }

    public String toHtml() {
        if (vars.isEmpty()) {
            return "";
        }

        String s = "<html><table>";
        s += "<tr><th>VAR</th><th>VALUE</th></tr>";
        for (final Map.Entry<String, Integer> entry : vars.entrySet()) {
            s += "<tr><td style='text-align:center;'>" + 
                entry.getKey() + "</td><td style='text-align:center;'>" + 
                entry.getValue().toString() + "</td></tr>";
        }
        return s + "</table></html>";
    }

    public void readFromCsv(File file) 
        throws IOException, InvalidVariableNameException {
        
        Reader data = new FileReader(file);
        CSVParser parser = CSVFormat.RFC4180.parse(data);

        Compiler.getInput().clear();

        String var, valueStr;
        int value;
        for (final CSVRecord record : parser) {
            var = record.get(0).toUpperCase().trim();
            valueStr = record.get(1).trim();

            // Check if the information provided is valid
    
            if (!VariableContainer.isValidVarName(var)) {
                throw new InvalidVariableNameException(var, "");
            }
            
            value = Integer.parseInt(valueStr);
            if (value < 0) {
                value = 0;
            }

            // Add the input

            Compiler.getInput().setValue(var, value);
        }
        parser.close();


    }

    public void writeToCsv(File file) throws IOException {
        FileWriter fwriter = new FileWriter(file, true);
        try (CSVPrinter printer = new CSVPrinter(fwriter, CSVFormat.RFC4180)) {
            for (final Map.Entry<String, Integer> entry : vars.entrySet()) {
                printer.printRecord(entry.getKey(), entry.getValue());
            }
            printer.flush();

        }
    }

    @SuppressWarnings("unchecked")
    public VariableContainer clone() {
        
        VariableContainer clone = new VariableContainer();
        clone.vars = (HashMap<String, Integer>)vars.clone();
        return clone;
        
    }

    public static boolean isValidVarName(final String varName) {
        if (varName.isEmpty()) {
            return false;
        }
        if (varName.length() > 31) {
            return false;
        }
        if (!varName.substring(0, 1).matches("^[a-zA-Z_]+$")) {
            return false;
        }
        if (!varName.matches("^[a-zA-Z0-9_]+$")) {
            return false;
        }
        if (varName.toLowerCase()
                .matches("clear|decr|do|end|incr|not|while")
        ) {
            return false;
        }
        return true;
    }
    
}
