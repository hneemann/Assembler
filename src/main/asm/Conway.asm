

	.const GRAPHIC 0x8000
	.const BANKSWITCH 0x7fff
	.const SIZE    20
	.const COLOR   3
	
	.reg x R2 
	.reg y R3 
	.reg TEMP R4 
	.reg R_OFFS R5 
	.reg W_OFFS R6
	.reg COUNT R7
	.reg VALUE R8
	.reg BANK R9
	.reg NEWVAL R10
	.reg COUNT_RET R11

	ldi R0,0
	ldi R1,1

	ldi W_OFFS,GRAPHIC
	ldi R_OFFS,GRAPHIC+SIZE*SIZE

	;init screen
	ldi VALUE, COLOR
	ldi x, 16
	ldi y, 11
	call set
	inc x
	call set
	dec x
	inc y
	call set
	dec x
	call set
	inc x
	inc y
	call set

	jmp SWAP_PAGE

	; iterate
START:	ldi y,0
L_Y:	ldi x,0
L_X:	ldi NEWVAL,0
	call count
	call get
	;brk

	cpi VALUE,0
	BREQ isDead
isAlive:
	cpi COUNT,2
	breq wake
	cpi COUNT,3
	breq wake
	jmp loopEnd
isDead:
	cpi COUNT,3
	brne loopEnd
wake:	ldi NEWVAL,COLOR
	
loopEnd: 
	mov VALUE, NEWVAL
	call set
	inc x
	cpi x, SIZE
	brne L_X
	inc y
	cpi y, SIZE
	brne L_Y

	EORI BANK,1
	out BANKSWITCH,BANK
	
SWAP_PAGE:
	mov TEMP, W_OFFS
	mov W_OFFS, R_OFFS
	mov R_OFFS, TEMP

	brk
	jmp START
	

count:  ldi COUNT,0
	dec y
	rcall COUNT_RET, count_check
	inc x
	rcall COUNT_RET, count_check
	inc y
	rcall COUNT_RET, count_check
	inc y
	rcall COUNT_RET, count_check
	dec x
	rcall COUNT_RET, count_check
	dec x
	rcall COUNT_RET, count_check
	dec y
	rcall COUNT_RET, count_check
	dec y
	rcall COUNT_RET, count_check
	inc x
	inc y
	ret
count_check:
	cpi x,SIZE
	brcc c1
	cpi y,SIZE
	brcc c1
	mov TEMP,y
	muli TEMP,SIZE
	add TEMP,x
	add TEMP,R_OFFS
	inr VALUE,[TEMP]
	cpi VALUE,0
	breq c1
	inc COUNT
c1:	rret COUNT_RET


get:	mov TEMP,y
	muli TEMP,SIZE
	add TEMP,x
	add TEMP,R_OFFS
	inr VALUE,[TEMP]
	ret

set:	mov TEMP,y
	muli TEMP,SIZE
	add TEMP,x
	add TEMP,W_OFFS
	outr [TEMP],VALUE
	ret

