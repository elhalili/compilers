package com.aelhalili.jimple.compiler.ast;

public class NumLiteral extends Expr {
    private double value;
    public NumLiteral(double value) {

    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }
}
