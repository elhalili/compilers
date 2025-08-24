package com.aelhalili.jimple.compiler.ast;

public class StringLiteral extends Expr {
    private String value;
    public StringLiteral(String value) {

    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
