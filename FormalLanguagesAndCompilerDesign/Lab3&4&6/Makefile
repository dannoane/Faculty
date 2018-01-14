build_lab3:
	cd Lab3/ && flex specifications.lxi && gcc -g lex.yy.c -o lexa

build_lab4:
	cd Lab4/ && flex lexical_analyser.lxi && bison -d parser.y && gcc -g lex.yy.c parser.tab.c -o compiler

build_lab6:
	cd Lab6/ && flex lexical_analyser.lxi && bison -d parser.y && gcc -g lex.yy.c parser.tab.h parser.tab.c -o compiler
