.text
	.align 4
.globl  gcd
gcd:
gcd_bb2:
	movl	%ESI, %EAX
gcd_bb3:
	cmpl	$0, %EAX
	jne	gcd_bb6
gcd_bb4:
	movl	%EDI, %EAX
gcd_bb1:
	ret
gcd_bb6:
	call	gcd
	jmp	gcd_bb1
	jmp	gcd_bb1
.globl  main
main:
main_bb2:
main_bb3:
	call	input
	call	input
	call	output
main_bb1:
	ret
