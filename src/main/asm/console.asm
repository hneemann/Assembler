	.data text "Hello World!\n",0;

	.const termPort 0x1f	

	.reg R_DATA R1
	.reg R_ADDR R0


L0:	ldi R_ADDR,text
L1:	ld R_DATA,R_ADDR
	out R_DATA,termPort
	inc R_ADDR
	cpi R_DATA,0
	brnz L1
	jmp L0