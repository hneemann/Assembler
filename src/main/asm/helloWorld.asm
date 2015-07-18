        .data text "Hello World!\n",0;

        .const termPort 0x1f    

        .reg DATA R1      ; alias for R1
        .reg ADDR R0      ; alias for R0


        LDI ADDR,text     ; load text addr
L1:     LD DATA,[ADDR]    ; load a character
        CPI DATA,0        ; is character zero?
        BREQ L2           ; yes, goto end
        OUT termPort,DATA ; write character to console
        INC ADDR          ; increment ADDR to next character
        JMP L1         
        
L2:     BRK