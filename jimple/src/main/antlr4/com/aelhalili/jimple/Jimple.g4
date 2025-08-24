grammar Jimple;

// parser rules
program
    : (functionDecl | statement)* EOF
    ;

functionDecl
    : 'fn' ID '(' paramList? ')' ':' type block
    ;

paramList
    : param (',' param)*
    ;

param
    : ID ':' type
    ;

type
    : 'num'
    | 'string'
    ;

block
    : '{' statement* '}'
    ;

statement
    : varDecl ';'
    | assignment ';'
    | expr ';'
    | returnStmt ';'
    | ifStmt
    | whileStmt
    ;

varDecl
    : 'let' ID ':' type ('=' expr)?
    ;

assignment
    : ID '=' expr
    ;

returnStmt
    : 'return' expr
    ;

ifStmt
    : 'if' '(' expr ')' block ('else' block)?
    ;

whileStmt
    : 'while' '(' expr ')' block
    ;

expr
    : literal # LiteralExpr
    | ID            # IDExpr
    | functionCall          # Callee
    | expr op=('*'|'/') expr # MuliDivOp
    | expr op=('+'|'-') expr # AddMinusOp
    | expr op=('<' | '<=' | '>' | '>=' | '==' | '!=') expr # BooleanOp
    | '(' expr ')' # ParentExpr
    ;

functionCall
    : ID '(' argList? ')'
    ;

argList
    : expr (',' expr)*
    ;

literal
    : NUMBER
    | STRING
    ;

// lexer rules
ID      : [a-zA-Z_][a-zA-Z0-9_]* ;
NUMBER  : [0-9]+ ('.' [0-9]+)? ;
STRING  : '"' (~["\\] | '\\' .)* '"' ;
WS      : [ \t\r\n]+ -> skip ;
COMMENT : '//' ~[\r\n]* -> skip ;
