	.dorg 0x0
	; sets the ram start address to 0x0 and enables von Neumann mode.
	; in this mode the .data directive does not create a data moving stub, but
	; inserts the data directly into the program code. Therefore you have to jump
	; over the data.

	jmp start

	.const TERMINAL 0x1f

	.data text "Hello World\n",0

start:	ldi R0,text
loop:	lpm R1,[R0]
	cpi R1,0
	breq start
	out TERMINAL, R1
	inc R0
	jmp loop
	