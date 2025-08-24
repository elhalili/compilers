package com.aelhalili.jimple.compiler.ast;

import java.util.List;
import java.util.Map;

public class FunctionDecl extends Node {
    private String name;
    private Map<String, Type> params;
    private List<Stmt> body;
    private Type returnType;

    public FunctionDecl(String name, Map<String, Type> params, Type returnType, List<Stmt> body) {
        this.name = name;
        this.params = params;
        this.returnType = returnType;
        this.body = body;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Type> getParams() {
        return params;
    }

    public void setParams(Map<String, Type> params) {
        this.params = params;
    }

    public Type getReturnType() {
        return returnType;
    }

    public void setReturnType(Type returnType) {
        this.returnType = returnType;
    }

    public List<Stmt> getBody() {
        return body;
    }

    public void setBody(List<Stmt> body) {
        this.body = body;
    }
}
