
; multiplikation

        LDI R0,30000
        LDI R1,15000

        MOV R2,R0
        ANDI R2,0xff
        MOV R3,R1
        ANDI R3,0xff
        MOV R4,R2
        MUL R4,R3       ; R4/R5 low*low
        LDI R5,0

        MOV R2,R0
        SWAP R2
        ANDI R2,0xff
        MOV R3,R1
        ANDI R3,0xff
        MOV R6,R2
        MUL R6,R3       ; R6/R7 high*low
        LDI R7,0

        MOV R2,R0
        ANDI R2,0xff
        MOV R3,R1
        SWAP R3
        ANDI R3,0xff
        MOV R8,R2
        MUL R8,R3       ; R8/R9 low*high
        LDI R9,0

        MOV R2,R0
        SWAP R2
        ANDI R2,0xff
        MOV R3,R1
        SWAP R3
        ANDI R3,0xff
        MOV R10,R2
        MUL R10,R3      ; R10 high*high

        LDI R0,8
l1:     LSL R6
        ROL R7
        LSL R8
        ROL R9
        SUBI R0,1
        BRNZ l1

        ADD R4,R6
        ADC R5,R7
        ADD R4,R8
        ADC R5,R9
        ADD R5,R10

        STS R4,1        
        STS R5,0

        BRK
