package com.aelhalili.jimple;

import com.aelhalili.jimple.compiler.ast.Prog;
import com.aelhalili.jimple.compiler.codegen.X86AssemblyGenerator;
import com.aelhalili.jimple.compiler.parser.AstBuilder;
import com.aelhalili.jimple.compiler.semantic.SemanticAnalyzer;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.io.FileInputStream;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Scanner;

public class App
{
    public static void main( String[] args ) throws Exception
    {
        if (args.length < 1) {
            System.err.println("Usage: java Main <source-file>");
            return;
        }


        FileInputStream stream = new FileInputStream(args[0]);

        JimpleLexer lexer = new JimpleLexer(CharStreams.fromStream(stream));
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        JimpleParser parser = new JimpleParser(tokens);
        ParseTree antlrAST = parser.program();

        AstBuilder astBuilder = new AstBuilder();
        Prog prog = (Prog) astBuilder.visit(antlrAST);

        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
        semanticAnalyzer.analyze(prog);


        X86AssemblyGenerator x86CodeGen = new X86AssemblyGenerator();
        String x86Code = x86CodeGen.generate(prog);

        System.out.println(x86Code);
    }
}
