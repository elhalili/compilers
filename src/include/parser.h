#ifndef KLD_LEXER_H
#define KLD_LEXER_H
#include <algorithm>
#include <memory>
#include <string>
#include <utility>
#include <vector>

class ExpAST {
public: 
    virtual ~ExpAST() = default;
};

class NumExprAST: public ExpAST {
    double val;
public: 
    NumExprAST(double val): val(val) {}
};

class VarExprAST: public ExpAST {
    std::string name;
public:
    VarExprAST(const std::string &name): name(name) {}
};

class BinaryExpAST: public ExpAST {
    char op;
    std::unique_ptr<ExpAST> left_op, right_op;

public:
    BinaryExpAST(char op, std::unique_ptr<ExpAST> lf, std::unique_ptr<ExpAST> rt): op(op), left_op(std::move(lf)), right_op(std::move(rt)) {}
};

class CallExpAST: public ExpAST {
    std::string callee;
    std::vector<std::unique_ptr<ExpAST>> args;

public:
    CallExpAST(const std::string &callee, std::vector<std::unique_ptr<ExpAST>> args): callee(callee), args(std::move(args)) {}
};

class PrototypeAST {
    std::string name;
    std::vector<std::string> args;

public:
    PrototypeAST(const std::string &name, std::vector<std::string> args): name(name), args(std::move(args)) {}
    const std::string& getName() const { return name; } 
};

class FunctionAST {
    std::unique_ptr<PrototypeAST> proto;
    std::unique_ptr<ExpAST> body;

public:
    FunctionAST(std::unique_ptr<PrototypeAST> proto, std::unique_ptr<ExpAST> body): proto(std::move(proto)), body(std::move(body)) {}
};

#endif 
