        .const TERMINAL_PORT 0x1f

        LDI R0, 0x19AB          ; first number
        RCALL RA, hexOutR0      ; output
        LDI R0,'\n'             ; load line feed
        OUT TERMINAL_PORT, R0   ; linefeed to console
        LDI R0, 0x0F8A          ; second number
        RCALL RA, hexOutR0      ; output
        BRK

; write R0 to console as 4 digit hex number
        .reg DATA r0            ; data
        .reg DIGIT r1           ; a single digit
        .reg CREG r2            ; return adress register

hexOutR0: 
        SWAP DATA               ; swap high and low byte
        SWAPN DATA              ; swap nibbles
        RCALL CREG,hexDigitOutR0; call, return addr to CREG
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
        MOV DIGIT,DATA          ; copy to digit
        ANDI DIGIT,0xf          ; clear higher bits
        CPI DIGIT,10            ; compare with 10
        BRCC h3                 ; larger then 10
        ADDI DIGIT,'0'          ; convert to digit
        JMP h4                  ; output
h3:     ADDI DIGIT,'A'-10       ; convert to character
h4:     OUT TERMINAL_PORT,DIGIT ; output
        RRET CREG               ; return to CREG