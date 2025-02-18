grammar Jimple;

@header {
package com.aelhalili.jimple;
}

/*
    Jimple programming language grammar
*/


program:                statement* EOF;

statement:              condition
                        | loop
                        | function_dec
                        | variable_dec
                        | RETURN expr;


condition:              IF expr COLON statement*
                        (ELSE_IF expr COLON statement*)*
                        (ELSE statement*)?
                        ENDIF;


loop:                   LOOP expr COLON statement* ENDLOOP;

function_dec:           FN IDENT LP fn_args RP COLON
                            statement*
                        ENDFUNC;

fn_args:                IDENT (COMMA IDENT)*
                        | ;

variable_dec:           LET IDENT '=' expr SEMI;

expr:                    expr AND expr   
                        | expr OR expr   
                        | expr '==' expr
                        | expr '!=' expr 
                        | expr '>' expr  
                        | expr '<' expr  
                        | expr '>=' expr 
                        | expr '<=' expr 
                        | expr '+' expr 
                        | expr '-' expr 
                        | expr '*' expr 
                        | expr '/' expr 
                        | '-' expr       
                        | LP expr RP     
                        | func_call      
                        | IDENT          
                        | NUM           
                        ;


func_call:              IDENT LP args RP;
args:                   (expr COMMA)* expr | ;



/*
    Tokens
*/
IDENT:              [a-zA-Z_]+[a-zA-Z0-9_]*;

INT:                '0' | [\-]{0,1}[1-9]+;
DOUBLE:             INT[.][0-9]+;
NUM:                (INT | DOUBLE);

FN:                 'fn';
ENDFUNC:            'endfn';
RETURN:             'return';

LET:                'let';

IF:                 'if';
ELSE_IF:            'else if';
ELSE:               'else';
ENDIF:              'endif';

LOOP:               'loop';
ENDLOOP:            'endloop';

AND:                'and';
OR:                 'or';

SEMI:               ';';
COLON:              ':';
COMMA:              ',';
LP:                 '(';
RP:                 ')';
LCURLY:             '{';
RCURLY:             '}';


WS:                 [ \n\t] -> skip;