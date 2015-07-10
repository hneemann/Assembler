	
	.const keyPort 0x16
	.const termPort 0x1f

l1:	in R0, keyPort
	out R0,termPort
	jmp l1