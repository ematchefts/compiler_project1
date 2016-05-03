.text
	.align 4
.globl  gcd
gcd:
gcd_bb2:
	movl	%EDI, %ECX
gcd_bb3:
	cmpl	$0, %ESI
	jne	gcd_bb6
gcd_bb4:
	movl	%ECX, %EAX
gcd_bb1:
	ret
gcd_bb6:
	movl	$0, %EDX
	movl	%ECX, %EAX
	idivl	%ESI, %EAX
	imull	%ESI, %EAX
	movl	%EAX, %EDI
	movl	%ECX, %EAX
	subl	%EDI, %EAX
	movl	%EAX, %ESI
	movl	%ESI, %EDI
	call	gcd
	jmp	gcd_bb1
	jmp	gcd_bb1
.globl  main
main:
main_bb2:
	pushq	%R15
main_bb3:
	call	input
	movl	%EAX, %R15D
	call	input
	movl	%EAX, %ESI
	movl	%R15D, %EDI
	call	gcd
	movl	%EAX, %EDI
	call	output
main_bb1:
	popq	%R15
	ret
