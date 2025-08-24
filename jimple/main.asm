global _start
global foo
global main

section .text

foo:
    push rbp
    mov rbp, rsp
    mov rax, 0
    pop rbp
    ret

main:
    push rbp
    mov rbp, rsp
    call foo
    pop rbp
    ret


_start:
    call main
    mov rdi, rax
    mov rax, 60
    syscall


