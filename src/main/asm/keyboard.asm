	
	.const keyPort 0x16
	.const termPort 0x1f

l1:	in R0, keyPort
	cpi R0,0
	brz l1
	out termPort, R0
	jmp l1