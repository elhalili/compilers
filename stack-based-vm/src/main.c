#include <stdio.h>
#include <stdlib.h>

#define STACK_SIZE 256
#define PROGRAM_SIZE 1024

typedef enum {
  HALT,
  PUSH,
  POP,
  ADD,
  SUB,
  MUL,
  DIV,
  MOD,
  EQ,
  LT,
  GT,
  AND,
  OR,
  XOR,
  NOT,
  SHL,
  SHR,
  JMP,
  JMP_IF,
  JMP_IF_NOT,
  DUP
} OpCode;

typedef struct {
  int stack[STACK_SIZE];
  int sp;
  int program[PROGRAM_SIZE];
  int pc;
  int running;
} VM;

void vm_init(VM *vm, int *program) {
  vm->sp = -1;
  vm->pc = 0;
  vm->running = 1;
  for (int i = 0; i < PROGRAM_SIZE; i++) {
    vm->program[i] = program[i];
  }
}
int counter = 0;
void vm_run(VM *vm) {
  while (vm->running && counter++ < 200) {
    int opcode = vm->program[vm->pc++];
    switch (opcode) {
    case HALT:
      vm->running = 0;
      break;

    case PUSH: {
      int val = vm->program[vm->pc++];
      if (vm->sp + 1 >= STACK_SIZE) {
        printf("Stack overflow\n");
        vm->running = 0;
      } else {
        vm->stack[++vm->sp] = val;
      }
      break;
    }

    case POP:
      if (vm->sp < 0) {
        printf("Stack underflow in POP\n");
        vm->running = 0;
      } else {
        vm->sp--;
      }
      break;

    case ADD: {
      if (vm->sp < 1) {
        printf("Stack underflow in ADD\n");
        vm->running = 0;
        break;
      }
      int a = vm->stack[vm->sp--];
      int b = vm->stack[vm->sp--];
      vm->stack[++vm->sp] = a + b;
      break;
    }

    case SUB: {
      if (vm->sp < 1) {
        printf("Stack underflow in SUB\n");
        vm->running = 0;
        break;
      }
      int a = vm->stack[vm->sp--];
      int b = vm->stack[vm->sp--];
      vm->stack[++vm->sp] = b - a;
      break;
    }

    case MUL: {
      if (vm->sp < 1) {
        printf("Stack underflow in MUL\n");
        vm->running = 0;
        break;
      }
      int a = vm->stack[vm->sp--];
      int b = vm->stack[vm->sp--];
      vm->stack[++vm->sp] = a * b;
      break;
    }

    case DIV: {
      if (vm->sp < 1) {
        printf("Stack underflow in DIV\n");
        vm->running = 0;
        break;
      }
      int a = vm->stack[vm->sp--];
      int b = vm->stack[vm->sp--];
      if (a == 0) {
        printf("Division by zero\n");
        vm->running = 0;
        break;
      }
      vm->stack[++vm->sp] = b / a;
      break;
    }

    case MOD: {
      if (vm->sp < 1) {
        printf("Stack underflow in MOD\n");
        vm->running = 0;
        break;
      }
      int a = vm->stack[vm->sp--];
      int b = vm->stack[vm->sp--];
      if (a == 0) {
        printf("Modulo by zero\n");
        vm->running = 0;
        break;
      }
      vm->stack[++vm->sp] = b % a;
      break;
    }

    case EQ: {
      if (vm->sp < 1) {
        printf("Stack underflow in EQ\n");
        vm->running = 0;
        break;
      }
      int a = vm->stack[vm->sp--];
      int b = vm->stack[vm->sp--];
      vm->stack[++vm->sp] = (b == a);
      break;
    }

    case LT: {
      if (vm->sp < 1) {
        printf("Stack underflow in LT\n");
        vm->running = 0;
        break;
      }
      int a = vm->stack[vm->sp--];
      int b = vm->stack[vm->sp--];
      vm->stack[++vm->sp] = (b < a);
      break;
    }

    case GT: {
      if (vm->sp < 1) {
        printf("Stack underflow in GT\n");
        vm->running = 0;
        break;
      }
      int a = vm->stack[vm->sp--];
      int b = vm->stack[vm->sp--];
      vm->stack[++vm->sp] = (b > a);
      break;
    }

    case AND: {
      if (vm->sp < 1) {
        printf("Stack underflow in AND\n");
        vm->running = 0;
        break;
      }
      int a = vm->stack[vm->sp--];
      int b = vm->stack[vm->sp--];
      vm->stack[++vm->sp] = b & a;
      break;
    }

    case OR: {
      if (vm->sp < 1) {
        printf("Stack underflow in OR\n");
        vm->running = 0;
        break;
      }
      int a = vm->stack[vm->sp--];
      int b = vm->stack[vm->sp--];
      vm->stack[++vm->sp] = b | a;
      break;
    }

    case XOR: {
      if (vm->sp < 1) {
        printf("Stack underflow in XOR\n");
        vm->running = 0;
        break;
      }
      int a = vm->stack[vm->sp--];
      int b = vm->stack[vm->sp--];
      vm->stack[++vm->sp] = b ^ a;
      break;
    }

    case NOT: {
      if (vm->sp < 0) {
        printf("Stack underflow in NOT\n");
        vm->running = 0;
        break;
      }
      int a = vm->stack[vm->sp--];
      vm->stack[++vm->sp] = ~a;
      break;
    }

    case SHL: {
      if (vm->sp < 1) {
        printf("Stack underflow in SHL\n");
        vm->running = 0;
        break;
      }
      int shift = vm->stack[vm->sp--];
      int val = vm->stack[vm->sp--];
      vm->stack[++vm->sp] = val << shift;
      break;
    }

    case SHR: {
      if (vm->sp < 1) {
        printf("Stack underflow in SHR\n");
        vm->running = 0;
        break;
      }
      int shift = vm->stack[vm->sp--];
      int val = vm->stack[vm->sp--];
      vm->stack[++vm->sp] = val >> shift;
      break;
    }

    case JMP: {
      int addr = vm->program[vm->pc++];
      vm->pc = addr;
      break;
    }

    case JMP_IF: {
      int addr = vm->program[vm->pc++];
      if (vm->sp < 0) {
        printf("Stack underflow in JMP_IF\n");
        vm->running = 0;
        break;
      }
      int cond = vm->stack[vm->sp--];
      if (cond)
        vm->pc = addr;
      break;
    }

    case JMP_IF_NOT: {
      int addr = vm->program[vm->pc++];
      if (vm->sp < 0) {
        printf("Stack underflow in JMP_IF_NOT\n");
        vm->running = 0;
        break;
      }
      int cond = vm->stack[vm->sp--];
      if (!cond)
        vm->pc = addr;
      break;
    }

    case DUP: {
      if (vm->sp < 0) {
        printf("Stack underflow in DUP\n");
        vm->running = 0;
        break;
      }
      int val = vm->stack[vm->sp];
      vm->stack[++vm->sp] = val;
      break;
    }

    default:
      printf("Unknown opcode: %d\n", opcode);
      vm->running = 0;
      break;
    }
  }

  printf("VM halted. Stack top: ");
  if (vm->sp >= 0)
    printf("%d\n", vm->stack[vm->sp]);
  else
    printf("empty\n");
}

int main() {
  // example 1: (5 + 3) * 2 - 1 = 15
  int program1[] = {PUSH, 5, PUSH, 3, ADD, PUSH, 2, MUL, PUSH, 1, SUB, HALT};

  VM vm;
  vm_init(&vm, program1);
  vm_run(&vm);

  // example 2: if (3 < 5) push 99 else push 0
  int program2[] = {PUSH, 3, PUSH, 5,  LT,   JMP_IF, 11,
                    PUSH, 0, JMP,  13, PUSH, 99,     HALT};
  vm_init(&vm, program2);
  vm_run(&vm);

  // example 3: i = 0 while (i < 10) i++
  int program3[] = {PUSH, 0,    DUP, PUSH, 10,  LT, JMP_IF_NOT,
                    13,   PUSH, 1,   ADD,  JMP, 2,  HALT};

  vm_init(&vm, program3);
  vm_run(&vm);
  return 0;
}