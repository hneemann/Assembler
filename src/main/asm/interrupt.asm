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
; Register R12 is reserved for use in ISR! 
; Don't use R12 in main program!
;********************************************************

.org 0x1000

	.word pos 		; leds position as a bit mask
	.word dir		; zero means up

	IN R12,0		; read flags
	PUSH r12		; store flags on stack
	PUSH r0			; save R0

	BRK

	LDS R0,dir		; read led direction
	LDS R12,pos		; read led pos
	CPI R12,0		; is pos zero?
	BRNE i1			; no? goto i1 
	LDI R12,1		; yes? Set pos to 1
	LDI R0,0		; and direction up
i1:	OUT LED_PORT, r12	; set leds
	CPI R0,0		; up?
	BRNE i2			; goto down
	LSL R12			; shift pos up
	CPI R12,0x8000		; end reached
	BRNE i3			; no
	LDI R0,1		; yes
	STS dir,R0		; dir to one or down
	JMP i3			; done
i2:	LSR R12			; handle down
	CPI R12,1		; bottom reached
	BRNE i3			; no? done
	LDI R0,0		; yes
	STS dir, R0		; dir to zero or up
i3:	STS pos,R12		; store pos for next irq

	POP R0			; resore R0
	POP R12			; get flags from stack
	OUT 0, R12		; restore flags
	IN R12, 1		; get ret addr from IC
	RRET R12		; return