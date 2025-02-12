#ifndef KLD_LEXER_H
#define KLD_LEXER_H
#include <cctype>
#include <cstdio>
#include <cstdlib>
#include <string>

enum Token {
    tok_eof = -1,

    // keywords
    tok_def = -2,
    tok_extern = -3,

    // primary
    tok_id = -4,
    tok_number = -5
};

static std::string ident_str;
static double num_val;

static int get_tok() {
    static int last_char = ' ';

    while (isspace(last_char)) {
        // reading from standard input
        last_char = getchar();

        if (isalpha(last_char)) { // [az-AZ][az-AZ-09]*
            ident_str = last_char;

            while (isalnum(last_char = getchar()))
                ident_str += last_char;

            if (ident_str == "def") {
                return tok_def;
            } else if (ident_str == "extern") {
                return tok_extern;
            } 

            return tok_id;
        }
        
        if (isdigit(last_char) || last_char == '.') {
            std::string num_str;
            do {
                num_str += last_char;
                last_char = getchar();
            } while (isdigit(last_char) || last_char == '.');
        
            num_val = strtod(num_str.c_str(), 0);

            return tok_number;
        }

        if (last_char == '#') {
            do {
                last_char = getchar();

            } while (last_char != EOF || last_char != '\n' || last_char != 'r');
            
            if (last_char != EOF) 
                return get_tok();
        }

        if (last_char == EOF)
            return tok_eof;

        int unrecognized_char = last_char;
        last_char = getchar();

        return unrecognized_char;
    }
}

#endif
