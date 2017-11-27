%{
    #include <string.h>
    #include <stdio.h>
    #include <stdlib.h>

    extern int yylex();
    extern int yyparse();
    extern FILE *yyin;
    extern int line_number;
    extern void init();
    extern void print_PIF();
    extern void print_ST();
    void yyerror(const char *s);
%}

%token START
%token END
%token DECLVAR
%token DECLCONST
%token DECLTYPE
%token INTEGER
%token REAL
%token ASSIG
%token PLUS
%token MINUS
%token TIMES
%token DIV
%token MOD
%token POW
%token EQ
%token NEQ
%token GTR
%token LSS
%token GEQ
%token LEQ
%token WHILESYM
%token IFSYM
%token DOSYM
%token WRITEOP
%token READOP
%token ID
%token INTEGERVAL
%token REALVAL
%token UNKNOWN

%%
program:    stmt_body | decl_body | stmt_body program | decl_body program

stmts:      stmt | stmt stmts
stmt_body:  START stmt END
stmt:       assig | if | while | read | write
assig:      ASSIG ID expr | EQ ID const
expr:       START op params END
op:         PLUS | MINUS | TIMES | DIV | MOD | POW | EQ | GTR | LSS | GEQ | LEQ | NEQ
params:     param | param params
param:      ID | const | expr
const:      INTEGERVAL | REALVAL
if:         IFSYM expr stmt | IFSYM expr stmt stmt | IFSYM expr block | IFSYM expr block block | IFSYM expr block stmt | IFSYM expr stmt block
block:      DOSYM stmts
while:      WHILESYM expr stmt | WHILESYM expr block
read:       READOP ID | READOP
write:      WRITEOP ID | WRITEOP const | WRITEOP expr

decls:      decl | decl decls
decl_body:  START decl END
decl:       declv | declc | declt
declv:      DECLVAR ID type param | DECLVAR ID type  
declc:      DECLCONST ID type param      
declt:      DECLTYPE ID decls
type:       INTEGER | REAL
%%

int main(int argc, char *argv[]) {
    
    --argc;
    ++argv;

    if (argc > 0) {
        yyin = fopen(argv[0], "r");
    }
    else {
        yyin = stdin;
    }
    
    init();
    while (!feof(yyin)) {
        yyparse();
    }
    print_PIF();
    print_ST();
    
    printf("The file is sintactly correct!\n");
    
    return 0;
}

void yyerror(const char *s) {
    
    printf("Error: %s at line -> %d ! \n", s, line_number);
    exit(-1);
}