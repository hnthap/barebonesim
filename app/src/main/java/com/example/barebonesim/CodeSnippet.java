package com.example.barebonesim;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

enum SnippetVarType {
    FIRST, SECOND, RESULT, TEMPORARY, RESULT_SIGN
}

public enum CodeSnippet {
    
    ASSIGN ("Y := X","assign.txt",
        new String[]{ "X", "Y", "T" },
        new SnippetVarType[]{ 
            SnippetVarType.FIRST,
            SnippetVarType.RESULT, SnippetVarType.TEMPORARY }),
    ADD ("Z := X + Y", "add.txt",
        new String[]{ "X", "Y", "Z", "T" },
        new SnippetVarType[]{ 
            SnippetVarType.FIRST, SnippetVarType.SECOND,
            SnippetVarType.RESULT, SnippetVarType.TEMPORARY }),
    SUBTRACT ("Z := X - Y", "subtract.txt",
        new String[]{ "X", "Y", "Z", "T" },
        new SnippetVarType[]{ 
            SnippetVarType.FIRST, SnippetVarType.SECOND,
            SnippetVarType.RESULT, SnippetVarType.TEMPORARY }),
    MULTIPLY ("Z := X * Y", "multiply.txt",
        new String[]{ "X", "Y", "Z", "T_X", "T_Y" },
        new SnippetVarType[]{ 
            SnippetVarType.FIRST, SnippetVarType.SECOND,
            SnippetVarType.RESULT, SnippetVarType.TEMPORARY,
            SnippetVarType.TEMPORARY }),
    INT_DIVIDE ("Z := floor(X / Y)",
        "int_divide.txt",
        new String[]{ "X", "Y", "Z", "T", "T_X", "T_Y" },
        new SnippetVarType[]{
            SnippetVarType.FIRST, SnippetVarType.SECOND,
            SnippetVarType.RESULT, SnippetVarType.TEMPORARY,
            SnippetVarType.TEMPORARY, SnippetVarType.TEMPORARY }),
    TOGGLE ("if X != 0 then X := 0 else X := 1",
        "toggle.txt",
        new String[]{ "X", "T" },
        new SnippetVarType[]{ SnippetVarType.FIRST, SnippetVarType.TEMPORARY }),
    LESS_THAN ("if X < Y then Z := 0 else Z := 1",
        "less_than.txt",
        new String[]{ "X", "Y", "Z", "T_X", "T_Y" },
        new SnippetVarType[]{
            SnippetVarType.FIRST, SnippetVarType.SECOND,
            SnippetVarType.RESULT,
            SnippetVarType.TEMPORARY, SnippetVarType.TEMPORARY }),
    NOT_LESS_THAN ("if X >= Y then Z := 0 else Z := 1",
        "not_less_than.txt",
        new String[]{ "X", "Y", "Z", "T_X", "T_Y" },
        new SnippetVarType[]{
            SnippetVarType.FIRST, SnippetVarType.SECOND,
            SnippetVarType.RESULT,
            SnippetVarType.TEMPORARY, SnippetVarType.TEMPORARY }),
    GREATER_THAN ("if X > Y then Z := 0 else Z := 1",
        "greater_than.txt",
        new String[]{ "X", "Y", "Z", "T_X", "T_Y" },
        new SnippetVarType[]{
            SnippetVarType.FIRST, SnippetVarType.SECOND,
            SnippetVarType.RESULT,
            SnippetVarType.TEMPORARY, SnippetVarType.TEMPORARY }),
    NOT_GREATER_THAN ("if X <= Y then Z := 0 else Z := 1",
        "not_greater_than.txt",
        new String[]{ "X", "Y", "Z", "T_X", "T_Y" },
        new SnippetVarType[]{
            SnippetVarType.FIRST, SnippetVarType.SECOND,
            SnippetVarType.RESULT,
            SnippetVarType.TEMPORARY, SnippetVarType.TEMPORARY }),
    EQUALS ("if X = Y then Z := 0 else Z := 1",
        "equals.txt",
        new String[]{ "X", "Y", "Z", "T_X_Y", "T_Y_X" },
        new SnippetVarType[]{
            SnippetVarType.FIRST, SnippetVarType.SECOND,
            SnippetVarType.RESULT,
            SnippetVarType.TEMPORARY, SnippetVarType.TEMPORARY }),
    NOT_EQUALS ("if X != Y then Z := 0 else Z := 1",
        "not_equals.txt",
        new String[]{ "X", "Y", "Z", "T_X_Y", "T_Y_X" },
        new SnippetVarType[]{
            SnippetVarType.FIRST, SnippetVarType.SECOND,
            SnippetVarType.RESULT,
            SnippetVarType.TEMPORARY, SnippetVarType.TEMPORARY }),
    CHANGE_SIGN ("X := -X",
        "change_sign.txt",
        new String[]{ "X", "X_NEG", "T" },
        new SnippetVarType[]{
            SnippetVarType.FIRST, SnippetVarType.RESULT_SIGN,
            SnippetVarType.TEMPORARY });

    private static final String SNIPPET_DIRECTORY =
        "src/main/resources/snippets/";
    
    private final String name;
    private final String barebones;
    private final String[] vars;
    private final SnippetVarType[] roles;

    private CodeSnippet(
            String name, String codeFileName,
            String[] vars, SnippetVarType[] roles
    ) {
        this.name = name;
        this.vars = new String[vars.length];
        for (int i = 0; i < vars.length; ++i) {
            this.vars[i] = vars[i];
        }
        this.roles = new SnippetVarType[roles.length];
        for (int i = 0; i < vars.length; ++i) {
            this.roles[i] = roles[i];
        }

        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader =
                new BufferedReader(new FileReader(
                SNIPPET_DIRECTORY + codeFileName))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.lineSeparator());
            }
        } catch (IOException e) {

        }
        this.barebones = builder.toString();
    }

    public static final CodeSnippet ofName(String name) {
        switch (name) {
        case "Y := X":
            return ASSIGN;
        case "Z := X + Y":
            return ADD;
        case "Z := X - Y":
            return SUBTRACT;
        case "Z := X * Y":
            return MULTIPLY;
        case "Z := floor(X / Y)":
            return INT_DIVIDE;
        case "if X != 0 then X := 0 else X := 1":
            return TOGGLE;
        case "if X < Y then Z := 0 else Z := 1":
            return LESS_THAN;
        case "if X >= Y then Z := 0 else Z := 1":
            return NOT_LESS_THAN;
        case "if X > Y then Z := 0 else Z := 1":
            return GREATER_THAN;
        case "if X <= Y then Z := 0 else Z := 1":
            return NOT_GREATER_THAN;
        case "if X = Y then Z := 0 else Z := 1":
            return EQUALS;
        case "if X != Y then Z := 0 else Z := 1":
            return NOT_EQUALS;
        default:
            assert name == "X := -X";
            return CHANGE_SIGN;
        }
    }

    public final String getName() {
        return name;
    }

    public final String[] getDefaultVarNames() {
        return vars;
    }

    public final SnippetVarType[] getRoles() {
        return roles;
    }

    public final String getLabelTextForVarByIndex(int index) {
        assert (index < 0 || index >= roles.length);
        switch (roles[index]) {
        case FIRST:
        case SECOND:
        case RESULT:
            return String.format("<html>Rename %s to . . .</html>",
                vars[index]);
        case RESULT_SIGN:
            return "<html>Name the variable representing<br/>" +
                "the result's sign<br/>" +
                "<small>It is 1 if the result is negative, " +
                "otherwise zero</small></html>";
        default:
            assert roles[index] == SnippetVarType.TEMPORARY;
            return "<html>Name a temporary variable<br/>" +
                "<small>Should not conflict with other variables<br/>" +
                "and should start with \"T\"</small>" +
                "</html>";
        }
    }

    public final String getBareBonesCode(String[] newVarNames) {
        return StringUtils.replaceEach(barebones, vars, newVarNames);
    }

}
