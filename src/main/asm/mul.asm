
; multiplikation

    LDI R0,30000  ; first operand
    LDI R1,15000  ; second operand

    MOV R2,R0
    ANDI R2,0xff  ; low byte first operand
    MOV R3,R1
    ANDI R3,0xff  ; low byte second operand
    MOV R4,R2
    MUL R4,R3     ; R4/R5 = low*low
    LDI R5,0

    MOV R2,R0
    SWAP R2
    ANDI R2,0xff  ; high byte first operand
    MOV R3,R1
    ANDI R3,0xff  ; low byte second operand
    MOV R6,R2
    MUL R6,R3     ; R6/R7 = high*low
    LDI R7,0

    MOV R2,R0
    ANDI R2,0xff  ; low byte first operand
    MOV R3,R1
    SWAP R3
    ANDI R3,0xff  ; high byte second operand
    MOV R8,R2
    MUL R8,R3     ; R8/R9 = low*high
    LDI R9,0

    MOV R2,R0
    SWAP R2
    ANDI R2,0xff  ; high byte first operand
    MOV R3,R1
    SWAP R3
    ANDI R3,0xff  ; high byte second operand
    MOV R10,R2
    MUL R10,R3    ; R10 = high*high

    LDI R0,8     ; shift R6/R7, R8/R9 8 bits up
l1: LSL R6       ; move up R6/R7
    ROL R7
    LSL R8       ; move up R8/R9
    ROL R9
    SUBI R0,1    ; dec R0
    BRNE l1      ; if not zero repeat

                 ; add results
    ADD R4,R6    ; R4/R5 = R4/R5 + R6/R7
    ADC R5,R7
    ADD R4,R8    ; R4/R5 = R4/R5 + R8/R9
    ADC R5,R9
    ADD R5,R10   ; R4/R5 = R4/R5 + 0/R10

    STS 1, R4     ; write to memory
    STS 0, R5

    BRK
