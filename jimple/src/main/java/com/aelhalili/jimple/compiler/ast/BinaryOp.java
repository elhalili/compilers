package com.aelhalili.jimple.compiler.ast;

public class BinaryOp extends Expr {
    private Expr left;
    private Expr right;
    private String  op;
    public BinaryOp(Expr left, Expr right, String op) {

    }

    public Expr getLeft() {
        return left;
    }

    public void setLeft(Expr left) {
        this.left = left;
    }

    public Expr getRight() {
        return right;
    }

    public void setRight(Expr right) {
        this.right = right;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }
}
