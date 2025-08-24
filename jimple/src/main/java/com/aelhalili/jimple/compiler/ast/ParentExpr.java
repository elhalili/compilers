package com.aelhalili.jimple.compiler.ast;

public class ParentExpr extends Expr {
    private Expr inner;
    public ParentExpr(Expr inner) {
        this.inner = inner;
    }

    public Expr getInner() {
        return inner;
    }

    public void setInner(Expr inner) {
        this.inner = inner;
    }
}
