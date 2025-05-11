#ifndef KLD_LEXER_H
#define KLD_LEXER_H

#include <cstdio>
#include <string>

extern std::string ident_str; 
extern double num_val;            


// The lexer returns tokens [0-255] if it is an unknown character, otherwise one
// of these for known things.
enum Token {
  tok_eof = -1,

  // commands
  tok_def = -2,
  tok_extern = -3,

  // primary
  tok_identifier = -4,
  tok_number = -5,
};


int get_tok() {
    static int last_char = ' '; 

    // Skip whitespaces
    while(std::isspace(last_char)) 
        last_char = getchar();

    // identifier
    if (std::isalpha(last_char)) {
        ident_str += last_char;
        while (std::isalnum(last_char = getchar()))
            ident_str += last_char;

        if (ident_str == "def")
            return tok_def;
        if (ident_str == "extern")
            return tok_extern;
        return tok_identifier;
    }

    // numbers
    if (std::isdigit(last_char) || last_char == '.') {
        std::string num_str;

        do {
            num_str += last_char;
            last_char = getchar();
        } while (std::isdigit(last_char) || last_char == '.');

        num_val = std::stod(num_str);
        return tok_number;
    }

    // Ignore comments
    if (last_char == '#') {
        do
        last_char = getchar();
        while (last_char != EOF && last_char != '\n' && last_char != '\r');
    }

    // reach end of file
    if (last_char == EOF) 
        return tok_eof;

    // Otherwise return ascii value
    char this_char = last_char;
    last_char = getchar();

    return this_char;
}

#endif
