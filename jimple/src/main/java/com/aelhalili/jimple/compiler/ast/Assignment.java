package com.aelhalili.jimple.compiler.ast;

public class Assignment extends Stmt {
    private String varName;
    private Expr expr;
    public Assignment(String varName, Expr expr) {
        this.varName = varName;
        this.expr = expr;
    }

    public String getVarName() {
        return varName;
    }
    public Expr getExpr() {
        return expr;
    }

    public void setExpr(Expr expr) {
        this.expr = expr;
    }
    public void setVarName(String varName) {
        this.varName = varName;
    }
}

