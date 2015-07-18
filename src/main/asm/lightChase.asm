
; Light Chase

        .reg POS R0     ; alias for R0
        .reg MASK R1    ; alias for R1
        .const LedAddr 0x15

        LDI MASK,1      ; init MASK     
        LDI POS,0       ; init POS

up:     INC POS         ; increment position
        LSL MASK        ; shift MASK to the left
        OUT LedAddr, MASK
        CPI POS,15      ; loop
        BRNE up

down:   DEC POS         ; decrement mask
        LSR MASK        ; shift MASK to the right
        OUT LedAddr, MASK
        CPI POS,0       ; loop
        BRNE down

        JMP up
        
        