#include "parser.h"
#include <string>
#include <map>

std::string ident_str; 
double num_val;            
int current_token;

std::map<char, int> bin_op_precedence;

int main(int argc, char *argv[]) {
    bin_op_precedence['+'] = 10;
    bin_op_precedence['-'] = 20;
    bin_op_precedence['/'] = 30;
    bin_op_precedence['*'] = 40;

    // prime the first token.
    fprintf(stderr, "ready> ");
    get_next_token();

    // run the main "interpreter loop" now.
    main_loop();

    return 0;
}
