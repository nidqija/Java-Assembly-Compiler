.intel_syntax noprefix
.global main
.extern puts

.section .data
    msg_0: .asciz "Finished adding!"

.section .text

MathHelper_add:
    push rbp
    mov rbp , rsp
    sub rsp, 32
   mov [rbp - 8],rcx
   mov [rbp - 16],rdx
 mov rax, [rbp - 8]
    push rax
 mov rax, [rbp - 16]
    mov rbx, rax
    pop rax
    add rax, rbx
.L_add_end:
    mov rsp, rbp
    pop rbp
    ret

main:
    push rbp
    mov rbp , rsp
    sub rsp, 32
   mov [rbp - 8],rcx
   mov rax, 10
    mov rcx, rax
    mov rdi, rax
   mov rax, 20
    mov rdx, rax
    mov rsi, rax
    call MathHelper_add
    mov [rbp - 16], rax
      lea rcx, [rip + msg_0]
      lea rdi, [rip + msg_0]
      call puts
.L_main_end:
    mov rax, 0
    mov rsp, rbp
    pop rbp
    ret
