
	.byte A

.code 

	LDI R0,7
L1:	ST R0,(R0)+A
	SBI R0,1
	BRNZ L1

	LDI R0,7
L2:	LD R1,(R0)+A
	ST R1,(R0)+A+8
	SBI R0,1
	BRNZ L2
