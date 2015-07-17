        .data text "Hello World!\n",0;

        .const termPort 0x1f    

        .reg R_DATA R1
        .reg R_ADDR R0


        LDI R_ADDR,text
L1:     LD R_DATA,[R_ADDR]
        OUT termPort,R_DATA
        INC R_ADDR
        CPI R_DATA,0
        BRNZ L1
        
        BRK