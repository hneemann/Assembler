
; Light Chase

	.reg POS R0
	.reg MASK R1
	.const LedPort 0x15

	LDI MASK,1
	LDI POS,0

up:	INC POS
	LSL MASK
	OUT LedPort, MASK
	CPI POS,15
	BRNZ up

down:	DEC POS
	LSR MASK
	OUT LedPort, MASK
	CPI POS,0
	BRNZ down

	JMP up
	
	