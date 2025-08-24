package com.aelhalili.jimple.compiler.semantic;

import com.aelhalili.jimple.compiler.ast.*;

import java.util.*;

public class SemanticAnalyzer {

    private SymbolTable currentScope;

    public void analyze(Prog program) {
        currentScope = new SymbolTable(null);
        for (var item : program.getStmts()) {
            if (item instanceof FunctionDecl f) {
                analyzeFunction(f);
            }
        }
    }

    private void analyzeFunction(FunctionDecl f) {
        SymbolTable functionScope = new SymbolTable(currentScope);

        for (Map.Entry<String,Type> param :  f.getParams().entrySet()) {
            functionScope.declare(param.getKey(), param.getValue());
        }

        analyzeBlock(f.getBody(), functionScope, f.getReturnType());
    }

    private void analyzeBlock(List<Stmt> stmts, SymbolTable scope, Type expectedReturn) {
        for (Stmt s : stmts) {
            if (s instanceof VarDecl v) {
                Type initType = v.getInit() != null ? inferExprType(v.getInit(), scope) : v.getType();
                if (!v.getType().equals(initType)) {
                    throw new RuntimeException("Type error: cannot assign " + initType + " to " + v.getType());
                }
                scope.declare(v.getName(), v.getType());
            }
            else if (s instanceof Assignment a) {
                Type varType = scope.lookup(a.getVarName());
                Type exprType = inferExprType(a.getExpr(), scope);
                if (varType != exprType) {
                    throw new RuntimeException("Type error: cannot assign " + exprType + " to " + varType);
                }
            }
            else if (s instanceof IfStmt i) {
                Type condType = inferExprType(i.getCondition(), scope);
                if (condType != Type.NUM) {
                    throw new RuntimeException("Condition must be num (boolean)");
                }
                analyzeBlock(i.getThenStmt(), new SymbolTable(scope), expectedReturn);
                if (i.getElseStmt() != null)
                    analyzeBlock(i.getElseStmt(), new SymbolTable(scope), expectedReturn);
            }
            else if (s instanceof WhileStmt w) {
                Type condType = inferExprType(w.getCondition(), scope);
                if (condType != Type.NUM) {
                    throw new RuntimeException("Condition must be num (boolean)");
                }
                analyzeBlock(w.getBody(), new SymbolTable(scope), expectedReturn);
            }
            else if (s instanceof ReturnStmt r) {
                Type exprType = inferExprType(r.getExpr(), scope);
                if (exprType != expectedReturn) {
                    throw new RuntimeException("Return type mismatch: expected " + expectedReturn + " got " + exprType);
                }
            }
        }
    }

    private Type inferExprType(Expr e, SymbolTable scope) {
        if (e instanceof NumLiteral) return Type.NUM;
        if (e instanceof StringLiteral) return Type.STRING;
        if (e instanceof VarExpr v) return scope.lookup(v.name);
        if (e instanceof BinaryOp b) {
            Type left = inferExprType(b.getLeft(), scope);
            Type right = inferExprType(b.getRight(), scope);
            if (!left.equals(right)) {
                throw new RuntimeException("Type error: cannot apply " + b.getOp()+ " to " + left + " and " + right);
            }
            return left;
        }
        if (e instanceof FunctionCallExpr fc) {
            return Type.NUM;
        }
        return Type.NUM;
    }
}
