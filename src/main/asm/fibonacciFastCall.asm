     LDI R0,10
     CALL fibonacci
     STS 0, R0     ; store result
     BRK

fibonacci:
     CPI R0,2
     BRC fibEnd

     PUSH R0      ; keep n
     SUBI R0, 1
     CALL fibonacci
     POP R1       ; get n
     PUSH R0      ; store result

     SUBI R1, 2
     MOV R0,R1
     CALL fibonacci
	
     POP R1
     ADD R0,R1
fibEnd:
     RET