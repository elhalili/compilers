package com.aelhalili.jimple.compiler.ast;

public class ReturnStmt extends Stmt {
    private Expr expr;

    public ReturnStmt(Expr expr) {
        this.expr = expr;
    }

    public Expr getExpr() {
        return expr;
    }

    public void setExpr(Expr expr) {
        this.expr = expr;
    }
}
