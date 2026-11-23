.section data
 msg_0: .asciz "Hello world!"
.intel_syntax noprefix
.global main
.extern puts

.section .text

main:
    push rbp
    mov rbp , rsp
    sub rsp, 32
   mov rax, 10
    mov [rbp - 8], rax
   mov rax, 20
    mov [rbp - 16], rax
 mov rax, [rbp - 8]
    push rax
 mov rax, [rbp - 16]
    mov rbx, rax
    pop rax
    cmp rax, rbx
    jg .L_else_0
      lea rcx, [rip + msg_0]
      lea rdi, [rip + msg_0]
      call puts
    jmp .L_endif_1
.L_else_0:
    jmp .L_main_end
.L_endif_1:
.L_main_end:
    mov rax, 0
    mov rsp, rbp
    pop rbp
    ret
