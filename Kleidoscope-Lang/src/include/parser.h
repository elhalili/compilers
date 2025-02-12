#ifndef KLD_PARSER_H
#define KLD_PARSER_H
#include "./lexer.h"
#include "./AST.h"
#include <algorithm>
#include <cstdio>
#include <memory>

// the current token
static int current_token;
static int get_next_token() {
    return current_token = get_tok();
}

// Error handling
std::unique_ptr<ExpAST> log_error(const char* str) {
    fprintf(stderr, "Error: %s\n", str);
    return nullptr;
}
std::unique_ptr<ExpAST> log_error_proto(const char* str) {
    log_error(str);
    return nullptr;
}

// number_expr ::= number
static std::unique_ptr<ExpAST> parse_num_expr() {
    auto res = std::make_unique<NumExprAST>(num_val);
    get_next_token();

    return std::move(res);
}

// parent_expr ::= '(' expression ')'
static unique_ptr<ExpAST> parse_parent_exp() {
    get_next_token(); // eat '('
    auto v = parse_expr();
    if (!v) {
        return nullptr;
    }

    if (current_token != ')') {
        return log_error("Expected ')'");
    }

    get_next_token(); // eat ')'
    return v;
}




#endif
