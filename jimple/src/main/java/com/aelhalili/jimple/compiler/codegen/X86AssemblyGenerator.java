package com.aelhalili.jimple.compiler.codegen;

import com.aelhalili.jimple.compiler.ast.*;
import java.util.*;

public class X86AssemblyGenerator {

    private final StringBuilder text = new StringBuilder();
    private final StringBuilder rodata = new StringBuilder();
    private final Map<String, FuncInfo> functions = new HashMap<>();
    private final Map<String, String> stringPool = new LinkedHashMap<>();
    private int strCounter = 0;
    private int labelCounter = 0;

    public String generate(Prog program) {
        text.setLength(0);
        rodata.setLength(0);
        functions.clear();
        stringPool.clear();
        strCounter = 0;
        labelCounter = 0;

        for (Node n : program.getStmts()) {
            if (n instanceof FunctionDecl f) {
                functions.put(f.getName(), new FuncInfo(f));
            }
        }

        emitHeader();

        for (Node n : program.getStmts()) {
            if (n instanceof FunctionDecl f) {
                emitFunction(f);
            }
        }

        emitEntryPoint();

        emitRodata();

        return text.toString() + rodata.toString();
    }

    private void emitHeader() {
        emit("global _start");
        for (var fn : functions.keySet()) emit("global " + mangle(fn));
        emit("");
        emit("section .text");
        emit("");
    }

    private void emitRodata() {
        if (stringPool.isEmpty()) return;
        rodata.append("\nsection .rodata\n");
        for (var e : stringPool.entrySet()) {
            rodata.append(e.getValue()).append(":\n");
            rodata.append("    db ").append(toDbString(e.getKey())).append(", 0\n");
        }
    }

    private void emitEntryPoint() {
        // _start:
        //   call main
        //   mov rdi, rax        ; exit code = main's return (clamped to 0..255 by kernel)
        //   mov rax, 60         ; sys_exit
        //   syscall
        emit("_start:");
        emit("    call " + mangle("main"));
        emit("    mov rdi, rax");
        emit("    mov rax, 60");
        emit("    syscall");
        emit("");
    }

    // --- Function codegen ---
    private void emitFunction(FunctionDecl f) {
        var finfo = functions.get(f.getName());
        finfo.beginFrame();

        emit(mangle(f.getName()) + ":");
        // Prologue
        emit("    push rbp");
        emit("    mov rbp, rsp");
        if (finfo.frameSize > 0) emit("    sub rsp, " + finfo.frameSize);

        // Map parameters (System V: rdi, rsi, rdx, rcx, r8, r9)
        String[] regs = {"rdi", "rsi", "rdx", "rcx", "r8", "r9"};
        int nParams = Math.min(f.getParams().size(), regs.length);
        for (int i = 0; i < nParams; i++) {
            var p = f.getParams().get(i);
            int off = finfo.allocLocal(p.name()); // assign local slot for param
            emit("    mov [rbp-" + off + "], " + regs[i]);
        }

        // Body
        for (Stmt s : f.getBody()) genStmt(s, finfo);

        // If control falls off end and function expects num, return 0
        emit("    mov rax, 0");
        // Epilogue
        if (finfo.frameSize > 0) emit("    add rsp, " + finfo.frameSize);
        emit("    pop rbp");
        emit("");
    }

    // --- Statements ---
    private void genStmt(Stmt s, FuncInfo fn) {
        if (s instanceof VarDecl v) {
            int off = fn.allocLocal(v.getName());
            if (v.getInit() != null) {
                genExpr(v.getInit(), fn);            // rax = value
                emit("    mov [rbp-" + off + "], rax");
            } else {
                emit("    mov qword [rbp-" + off + "], 0");
            }
            return;
        }
        if (s instanceof Assignment a) {
            int off = fn.lookupLocal(a.getVarName());
            genExpr(a.getExpr(), fn);                // rax = value
            emit("    mov [rbp-" + off + "], rax");
            return;
        }
        if (s instanceof IfStmt i) {
            String L_else = newLabel("else");
            String L_end  = newLabel("endif");
            genExpr(i.getCondition(), fn);            // rax = 0/1
            emit("    cmp rax, 0");
            emit(i.getElseStmt() != null ? "    je " + L_else : "    je " + L_end);
            // then
            for (Stmt t : i.getThenStmt()) genStmt(t, fn);
            emit("    jmp " + L_end);
            if (i.getElseStmt() != null) {
                emit(L_else + ":");
                for (Stmt e : i.getThenStmt()) genStmt(e, fn);
            }
            emit(L_end + ":");
            return;
        }
        if (s instanceof WhileStmt w) {
            String L_cond = newLabel("while_cond");
            String L_body = newLabel("while_body");
            String L_end  = newLabel("while_end");
            emit(L_cond + ":");
            genExpr(w.getCondition(), fn);
            emit("    cmp rax, 0");
            emit("    je " + L_end);
            emit(L_body + ":");
            for (Stmt b : w.getBody()) genStmt(b, fn);
            emit("    jmp " + L_cond);
            emit(L_end + ":");
            return;
        }
        if (s instanceof ReturnStmt r) {
            genExpr(r.getExpr(), fn); // rax holds return
            // epilogue
            if (fn.frameSize > 0) emit("    add rsp, " + fn.frameSize);
            emit("    pop rbp");
            emit("    ret");
            return;
        }

        if (s == null) {
            throw new RuntimeException("Null statement");
        }

        throw new RuntimeException("Unhandled statement: " + s.getClass());
    }

    // --- Expressions (result in RAX) ---
    private void genExpr(Expr e, FuncInfo fn) {
        if (e instanceof NumLiteral n) {
            emit("    mov rax, " + (long) n.getValue());
            return;
        }
        if (e instanceof StringLiteral s) {
            String label = internString(s.getValue());
            emit("    lea rax, [" + label + "]");
            return;
        }
        if (e instanceof VarExpr v) {
            int off = fn.lookupLocal(v.name);
            emit("    mov rax, [rbp-" + off + "]");
            return;
        }
        if (e instanceof BinaryOp b) {
            genExpr(b.getLeft(), fn);        // rax = left
            emit("    push rax");
            genExpr(b.getRight(), fn);       // rax = right
            emit("    mov rbx, rax");   // rbx = right
            emit("    pop rax");        // rax = left

            switch (b.getOp()) {
                case "+": emit("    add rax, rbx"); break;
                case "-": emit("    sub rax, rbx"); break;
                case "*": emit("    imul rax, rbx"); break;
                case "/":
                    emit("    cqo");        // sign-extend rax into rdx:rax
                    emit("    idiv rbx");   // rax = rax / rbx
                    break;
                case "<":  cmpSet("l",  false); break;
                case "<=": cmpSet("le", false); break;
                case ">":  cmpSet("g",  false); break;
                case ">=": cmpSet("ge", false); break;
                case "==": cmpSet("e",  false); break;
                case "!=": cmpSet("ne", false); break;
                default:
                    throw new RuntimeException("Unsupported op: " + b.getOp());
            }
            return;
        }
        if (e instanceof FunctionCallExpr fc) {
            genCall(fc, fn);
            return;
        }
        if (e instanceof ParentExpr) {
            genExpr(((ParentExpr) e).getInner(), fn);
            return;
        }
        throw new RuntimeException("Unhandled expr: " + e.getClass());
    }

    private void genCall(FunctionCallExpr fc, FuncInfo fn) {
        // Evaluate args left->right, push each result
        for (Expr arg : fc.getArguments()) {
            genExpr(arg, fn);
            emit("    push rax");
        }
        // Pop into registers in reverse so arg0 -> rdi, arg1 -> rsi, ...
        String[] regs = {"r9","r8","rcx","rdx","rsi","rdi"}; // reverse popping
        int count = Math.min(fc.getArguments().size(), 6);
        for (int i = 0; i < count; i++) {
            emit("    pop " + regs[i]);
        }
        // Call
        emit("    call " + mangle(fc.getFunctionName()));
        // Caller cleanup for >6 args not supported in this minimal version
    }

    // Comparison result: rax <- 0/1.
    private void cmpSet(String cc, boolean unsigned) {
        // rax = left, rbx = right
        emit("    cmp rax, rbx");
        emit("    mov rax, 0");
        emit("    mov rcx, 1");
        // setcc works on byte regs; we’ll do a branchless trick with cmov
        // but simplest: use setcc + movzx
        // (Kept here as two-step to avoid extra constraints)
        // We'll just do the classic setcc to al:
        // NOTE: we can overwrite previous sequence:
        text.setLength(text.length()); // no-op, keep prior
        emit("    sete al"); // default changed by cc
        // Replace sete with the chosen cc:
        int idx = text.lastIndexOf("sete al");
        text.replace(idx, idx + "sete al".length(), "set" + cc + " al");
        emit("    movzx rax, al");
    }

    private String mangle(String name) { return name; }
    private String newLabel(String stem) { return stem + "_" + (labelCounter++); }
    private void emit(String line) { text.append(line).append("\n"); }
    private String internString(String value) {
        return stringPool.computeIfAbsent(value, v -> "str_" + (strCounter++));
    }

    private static String toDbString(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            switch (c) {
                case '\n': sb.append("10,"); break;
                case '\t': sb.append("9,"); break;
                case '"':  sb.append("34,"); break;
                case '\\': sb.append("92,"); break;
                default:
                    if (c >= 32 && c < 127) sb.append("'").append(c).append("',");
                    else sb.append((int)c).append(",");
            }
        }
        if (sb.length() > 0) sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    // minimal frame/scope management for locals
    private static class FuncInfo {
        final FunctionDecl decl;
        final Map<String, Integer> locals = new LinkedHashMap<>();
        int nextOffset = 0;   // grows by 8 per local
        int frameSize = 0;

        FuncInfo(FunctionDecl decl) { this.decl = decl; }

        void beginFrame() {
            // TODO
        }

        int allocLocal(String name) {
            Integer off = locals.get(name);
            if (off != null) return off;
            nextOffset += 8;
            locals.put(name, nextOffset);
            frameSize = Math.max(frameSize, nextOffset);
            return nextOffset;
        }

        int lookupLocal(String name) {
            Integer off = locals.get(name);
            if (off == null) throw new RuntimeException("Unknown local: " + name);
            return off;
        }
    }
}
