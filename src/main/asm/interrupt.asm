        .const TERMINAL_PORT 0x1f
	.const LED_PORT 0x15

        .reg DATA r0            ; data
        .reg DIGIT r1           ; a single digit
        .reg CREG r2            ; return adress register
        .reg Count r3 		; loop counter

repeat:
        LDI R0, 0x19AB          ; first number
        RCALL RA, hexOutR0      ; output
        LDI R0,' '              ; load blank
        OUT TERMINAL_PORT, R0   ; linefeed to console
        LDI R0, 0x0F8A          ; second number
        RCALL RA, hexOutR0      ; output
        LDI R0,' '              ; load blank
        OUT TERMINAL_PORT, R0   ; linefeed to console
	
	INC R3
	MOV R0, R3
        RCALL RA, hexOutR0      ; output

        LDI R0,'\n'             ; load line feed
        OUT TERMINAL_PORT, R0   ; linefeed to console
	JMP repeat

; write R0 to console as 4 digit hex number
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

;********************************************************
; ISR to move the LEDs up and down
; State machine that returns after LEDs are modified!
;********************************************************

.org 0x1000

	.word pos 		; leds position as a bit mask
	.word dir		; zero means up

	STD [SP-1], R0		; store R0 on stack
	IN R0, 0		; read the flags
	SUBI SP,1		; correct stack pointer
	PUSH R0			; store the flags on stack
	PUSH R1			; store R1 on stack

	BRK

	LDS R0,dir		; read led direction
	LDS R1,pos		; read led pos
	CPI R1,0		; is pos zero?
	BRNE i1			; no? goto i1 
	LDI R1,1		; yes? Set pos to 1
	LDI R0,0		; and direction up
i1:	OUT LED_PORT, R1	; set leds
	CPI R0,0		; up?
	BRNE i2			; goto down
	LSL R1			; shift pos up
	CPI R1,0x8000		; end reached
	BRNE i3			; no
	LDI R0,1		; yes
	STS dir,R0		; dir to one or down
	JMP i3			; done
i2:	LSR R1			; handle down
	CPI R1,1		; bottom reached
	BRNE i3			; no? done
	LDI R0,0		; yes
	STS dir, R0		; dir to zero or up
i3:	STS pos,R1		; store pos for next irq

	POP R1			; restore R1
	LD R0,[SP]		; get flags from stack
	ADDI SP,2		; correct stack pointer
	OUT 0, R0		; restore flags
	LDD R0,[sp-1]		; restore R0
	RETI			; return from ISR
