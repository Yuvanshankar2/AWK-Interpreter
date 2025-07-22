# AWK-Interpreter

# What is AWK?
AWK is a scripting language that is mainly used for processing text. You can learn more about the language from the following sources:

[Youtube](https://www.youtube.com/watch?v=oPEnvuj9QrI)

[GeeksforGeeks](https://www.geeksforgeeks.org/linux-unix/awk-command-unixlinux-examples/)

[Baeldung](https://www.baeldung.com/linux/awk-guide)

[Wikipedia](https://en.wikipedia.org/wiki/AWK)

# Project Description

Language used for this project: Java

1) Designed and Implemented an interpreter for AWK, a text processing scripting language, using a 3-stage agile
software development methodology.

2) Implemented a Lexer phase, which tokenizes an incoming AWK script and stores the resulting tokens in a list.

3) Implemented a Parser phase which parses the list of tokens to generate an Abstract Syntax Tree (AST), providing a
structured hierarchical representation of the whole script.

4) Implemented an Interpreter phase, which walks through the AST and executes each parsed component. It leverages
hashmaps to store the computed values of global and local variables.

5) Utilized the JUnit testing library to develop unit tests for each phase of the project.

This project can help users understand:

1) The fundamental workflow of a lexer and parser.
2) The heirarchical tree-based structure of the scripting language. 

# How to get started?

1) Clone the repository 
2) Make two ```txt``` files. One of the files must contain sample data in the form of rows and columns such as the following:
   ```
       A 1000
       B 2000
       C 3000
       D 4000
       E 5000
   ```
   The other file must contain sample awk code. A basic example would be the following:

   ```
     {
       if($2 > 3000){
         print $1;
       }
    }
    ```
3) Open ```Main.java``` and replace the existing block of code with the following:

```
      public class Main {
	public static void main(String args[]) throws Exception {
		Path path = Paths.get(args[0]);
		String data = new String(Files.readAllBytes(path));
		Lexer lexer = new Lexer(data);
		lexer.Lex();
		Parser parser = new Parser(lexer.getList()); 
		ProgramNode program = parser.Parse();
        Interpreter interpreter = new Interpreter(program, "YOUR INPUT FILE");
        interpreter.IntepretProgram(program);
		}
	}
```
 Replace ```YOUR INPUT FILE``` with your input sample data file.

4) Open your terminal and run the following commands:
      ```
         javac -Xlint Main.java
         java Main AWK FILE PATH
      ```
   Replace ```AWK FILE PATH``` with the path to your awk program file.

   This will start the interpreter. The results will be displayed on your terminal.

