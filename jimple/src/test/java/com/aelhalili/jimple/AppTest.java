package com.aelhalili.jimple;

import com.aelhalili.jimple.compiler.ast.Prog;
import com.aelhalili.jimple.compiler.codegen.X86AssemblyGenerator;
import com.aelhalili.jimple.compiler.parser.AstBuilder;
import com.aelhalili.jimple.compiler.semantic.SemanticAnalyzer;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.Test;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class AppTest
{

    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }
}
