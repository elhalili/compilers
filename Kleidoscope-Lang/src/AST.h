#ifndef KLD_AST_H
#define KLD_AST_H

#include <string>
#include <memory>
#include <vector>

// Base node
class ExprAST {
public:
    virtual ~ExprAST() = default; 
};

class NumberAST: public ExprAST {
    double _val;
public:
    NumberAST(double val): _val(val) {}
};

class VariableAST: public ExprAST {
    std::string _name;
public:
    VariableAST(const std::string& name): _name(name) {}
};

class BinaryExpr: public ExprAST {
    char _op;
    std::unique_ptr<ExprAST> _lhs, _rhs;

public:
    BinaryExpr(char op, std::unique_ptr<ExprAST> lhs, std::unique_ptr<ExprAST> rhs): _op(op), _lhs(std::move(lhs)), _rhs(std::move(rhs)) {}
};

class CallExprAST: public ExprAST {
    std::string _callee;
    std::vector<std::unique_ptr<ExprAST>> _args;

public:
    CallExprAST(const std::string& callee, std::vector<std::unique_ptr<ExprAST>> args): _callee(callee), _args(std::move(args)) {}
};

class PrototypeAST {
    std::string _name;
    std::vector<std::string> _args;
public:
    PrototypeAST(const std::string& name, const std::vector<std::string>& args): _name(name), _args(args) {}
    const std::string& get_name() const {
        return _name;
    }
};

class FunctionAST {
    std::unique_ptr<PrototypeAST> _proto;
    std::unique_ptr<ExprAST> _body;
public:
    FunctionAST(std::unique_ptr<PrototypeAST> proto, std::unique_ptr<ExprAST> body): _proto(std::move(proto)), _body(std::move(body)) {}
};
#endif 
