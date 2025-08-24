package com.aelhalili.jimple.compiler.ast;

public class VarExpr extends Expr {
    public String name;
    public VarExpr(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
