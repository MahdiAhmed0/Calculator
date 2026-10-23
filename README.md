# Calculator

Arbitrary-length integer calculator for a CS2 course, written in Java with every number stored as a linked list of digits.

## Overview

This is a console calculator that adds, subtracts and multiplies non-negative integers of any length. Instead of `int` or `long`, each number is kept as a singly linked list with one digit per node, so the only limit on the size of the operands is memory.

The motivation is overflow: a fixed-width type such as `int` holds values from -2,147,483,648 to 2,147,483,647, and a calculation whose result falls outside that range overflows and produces a wrong value. Storing digits in a list removes that limit, at the cost of implementing the arithmetic by hand.

It supports addition, subtraction (in either operand order, with negative results printed with a leading minus sign) and multiplication (schoolbook long multiplication). There is no division and no input validation. It is a NetBeans project (project name `CSProject`) and all of the code lives in a single file, [src/Main.java](src/Main.java).

## Requirements

- A JDK. The source uses only `java.util.Scanner` and has no dependency on recent language features, so any current JDK can build it. The NetBeans project is configured for source and target level 19, and the committed class files in `build/classes` are Java 19 class files, so running them without recompiling needs Java 19 or newer.
- NetBeans is optional. The repository includes the NetBeans project metadata (`nbproject/`, `build.xml`, `manifest.mf`), so the folder can be opened directly as a project.

## Build and run

From the repository root:

```bash
javac -d out src/Main.java
java -cp out Main
```

The committed classes can also be run directly without compiling, on a Java 19 or newer runtime:

```bash
java -cp build/classes Main
```

Or open the folder in NetBeans and run the `CSProject` project; the main class is `Main`.

### Input

| Prompt | What to type |
| --- | --- |
| `Enter num1:` | First operand: digits only, no sign, no leading zeros (see Known limitations), any length |
| `Enter num2:` | Second operand, same format |
| `choose between (+, -, *) :` | One of `+`, `-` or `*` |
| Any prompt | `Exit` (in any letter case) to quit |

Tokens are separated by whitespace, so the three values can also be typed on one line (`3 4 +`).

### Example session

```text
Welcome to Mahdi's Calculator
to quit at anytime just type "Exit"
Enter num1: 99999999999999999999
Enter num2: 1
choose between (+, -, *) : +
results: 100000000000000000000

Enter num1: 12345678901234567890
Enter num2: 9876543210987654321
choose between (+, -, *) : -
results: 2469135690246913569

Enter num1: 1
Enter num2: 1000
choose between (+, -, *) : -
results: -999

Enter num1: 500
Enter num2: 500
choose between (+, -, *) : -
results: 0

Enter num1: 99999999999999999999
Enter num2: 99999999999999999999
choose between (+, -, *) : *
results: 9999999999999999999800000000000000000001

Enter num1: 123
Enter num2: 0
choose between (+, -, *) : *
results: 0

Enter num1: 4
Enter num2: 2
choose between (+, -, *) : /
undefined.

Enter num1: exit
```

More examples: `2147483647 + 1 = 2147483648` (one past the `int` limit), `12345 * 6789 = 83810205`, `123456789 * 987654321 = 121932631112635269`, `100 - 99 = 1` and `99 - 100 = -1`.

## How it works

### Data structures

`src/Main.java` contains three classes:

| Class | Role |
| --- | --- |
| `Node` | One digit (`int value`) and a `next` reference. |
| `LinkedList` | Singly linked list with a `head` pointer. Holds the general list operations and all of the arithmetic. |
| `Main` | The console loop: reads input, builds the operand lists, dispatches on the operator and prints the result. |

`LinkedList` also carries the generic operations `AddHead`, `AddTail`, `DeleteHead`, `DeleteTail`, `DeleteAll`, `AddAfter`, `Find`, `Length` and `Print`. `DeleteHead`, `DeleteTail`, `AddAfter` and `Find` are not used by the calculator itself.

### Digit order

`convertStrToNumAndAddToList(String x)` walks the input string from left to right, converts each character with `Character.getNumericValue` and calls `AddHead` for it. Because every digit is pushed onto the front, the finished list is reversed: the head holds the least significant digit. For the input `123` the list is `3 -> 2 -> 1`. This is the order the arithmetic needs, since addition, subtraction and multiplication all start at the ones digit.

The result list works the other way around. Each arithmetic method is called on an empty `results` list. Addition and subtraction prepend every result digit to `head` as it is produced; multiplication builds the product in a temporary list and copies it into `results` at the end. In both cases digits arrive least significant first and each one is pushed to the front, so the finished `results` list reads most significant digit first, and `Print` walks it from `head` and prints the values in order.

### Addition: `addTwoLists`

Walks both operand lists in parallel from their heads. When one list runs out before the other, a temporary zero node stands in for the missing digit. For each position:

```text
currentRes = first.value + second.value + carry
carry      = currentRes / 10
lastDigit  = currentRes % 10
```

`lastDigit` is prepended to the result. After the loop, a non-zero carry is prepended as the final, most significant digit.

### Subtraction: `subtractTwoLists` and `whichBigger`

Subtraction first has to know which operand is larger so that it can always subtract the smaller from the larger. `whichBigger(list1, list2)` copies both lists into reversed "checker" lists (most significant digit first), then:

1. If the lists have different lengths, the longer one is treated as larger.
2. Otherwise it compares digit by digit from the most significant end.
3. It returns `1` if `list1` is larger, `0` if `list2` is larger and `-1` if they are equal.

`subtractTwoLists` then walks both lists in parallel, again padding the shorter one with zero nodes:

- Equal operands (`-1`): the result is a single `0` node and the method returns immediately.
- `list1` larger (`1`): computes `first.value - second.value` per digit. When the current digit of `first` is smaller than the digit of `second`, it borrows by adding 10 to the current digit and subtracting 1 from the next (more significant) node of `first`.
- `list2` larger (`0`): the same with the roles swapped, except that the borrow into the next node is skipped when `second` has no next node, and a `neg` flag is set.

After the loop, leading zeros are stripped from the result list. If `neg` is set, the value of the leading digit is negated, so a result list `1 -> 2 -> 3` with the flag set prints as `-123`.

### Multiplication: `multiplyTwoLists`, `singleMultiply` and `addTwoListsForMultiply`

Long multiplication, one digit of the second operand at a time:

1. `answer` starts as a list holding a single `0`.
2. For each digit of `list2`, starting at the least significant one, `singleMultiply(digit, list1)` multiplies the whole of `list1` by that digit with the usual carry and returns the partial product as a new list, least significant digit first (built with `AddTail`).
3. The partial product is shifted by prepending `addZero` zero nodes, where `addZero` is the position of the digit (0 for the ones digit, 1 for the tens digit, and so on).
4. `addTwoListsForMultiply(answer, tmp)` returns the sum of the running total and the shifted partial product as a new least-significant-first list, which replaces `answer`.
5. When all digits are processed, the final `answer` is copied into `results` by prepending each node, which reverses it into most-significant-first order for printing.
6. If every node of `answer` is `0`, the result is reset to a single `0` node, so multiplying by zero prints `0` rather than a run of zeros.

### Console loop: `Main.main`

- Prints a welcome line and the hint that typing `Exit` quits.
- In an endless loop, creates fresh `num1List`, `num2List` and `results` lists, then reads three whitespace-separated tokens with `Scanner.next()`: the first number, the second number and the operator.
- `Exit` (in any letter case) at any of the three prompts calls `System.exit(0)`.
- `+`, `-` and `*` call `addTwoLists`, `subtractTwoLists` or `multiplyTwoLists` on `results` and print it. Any other operator prints `undefined.`.

## Project structure

| Path | Role |
| --- | --- |
| [src/Main.java](src/Main.java) | All source code: `Node`, `LinkedList` and `Main`. |
| `README.md` | This file. |
| `build.xml` | NetBeans Ant build script for the `CSProject` project. |
| `manifest.mf` | Manifest template used by the NetBeans build. |
| `nbproject/project.xml`, `nbproject/project.properties` | NetBeans project definition (name `CSProject`, main class `Main`, source level 19). |
| `nbproject/build-impl.xml`, `nbproject/genfiles.properties` | Generated NetBeans build support. |
| `nbproject/private/` | NetBeans per-user settings that were committed along with the project. |
| `build/classes/` | Compiled `Node.class`, `LinkedList.class` and `Main.class`, plus a small NetBeans-generated `Main.rs` text file, committed as built. |

## Known limitations

- Input is not validated. Every character of an operand goes through `Character.getNumericValue`, so a minus sign, a decimal point or letters are accepted silently and produce a meaningless result (for example `-5 + 3` prints `-18`); in subtraction they can also crash the program with a `NullPointerException`. Only non-negative integers made of digits are supported, and there is no division.
- Leading zeros are not normalized. `007 + 1` prints `008` and `007 * 2` prints `014`. In subtraction, leading zeros break the comparison in `whichBigger`, which treats the longer string as the larger number. Depending on the operands this either prints a wrong answer (`5 - 01` prints `-96`) or crashes with a `NullPointerException` (`01 - 5`: a borrow runs past the most significant node; `07 - 7`: the zero-stripping loop runs off the end of an all-zero result).
- The loop reads with `Scanner.next()` and never checks for end of input. When input is piped in and does not end with `Exit`, the program terminates with a `NoSuchElementException` after the last operation.

## Credits

- Mahdi (Fibonacci): the developer.
- Dr. Shahabuddin Muhammad: helped a lot.

## Status

Course project, uploaded to GitHub in August 2023 in a single commit. The code has not changed since: the later commits only edited the README. The repository does not include a license file.
