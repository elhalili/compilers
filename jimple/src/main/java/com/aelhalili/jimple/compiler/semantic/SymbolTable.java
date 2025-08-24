package com.aelhalili.jimple.compiler.semantic;


import com.aelhalili.jimple.compiler.ast.Type;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private final Map<String, Type> symbols = new HashMap<>();
    private final SymbolTable parent;

    public SymbolTable(SymbolTable parent) {
        this.parent = parent;
    }

    public void declare(String name, Type type) {
        if (symbols.containsKey(name)) {
            throw new RuntimeException("Variable '" + name + "' already declared in this scope");
        }
        symbols.put(name, type);
    }

    public Type lookup(String name) {
        if (symbols.containsKey(name)) return symbols.get(name);
        if (parent != null) return parent.lookup(name);
        throw new RuntimeException("Undeclared variable: " + name);
    }
}