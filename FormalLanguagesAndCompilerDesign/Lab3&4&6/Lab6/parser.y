%union {
    int intval;
    char* id;
}

%{
    #include <stdio.h>
    #include <string.h>
    #include <stdlib.h>

    extern int yylex();
    extern int yyparse();
    extern FILE *yyin;
    extern int line_number;
    void yyerror(const char *s);

    char decls[500];
    char code[5000];

    int is_number(char* str) {

        for (int i = 0; i < strlen(str); ++i) {
            if (!(str[i] >= '0' && str[i] <= '9')) {
                return 0;
            }
        }

        return 1;
    }

    void strrev(char* str) {

        char c;
        int len = strlen(str);

        for (int i = 0; i < len / 2; ++i) {
            c = str[i];
            str[i] = str[len - i - 1];
            str[len - i - 1] = c; 
        }
    }

    char* itoa(int n) {

        char *num;
        int len, x;

        len = 0;
        x = n;
        while (x != 0) {
            x /= 10;
            ++len;
        }

        num = (char *) malloc(sizeof(char) * len);
        len = 0;
        x = n;
        while (x != 0) {
            num[len++] = x % 10 + '0';
            x /= 10;
        }
        num[len] = '\0';
        strrev(num);

        return num;
    }

    void declare(char* id) {

        char decl[50];

        sprintf(decl, "\t%s\tDW\t0\n", id);
        strcat(decls, decl);
    }

    char* compute(char *expr) {

        char *l, *op, *r;
        char *token;

        token = strtok(expr, " ");
        l = (char *) malloc(sizeof(char) * strlen(token) + 1);
        strcpy(l, token);
        
        token = strtok(NULL, " ");
        op = (char *) malloc(sizeof(char) * strlen(token) + 1);
        strcpy(op, token);

        token = strtok(NULL, " ");
        r = (char *) malloc(sizeof(char) * strlen(token) + 1);
        strcpy(r, token);

        char *expression = (char *) malloc(sizeof(char) * 500);
        char snippet[100] = "\0";

        strcat(expression, "PUSH AX\n");
        if (is_number(l)) {
            sprintf(snippet, "MOV AX, %s\n", l);
        }
        else {
            sprintf(snippet, "MOV AX, [%s]\n", l);
        }
        strcat(expression, snippet);

        if (strcmp(op, "+") == 0) {
            if (is_number(r)) {
                sprintf(snippet, "ADD AX, %s\n", r);
            }
            else {
                sprintf(snippet, "ADD AX, [%s]\n", r);
            }
            strcat(expression, snippet);
            strcat(expression, "MOV BX, AX\n");
        }
        else if (strcmp(op, "-") == 0) {
            if (is_number(r)) {
                sprintf(snippet, "SUB AX, %s\n", r);
            }
            else {
                sprintf(snippet, "SUB AX, [%s]\n", r);
            }
            strcat(expression, snippet);
            strcat(expression, "MOV BX, AX\n");
        }
        else if (strcmp(op, "*") == 0) {
            if (is_number(r)) {
                sprintf(snippet, "IMUL AX, %s\n", r);
            }
            else {
                sprintf(snippet, "IMUL AX, [%s]\n", r);
            }
            strcat(expression, snippet);
            strcat(expression, "MOV BX, AX\n");
        }
        else if (strcmp(op, "/") == 0) {
            strcat(expression, "XOR AL, AL\n");
            strcat(expression, "XOR AH, AH\n");
            strcat(expression, "XOR EDX, EDX\n");
            if (is_number(r)) {
                sprintf(snippet, "MOV BX, %s\n", r);
            }
            else {
                sprintf(snippet, "MOV BX, [%s]\n", r);
            }
            strcat(expression, snippet);
            strcat(expression, "IDIV BX\n");
            strcat(expression, "SHL EAX, 16\n");
            strcat(expression, "MOV BX, AX\n");
        }
        else if (strcmp(op, "%") == 0) {
            strcat(expression, "XOR AL, AL\n");
            strcat(expression, "XOR AH, AH\n");
            strcat(expression, "XOR EDX, EDX\n");
            if (is_number(r)) {
                sprintf(snippet, "MOV BX, %s\n", r);
            }
            else {
                sprintf(snippet, "MOV BX, [%s]\n", r);
            }
            strcat(expression, snippet);
            strcat(expression, "IDIV BX\n");
            strcat(expression, "SHL EDX, 16\n");
            strcat(expression, "MOV AX, DX\n");
            strcat(expression, "MOV BX, AX\n");
        }

        strcat(expression, "POP AX\n");
        return expression;
    }

    char* add_assig(char* id, char* expr) {

        char *expression = compute(expr);
        char *assig = (char *) malloc(sizeof(char) * 100);

        sprintf(assig, "%s\nPUSH AX\nMOV AX, [%s]\nMOV AX, BX\nMOV [%s], AX\nPOP AX\n", expression, id, id);
        return assig;
    }

    char* add_assig_const(char* id, int val) {

        char *assig = (char *) malloc(sizeof(char) * 200);
        
        sprintf(assig, "MOV [%s], word %d\n", id, val);
        return assig;
    }

    char* append_three(char *l, char *op, char *r) {

        char* new_str = (char *) malloc(sizeof(char) * (strlen(l) + strlen(op) + strlen(r) + 10));
        sprintf(new_str, "%s %s %s", l, op, r);

        return new_str;
    }

    char* append_two(char *l, char *r) {

        char* new_str = (char *) malloc(sizeof(char) * (strlen(l) + strlen(r) + 10));
        sprintf(new_str, "%s%s", l, r);

        return new_str;
    }

    char* add_conditional(char *cond, char *stmts) {

        char *l, *op, *r;
        char *token;

        token = strtok(cond, " ");
        l = (char *) malloc(sizeof(char) * strlen(token) + 1);
        strcpy(l, token);
        
        token = strtok(NULL, " ");
        op = (char *) malloc(sizeof(char) * strlen(token) + 1);
        strcpy(op, token);

        token = strtok(NULL, " ");
        r = (char *) malloc(sizeof(char) * strlen(token) + 1);
        strcpy(r, token);   

        char *code = (char *) malloc(sizeof(char) * 1000);
        if (is_number(l)) {
            sprintf(code, "MOV CX, %s\n", l);
        }
        else {
            sprintf(code, "MOV CX, [%s]\n", l);
        }
        if (is_number(r)) {
            sprintf(code + strlen(code), "MOV DX, %s\nCMP CX, DX\n", r);
        }
        else {
            sprintf(code + strlen(code), "MOV DX, [%s]\nCMP CX, DX\n", r);            
        }

        if (strcmp(op, "==") == 0) {
            strcat(code, "JNE DONE\n");
        }   
        else if (strcmp(op, "!=") == 0) {
            strcat(code, "JEQ DONE\n");
        }  
        else if (strcmp(op, ">") == 0) {
            strcat(code, "JL DONE\n");
        } 
        else if (strcmp(op, "<") == 0) {
            strcat(code, "JG DONE\n");
        } 
        else if (strcmp(op, ">=") == 0) {
            strcat(code, "JLE DONE\n");
        } 
        else if (strcmp(op, "<=") == 0) {
            strcat(code, "JGE DONE\n");
        } 

        strcat(code, stmts);
        strcat(code, "DONE:\n");

        return code;
    }

    char* add_read(char *id) {

        char *read_op = (char *) malloc(1000);

        sprintf(read_op, "LEA EDI, [rfmt]\nLEA ESI, [%s]\nXOR EAX, EAX\nCALL scanf\n", id);

        return read_op;
    }

    char* add_write(char *data) {

        char *write_op = (char *) malloc(1000);

        sprintf(write_op, "LEA EDI, [wfmt]\n");
        if (is_number(data)) {
            sprintf(write_op + strlen(write_op), "MOV ESI, dword %s\n", data);
        }
        else {
            sprintf(write_op + strlen(write_op), "MOV ESI, [%s]\n", data);
        }

        sprintf(write_op + strlen(write_op), "XOR EAX, EAX\nCALL printf\n");

        return write_op;
    }

    void build_program(char* program) {

        char assembly[10000];
        sprintf(assembly, "section .text\nglobal main\nextern scanf\nextern printf\nmain:\n%s\nMOV EAX, 1\nINT 0x80\n\nsection .data\nrfmt\tDB\t\"%%hd\",0\nwfmt\tDB\t\"%%hd\\n\",0x0a,0\n%s", program, decls);

        FILE *fp;
        fp = fopen("Lab6/prog.asm", "w");
        fputs(assembly, fp);
        fclose(fp);
    }
%}

%token START
%token END
%token DECLVAR
%token AS
%token INTEGER
%token ASSIG
%token PLUS
%token MINUS
%token TIMES
%token DIV
%token MOD
%token EQ
%token NEQ
%token GTR
%token LSS
%token GEQ
%token LEQ
%token IFSYM
%token WRITEOP
%token READOP
%token <id> ID
%token <intval> INTEGERVAL
%token UNKNOWN

%type<intval> const
%type<id> param
%type<id> op
%type<id> compop
%type<id> expr
%type<id> assig
%type<id> comp
%type<id> stmt
%type<id> stmts
%type<id> if
%type<id> program
%type<id> decl 
%type<id> read
%type<id> write

%%
all:        program { build_program($program); }
program:    stmt { $$ = $stmt; } | decl { $$ = ""; } | stmt program { $$ = append_two($1, $2); } | decl program { $$ = append_two($1, $2); }

stmts:      stmt { $$ = $stmt; } | stmt stmts { $$ = append_two($1, $2); }
stmt:       assig { $$ = $assig; } | if { $$ = $if; } | read { $$ = $read; } | write { $$ = $write; }
assig:      ID ASSIG expr { $$ = add_assig($1, $3); } | ID ASSIG const { $$ = add_assig_const($1, $3); }
expr:       param op param { $$ = append_three($1, $2, $3); }
op:         PLUS { $$ = "+"; } | MINUS { $$ = "-"; } | TIMES { $$ = "*"; } | DIV { $$ = "/"; } | MOD { $$ = "%"; }
comp:       param compop param  { $$ = append_three($1, $2, $3); }
compop:     EQ { $$ = "=="; } | GTR { $$ = ">"; } | LSS { $$ = "<"; } | GEQ { $$ = ">="; } | LEQ { $$ = "<="; } | NEQ { $$ = "!="; }
param:      ID { $$ = $1; } | const { $$ = itoa($1); }
const:      INTEGERVAL { $$ = $1; }
if:         IFSYM comp START stmts END { $$ = add_conditional($comp, $stmts); }
read:       READOP ID { $$ = add_read($2); }
write:      WRITEOP param { $$ = add_write($2); }

decl:       DECLVAR ID AS INTEGER { declare($2); $$ = ""; }
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
    
    while (!feof(yyin)) {
        yyparse();
    }
    
    printf("The file is sintactly correct!\n");
    
    return 0;
}

void yyerror(const char *s) {
    
    printf("Error: %s at line -> %d ! \n", s, line_number);
    exit(-1);
}