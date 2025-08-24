package com.aelhalili.jimple.compiler.ast;

import java.util.ArrayList;
import java.util.List;

public class IfStmt extends Stmt {
    private Expr condition;
    private List<Stmt> thenStmt;
    private List<Stmt> elseStmt;

    public IfStmt(Expr condition, List<Stmt> thenStmt, List<Stmt> elseStmt) {
        this.condition = condition;
        this.thenStmt = thenStmt;
        this.elseStmt = elseStmt;
    }

    public Expr getCondition() {
        return condition;
    }

    public void setCondition(Expr condition) {
        this.condition = condition;
    }

    public List<Stmt> getThenStmt() {
        return thenStmt;
    }

    public void setThenStmt(List<Stmt> thenStmt) {
        this.thenStmt = thenStmt;
    }

    public List<Stmt> getElseStmt() {
        return elseStmt;
    }

    public void setElseStmt(List<Stmt> elseStmt) {
        this.elseStmt = elseStmt;
    }
}
