package com.aelhalili.jimple.compiler.ast;

import java.util.List;

public class WhileStmt extends Stmt {
    private Expr condition;
    private List<Stmt> body;

    public WhileStmt(Expr condition, List<Stmt> body) {
        this.condition = condition;
        this.body = body;
    }

    public Expr getCondition() {
        return condition;
    }

    public void setCondition(Expr condition) {
        this.condition = condition;
    }

    public List<Stmt> getBody() {
        return body;
    }

    public void setBody(List<Stmt> body) {
        this.body = body;
    }
}
