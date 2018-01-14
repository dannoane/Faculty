section .text
global main
extern scanf
extern printf
main:
LEA EDI, [rfmt]
LEA ESI, [x]
XOR EAX, EAX
CALL scanf
PUSH AX
MOV AX, [x]
IMUL AX, 20
MOV BX, AX
POP AX

PUSH AX
MOV AX, [y]
MOV AX, BX
MOV [y], AX
POP AX
MOV CX, [y]
MOV DX, 200
CMP CX, DX
JL DONE
LEA EDI, [wfmt]
MOV ESI, [y]
XOR EAX, EAX
CALL printf
DONE:
LEA EDI, [wfmt]
MOV ESI, [x]
XOR EAX, EAX
CALL printf

MOV EAX, 1
INT 0x80

section .data
rfmt	DB	"%hd",0
wfmt	DB	"%hd\n",0x0a,0
	x	DW	0
	y	DW	0
