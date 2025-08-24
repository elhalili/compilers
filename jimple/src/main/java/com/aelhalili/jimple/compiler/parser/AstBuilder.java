package com.aelhalili.jimple.compiler.parser;

import com.aelhalili.jimple.JimpleBaseVisitor;
import com.aelhalili.jimple.JimpleParser;
import com.aelhalili.jimple.compiler.ast.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AstBuilder extends JimpleBaseVisitor<Node> {

    @Override
    public Node visitProgram(JimpleParser.ProgramContext ctx) {
        List<Node> items = new ArrayList<>();
        for (var child : ctx.children) {
            Node node = child.accept(this);
            if (node != null) items.add(node);
        }
        return new Prog(items);
    }

    @Override
    public Node visitVarDecl(JimpleParser.VarDeclContext ctx) {
        String name = ctx.ID().getText();
        Type type = "num".equals(ctx.type().getText())? Type.NUM: Type.STRING;
        Expr init = null;
        if (ctx.expr() != null) {
            init = (Expr) visit(ctx.expr());
        }
        return new VarDecl(name, type, init);
    }

    @Override
    public Node visitAssignment(JimpleParser.AssignmentContext ctx) {
        String name = ctx.ID().getText();
        Expr value = (Expr) visit(ctx.expr());
        return new Assignment(name, value);
    }

    @Override
    public Node visitIfStmt(JimpleParser.IfStmtContext ctx) {
        Expr condition = (Expr) visit(ctx.expr());
        List<Stmt> thenBlock = buildBlock(ctx.block(0));
        List<Stmt> elseBlock = ctx.block().size() > 1 ? buildBlock(ctx.block(1)) : null;
        return new IfStmt(condition, thenBlock, elseBlock);
    }

    @Override
    public Node visitWhileStmt(JimpleParser.WhileStmtContext ctx) {
        Expr condition = (Expr) visit(ctx.expr());
        List<Stmt> body = buildBlock(ctx.block());
        return new WhileStmt(condition, body);
    }

    @Override
    public Node visitReturnStmt(JimpleParser.ReturnStmtContext ctx) {
        return new ReturnStmt((Expr) visit(ctx.expr()));
    }

    @Override
    public Node visitLiteralExpr(JimpleParser.LiteralExprContext ctx) {
        if (ctx.literal().NUMBER() != null) {
            return new NumLiteral(Double.parseDouble(ctx.literal().NUMBER().getText()));
        }
        if (ctx.literal().STRING() != null) {
            String raw = ctx.literal().STRING().getText();
            return new StringLiteral(raw.substring(1, raw.length()-1));
        }

        return null;
    }

    @Override
    public Node visitIDExpr(JimpleParser.IDExprContext ctx) {
        return new VarExpr(ctx.getText());
    }

    @Override
    public Node visitCallee(JimpleParser.CalleeContext ctx) {
        String name = ctx.functionCall().ID().getText();
        List<Expr> args = new ArrayList<>();
        if (ctx.functionCall().argList() != null) {
            for (var e : ctx.functionCall().argList().expr()) {
                args.add((Expr) visit(e));
            }
        }
        return new FunctionCallExpr(name, args);
    }

    @Override
    public Node visitAddMinusOp(JimpleParser.AddMinusOpContext ctx) {
        Expr left =  (Expr) visit(ctx.expr(0));
        Expr right = (Expr) visit(ctx.expr(1));
        String op = ctx.op.getText();

        return new BinaryOp(left,right,op);
    }

    @Override
    public Node visitMuliDivOp(JimpleParser.MuliDivOpContext ctx) {
        Expr left =  (Expr) visit(ctx.expr(0));
        Expr right = (Expr) visit(ctx.expr(1));
        String op = ctx.op.getText();

        return new BinaryOp(left,right, op);
    }

    @Override
    public Node visitBooleanOp(JimpleParser.BooleanOpContext ctx) {
        Expr left =  (Expr) visit(ctx.expr(0));
        Expr right = (Expr) visit(ctx.expr(1));
        String op = ctx.op.getText();

        return new BinaryOp(left,right, op);
    }

    @Override
    public Node visitFunctionDecl(JimpleParser.FunctionDeclContext ctx) {
        String name = ctx.ID().getText();
        Map<String, Type> params = new HashMap<>();
        Type returnType = "num".equals(ctx.type().getText())? Type.NUM: Type.STRING;

        if (ctx.paramList() != null) {
            for (var param: ctx.paramList().param()) {
                Type type = "num".equals(param.type().getText())? Type.NUM: Type.STRING;
                params.put(name, type);
            }
        }

        List<Stmt> body = buildBlock(ctx.block());

        return new FunctionDecl(name, params, returnType, body);
    }

    @Override
    public Node visitParentExpr(JimpleParser.ParentExprContext ctx) {
        Expr expr = (Expr) visit(ctx.expr());
        return new ParentExpr(expr);
    }

    @Override
    public Node visitStatement(JimpleParser.StatementContext ctx) {
        if (ctx.assignment() != null) {
            return visitAssignment(ctx.assignment());
        }

        if (ctx.varDecl() != null) {
            return visitVarDecl(ctx.varDecl());
        }

        if (ctx.ifStmt() != null) {
            return visitIfStmt(ctx.ifStmt());
        }

        if (ctx.whileStmt() != null) {
            return visitWhileStmt(ctx.whileStmt());
        }

        if (ctx.returnStmt() != null) {
            return visitReturnStmt(ctx.returnStmt());
        }

        return visit(ctx.expr());
    }

    private List<Stmt> buildBlock(JimpleParser.BlockContext ctx) {
        List<Stmt> stmts = new ArrayList<>();
        for (var s : ctx.statement()) {
            Stmt stmt = (Stmt) visitStatement(s);
            stmts.add(stmt);
        }
        return stmts;
    }
}
