# Virtual Machine (VM) Project

A simple stack-based VM in C for executing basic arithmetic, logical operations, and control flow.

## Features Implemented
- **Arithmetic**: `ADD`, `SUB`, `MUL`, `DIV`, `MOD`
- **Logical**: `AND`, `OR`, `XOR`, `NOT`, `SHL`, `SHR`
- **Comparison**: `EQ`, `LT`, `GT`
- **Control Flow**: `JMP`, `JMP_IF`, `JMP_IF_NOT`
- **Stack Operations**: `PUSH`, `POP`, `DUP`
- **Program Control**: `HALT`

## Features Not Implemented
- **Function Calls**
- **Threads**
- **Memory Allocation**


### Bytecode for Example Programs
Here’s the corresponding bytecode arrays for the example programs:

1. **`(5 + 3) * 2 - 1 = 15`**:
   ```c
   int program1[] = {
       PUSH, 5,   // Push 5 onto the stack
       PUSH, 3,   // Push 3 onto the stack
       ADD,       // Add the top two values (5 + 3 = 8)
       PUSH, 2,   // Push 2 onto the stack
       MUL,       // Multiply the top two values (8 * 2 = 16)
       PUSH, 1,   // Push 1 onto the stack
       SUB,       // Subtract the top value from the second value (16 - 1 = 15)
       HALT       // Stop execution
   };
   ```

2. **`if (3 < 5) push 99 else push 0`**:
   ```c
   int program2[] = {
       PUSH, 3,   // Push 3 onto the stack
       PUSH, 5,   // Push 5 onto the stack
       LT,        // Compare if 3 < 5 (result is 1)
       JMP_IF, 9, // If true (1), jump to address 9 (PUSH 99)
       PUSH, 0,   // Else, push 0 onto the stack
       JMP, 11,   // Jump to address 11 (HALT)
       PUSH, 99,  // Push 99 onto the stack
       HALT       // Stop execution
   };
   ```

3. **`i = 0 while (i < 10) i++`**:
   ```c
   int program3[] = {
       PUSH, 0,    // Push 0 (initial value of i) onto the stack
       DUP,        // Duplicate the top value (i)
       PUSH, 10,   // Push 10 onto the stack
       LT,         // Compare if i < 10
       JMP_IF_NOT, 13, // If false, jump to address 13 (HALT)
       PUSH, 1,    // Push 1 onto the stack
       ADD,        // Increment i by 1 (i = i + 1)
       JMP, 2,     // Jump back to address 2 (DUP) to repeat the loop
       HALT        // Stop execution
   };
   ```

## How to Run
```bash
make
./build/vm
```
