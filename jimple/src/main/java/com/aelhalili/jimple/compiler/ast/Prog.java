package com.aelhalili.jimple.compiler.ast;

import java.util.List;

public class Prog extends Node {
    private List<Node> stmts;
    public Prog(List<Node> stmts) {
        this.stmts = stmts;
    }

    public List<Node> getStmts() {
        return stmts;
    }

    public void setStmts(List<Node> stmts) {
        this.stmts = stmts;
    }
}
