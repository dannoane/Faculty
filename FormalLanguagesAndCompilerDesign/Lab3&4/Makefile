build_lab3:
	flex specifications.lxi && gcc -g lex.yy.c -o lexa

build_lab4:
	flex lexical_analyser.lxi && bison -d parser.y && gcc -g lex.yy.c parser.tab.c -o compiler
