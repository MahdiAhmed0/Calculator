# Calculator

Arbitrary-length integer calculator for a CS2 course, written in Java with every number stored as a linked list of digits.

## Overview

A console calculator that adds, subtracts and multiplies non-negative integers of any length. Instead of `int` or `long`, each number is kept as a singly linked list with one digit per node, so the size of the operands is limited only by memory. The motivation is overflow: a fixed-width type such as `int` only holds values up to 2,147,483,647.

All of the code lives in [src/Main.java](src/Main.java), which contains the `Node`, `LinkedList` and `Main` classes. The folder is a NetBeans project (`CSProject`).

## Build and run

```bash
javac -d out src/Main.java
java -cp out Main
```

Or open the folder in NetBeans and run the project. Enter the first number, the second number and one of `+`, `-` or `*`; type `Exit` at any prompt to quit.

```text
Welcome to Mahdi's Calculator
to quit at anytime just type "Exit"
Enter num1: 99999999999999999999
Enter num2: 1
choose between (+, -, *) : +
results: 100000000000000000000

Enter num1: 1
Enter num2: 1000
choose between (+, -, *) : -
results: -999

Enter num1: 99999999999999999999
Enter num2: 99999999999999999999
choose between (+, -, *) : *
results: 9999999999999999999800000000000000000001
```

## How it works

- Parsing: `convertStrToNumAndAddToList` pushes each digit onto the head of a list, so the head holds the least significant digit, which is the order the arithmetic needs.
- Addition: `addTwoLists` walks both lists in parallel with a carry, padding the shorter list with zeros, and prepends each result digit so the result reads most significant digit first.
- Subtraction: `whichBigger` compares the operands by length and then digit by digit; `subtractTwoLists` always subtracts the smaller from the larger with borrowing and prints a leading minus sign when the result is negative.
- Multiplication: `multiplyTwoLists` uses schoolbook long multiplication, one partial product per digit (`singleMultiply`), shifted and accumulated with `addTwoListsForMultiply`.

Inputs are non-negative integers made of digits; the operators are `+`, `-` and `*`.

## Credits

- Mahdi (Fibonacci): the developer.
- Dr. Shahabuddin Muhammad: helped a lot.
