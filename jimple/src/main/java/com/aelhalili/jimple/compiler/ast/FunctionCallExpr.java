package com.aelhalili.jimple.compiler.ast;

import java.util.List;

public class FunctionCallExpr extends Expr {
    private String functionName;
    private List<Expr> arguments;
    public FunctionCallExpr(String functionName, List<Expr> arguments) {
        this.functionName = functionName;
        this.arguments = arguments;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public List<Expr> getArguments() {
        return arguments;
    }

    public void setArguments(List<Expr> arguments) {
        this.arguments = arguments;
    }
}
