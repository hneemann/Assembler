	.const TERMINAL_PORT 0x1f

; write stack argumment to console as 4 digit hex number
	.reg DATA r0  ; data
	.reg DIGIT r1 ; a single digit
hexOut: 
	enter 0
	LDD r0,[bp,2]
	swap DATA
	swapn DATA
	CALL hexDigitOutR0
	swapn DATA
	CALL hexDigitOutR0
	swap DATA
	swapn DATA
	CALL hexDigitOutR0
	swapn DATA
	CALL hexDigitOutR0
	leave
	ret

; write R0 to console as 1 digit hex number
hexDigitOutR0: 
	mov DIGIT,DATA
	andi DIGIT,0xf
	cpi DIGIT,10
	brnc h3      ; larger then 10
	addi DIGIT,'0'
	jmp h4
h3:	addi DIGIT,'A'-10
h4:	out TERMINAL_PORT,DIGIT
	ret

newline: ldi DIGIT,'\n'
	out TERMINAL_PORT,DIGIT
	ret