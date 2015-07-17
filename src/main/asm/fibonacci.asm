     LDI R0,10
     PUSH R0       ; n
     CALL fibonacci
     STS 0, R0     ; store result
     BRK

     .const n 2
     .const nm1 -1
fibonacci:
     ENTER 1

     LDD R0,[BP,n]   ; n
     CPI R0,2
     BRC fibEnd

     SUBI R0, 1
     PUSH R0
     CALL fibonacci
     STD [BP,nm1],R0

     LDD R0,[BP,n]   ; n
     SUBI R0, 2
     PUSH R0
     CALL fibonacci
	
     LDD R1,[BP,nm1] ; f(n-1)
     ADD R0,R1

fibEnd:
     LEAVE
     RET 1