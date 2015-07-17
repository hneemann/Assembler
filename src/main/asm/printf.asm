        .data text1 "the sum of %x and %x\n",0;
        .data text2 "gives %x\n",0;

        LDI R0,5         ; 3. arg
        PUSH R0
        LDI R0,4         ; 2. arg
        PUSH R0
        LDI R0, text1    ; 1. arg
        PUSH R0
        CALL printf      ; call 

        ADDI SP, 3       ; clear stack
        LDI R0,4+5       ; 2. arg
        PUSH R0
        LDI R0, text2    ; 1. arg
        PUSH R0
        CALL printf      ; call 
        ADDI SP, 2       ; clear stack
        BRK	


        .const TERMINAL_PORT 0x1f
        .reg ARG_ADDR R4
        .reg TEXT_ADDR R3
        .reg DATA R0  ; data
        .reg DIGIT R1 ; a single digit
printf:
        ENTER 0
        MOV ARG_ADDR, BP        ; addr of args
        ADDI ARG_ADDR, 2        ; set to first arg
        LD TEXT_ADDR, [ARG_ADDR]; load as text addr
	
pr0:    LD DATA,[TEXT_ADDR]     ; get character
        CPI DATA,0              ; check zero
        BRZ prEnd               ; if zero goto end

        CPI DATA,'%'            ; check '%'
        BRZ prHandlePercent     ; '%' found
	
pr1:    OUT TERMINAL_PORT, DATA ; output character
pr2:    INC TEXT_ADDR           ; next character
        JMP pr0                 ; loop
	
prEnd:  LEAVE                   ; finish
        RET

prHandlePercent:
        INC TEXT_ADDR           ; inc text addr
        LD DATA,[TEXT_ADDR]     ; read char after '%'
        CPI DATA,'x'            ; check 'x'
        BRNZ error              ; if not: error
	
        INC ARG_ADDR            ; next argument
        LD DATA, [ARG_ADDR]     ; read next arg
        RCALL RA, hexOutR0      ; write as hex
        JMP pr2	                ; next character

error:  LDI R5, '%'
        OUT TERMINAL_PORT, R5   ; print '%'
        JMP pr1                 ; print DATA
	

; write R0 to console as 4 digit hex number
        .reg CREG r2  ; return adress register

hexOutR0: 
        SWAP DATA
        SWAPN DATA
        RCALL CREG,hexDigitOutR0
        SWAPN DATA
        RCALL CREG,hexDigitOutR0
        SWAP DATA
        SWAPN DATA
        RCALL CREG,hexDigitOutR0
        SWAPN DATA
        RCALL CREG,hexDigitOutR0
        RRET RA

; write R0 to console as 1 digit hex number
hexDigitOutR0: 
        MOV DIGIT,DATA
        ANDI DIGIT,0xf
        CPI DIGIT,10
        BRNC h3      ; larger then 10
        ADDI DIGIT,'0'
        JMP h4
h3:     ADDI DIGIT,'A'-10
h4:     OUT TERMINAL_PORT, DIGIT
        RRET CREG