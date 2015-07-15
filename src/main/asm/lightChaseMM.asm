
; Light Chase

	.reg POS R0
	.reg MASK R1
	.const LedAddr 0x15

	LDI MASK,1
	LDI POS,0

up:	INC POS
	LSL MASK
	STS MASK, LedAddr
	CPI POS,15
	BRNZ up

down:	DEC POS
	LSR MASK
	STS MASK, LedAddr
	CPI POS,0
	BRNZ down

	JMP up
	
	