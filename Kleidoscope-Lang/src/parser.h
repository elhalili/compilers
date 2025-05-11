#ifndef KLD_PARSER_H
#define KLD_PARSER_H
#include "lexer.h"
#include "AST.h"
#include <memory>
#include <map>
#include <iostream>
#include <utility>

/// current_token/get_next_token - Provide a simple token buffer.  CurTok is the current
/// token the parser is looking at.  get_next_token reads another token from the
/// lexer and updates CurTok with its results.

/// Helpers functions
extern int current_token;
int get_next_token() {
    return current_token = get_tok();
}

extern std::map<char, int> bin_op_precedence;
int get_tok_precedence() {
    if (current_token < 0 || current_token > 255) // is an ascii
        return -1;

    int tok_prec = bin_op_precedence[current_token];
    if (tok_prec <= 0)
        return -1;

    return tok_prec;
}

std::unique_ptr<ExprAST> log_error(const std::string& msg) {
    std::perror(msg.c_str());
    return nullptr;
}

std::unique_ptr<PrototypeAST> log_proto_error(const std::string& msg) {
    log_error(msg);
    return nullptr;
}
/// grammar parsing
/// numberexpr ::= number
std::unique_ptr<ExprAST> parse_expr();
std::unique_ptr<ExprAST> parse_num_expr() {
    auto result = std::make_unique<NumberAST>(num_val);
    get_next_token(); 
    return std::move(result);
}

/// parentexpr ::= '(' expression ')'
std::unique_ptr<ExprAST> parse_parent_expr() {
    get_next_token(); // eat '('
    auto expr = parse_expr();
    if (!expr) 
        return nullptr;

    if (current_token != ')') 
        return log_error("Expected )\n");

    get_next_token(); // eat ')'

    return expr;
}

/// identifierexpr
///   ::= identifier
///   ::= identifier '(' expression* ')'
std::unique_ptr<ExprAST> parse_ident_expr() {
    std::string ident_name = ident_str;
    get_next_token();

    if (current_token != '(') 
        return std::make_unique<VariableAST>(ident_name);

    // parse call
    get_next_token();
    std::vector<std::unique_ptr<ExprAST>> args;
    if (current_token != ')') {
        while (current_token != ')') {
            if (auto arg = parse_expr()) 
                args.push_back(std::move(arg));
            else 
                return nullptr;

            if (current_token != ',' ) 
                return log_error("Expected: ,\n");

            get_next_token();
        }
    }

    get_next_token();

    return std::make_unique<CallExprAST>(ident_name, std::move(args));
}

/// primary
///   ::= identifierexpr
///   ::= numberexpr
///   ::= parentexpr
std::unique_ptr<ExprAST> parse_primary() {
    switch (current_token) {
        case tok_identifier:
            return parse_ident_expr();
        case tok_number:
            return parse_num_expr();
        case '(':
            return parse_parent_expr();
        default: 
            return log_error("unknown token when expecting an expression\n");
    }
}

std::unique_ptr<ExprAST> parse_bin_op(std::unique_ptr<ExprAST> lhs, int prec) {
    int current_prec = get_tok_precedence();
    while (current_prec >= prec) {
        auto op = get_next_token();
        auto rhs = parse_primary();
        
        if (!rhs) {
            return nullptr;
        }
        
        int next_op_prec = get_tok_precedence();
        while (next_op_prec >= bin_op_precedence[op]) {
            rhs = parse_bin_op(std::move(rhs), bin_op_precedence[next_op_prec]);
        }

        lhs = std::make_unique<BinaryExpr>(op, std::move(lhs), std::move(rhs));
    }

    return lhs;
}

std::unique_ptr<ExprAST> parse_expr() {
    auto lhs = parse_primary();
    if (!lhs) {
        return nullptr;
    }
    return parse_bin_op(std::move(lhs), 0);
}


/// prototype
///   ::= id '(' id* ')'
std::unique_ptr<PrototypeAST> parse_proto() {
    if (current_token != tok_identifier)
        return log_proto_error("Expected function name in prototype");

    std::string fn_name = ident_str;
    get_next_token();

    if (current_token != '(')
        return log_proto_error("Expected '(' in prototype");

    std::vector<std::string> args_names;
    while (get_next_token() == tok_identifier)
        args_names.push_back(ident_str);
    if (current_token!= ')')
        return log_proto_error("Expected ')' in prototype");

    // success.
    get_next_token();  // eat ')'.

    return std::make_unique<PrototypeAST>(fn_name, args_names);
}


/// definition ::= 'def' prototype expression
std::unique_ptr<FunctionAST> parse_def() {
    get_next_token();  // eat def.
    auto proto = parse_proto();
    if (!proto) return nullptr;

    if (auto e = parse_expr())
        return std::make_unique<FunctionAST>(std::move(proto), std::move(e));

        return nullptr;
}


/// external ::= 'extern' prototype
static std::unique_ptr<PrototypeAST> parse_extern() {
    get_next_token();  // eat extern.
    return parse_proto();
}


/// toplevelexpr ::= expression
static std::unique_ptr<FunctionAST> parse_top_level_expr() {
    if (auto e = parse_expr()) {
        // make an anonymous proto.
        auto proto = std::make_unique<PrototypeAST>("", std::vector<std::string>());
        return std::make_unique<FunctionAST>(std::move(proto), std::move(e));
    }
    return nullptr;
}


void handle_definition() {
  if (parse_def()) {
    fprintf(stderr, "Parsed a function definition.\n");
  } else {
    // Skip token for error recovery.
    get_next_token();
  }
}

void handle_extern() {
  if (parse_extern()) {
    fprintf(stderr, "Parsed an extern\n");
  } else {
    // Skip token for error recovery.
    get_next_token();
  }
}

void handle_top_level_expression() {
  // Evaluate a top-level expression into an anonymous function.
  if (parse_top_level_expr()) {
    fprintf(stderr, "Parsed a top-level expr\n");
  } else {
    // Skip token for error recovery.
    get_next_token();
  }
}


/// top ::= definition | external | expression | ';'
void main_loop() {
  while (true) {
    fprintf(stderr, "ready> ");
    switch (current_token) {
    case tok_eof:
      return;
    case ';': // ignore top-level semicolons.
      get_next_token();
      break;
    case tok_def:
      handle_definition();
      break;
    case tok_extern:
      handle_extern();
      break;
    default:
      handle_top_level_expression();
      break;
    }
  }
}

#endif
