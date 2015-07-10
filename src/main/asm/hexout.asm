	.const TERMINAL_PORT 0x1f

	ldi r0, 0x1Ab5
	_call hexOutR0
	jmp _ADDR_


; write R0 to console as 4 digit hex number
	.reg DATA r0  ; data
	.reg DIGIT r1 ; a single digit
	.reg CREG r2  ; return adress register

hexOutR0: 
	swap DATA
	swapn DATA
	RCALL CREG,hexDigitOutR0
	swapn DATA
	RCALL CREG,hexDigitOutR0
	swap DATA
	swapn DATA
	RCALL CREG,hexDigitOutR0
	swapn DATA
	RCALL CREG,hexDigitOutR0
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
h4:	out DIGIT,TERMINAL_PORT
	rret CREG