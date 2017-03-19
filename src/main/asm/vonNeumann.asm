	.dorg 0x8000

	jmp start

	.const TERMINAL 0x1f

	.data text "Hello World\n",0

start:	ldi R0,text
loop:	ld R1,[R0]
	cpi R1,0
	breq start
	out TERMINAL, R1
	inc R0
	jmp loop
	