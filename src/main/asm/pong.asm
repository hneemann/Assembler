

	.const GRAPH 0x8000
	.const KEYBOARD 0x1e
	.const SIZE 100
	.const LEN 4
	.const DIR_MASK 15


	.reg POS R0
	.reg n R1
	.reg COL R2
	.reg TEMP R3
	.reg COL0 R4
	.reg COL1 R5

	.reg x R6
	.reg y R7
	.reg flags R8
	.reg e R9
	.reg angle R10 
	.reg BALL R11
	.reg TEMP2 R12

	ldi x, SIZE/2
	ldi y, SIZE/2
	mov BALL, y
	muli BALL,SIZE
	add BALL,x
	addi BALL, GRAPH
	outr [BALL], COL1

;	init Ball


;	init Padel
	ldi POS, GRAPH+(SIZE-1)*SIZE+SIZE/2-2
	ldi COL0,0
	ldi COL1,1
	ldi n, LEN+1
i1:	outr [POS], COL1
	addi POS, 1
	dec n
	brne i1
	subi POS,LEN

	

LOOP:	brk
			
	call MOVE_BALL
	in TEMP, KEYBOARD
	cpi TEMP, '.'
	breq LEFT
	cpi TEMP, ','
	breq RIGHT
	jmp LOOP

RIGHT:	cpi POS,GRAPH+(SIZE-1)*SIZE
	breq LOOP
	dec POS
	outr [POS], COL1
	mov TEMP, POS
	addi TEMP, LEN+1
	outr [TEMP], COL0
	jmp LOOP

LEFT:	cpi POS,GRAPH+SIZE*SIZE-LEN-1
	breq LOOP
	outr [POS], COL0
	inc POS
	mov TEMP, POS
	addi TEMP, LEN
	outr [TEMP], COL1
	jmp LOOP

MOVE_BALL:
	mov TEMP, x
	andi TEMP, 15
	mov TEMP2, y
	andi TEMP2, 15
	cmp TEMP,TEMP2
	brcc DX_LARGE

DY_LARGE:
	nop

DX_LARGE:
	mov TEMP, x
	andi TEMP, ~DIR_MASK
	


	ret

