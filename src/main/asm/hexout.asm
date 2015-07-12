	.const TERMINAL_PORT 0x1f

	LDI R0, 0x1Ab5
	RCALL RA, hexOutR0
	BRK

; write R0 to console as 4 digit hex number
	.reg DATA r0  ; data
	.reg DIGIT r1 ; a single digit
	.reg CREG r2  ; return adress register

hexOutR0: 
	SWAP DATA
	SWAPN DATA
	RCALL CREG,hexDigitOutR0
	SWAPN DATA
	RCALL CREG,hexDigitOutR0
	SWAP DATA
	SWAPN DATA
	RCALL CREG,hexDigitOutR0
	SWAPN DATA
	RCALL CREG,hexDigitOutR0
	RRET RA

; write R0 to console as 1 digit hex number
hexDigitOutR0: 
	MOV DIGIT,DATA
	ANDI DIGIT,0xf
	CPI DIGIT,10
	BRNC h3      ; larger then 10
	ADDI DIGIT,'0'
	JMP h4
h3:	ADDI DIGIT,'A'-10
h4:	OUT DIGIT,TERMINAL_PORT
	RRET CREG