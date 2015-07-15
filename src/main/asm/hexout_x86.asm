	ldi r0, 0x1Ab5
	push r0
	call hexOut
	
	call newline
	
	ldi r0, 0x2bc6
	push r0
	call hexOut

	call newline

	jmp _ADDR_


.include "hexout_x86.inc.asm"
