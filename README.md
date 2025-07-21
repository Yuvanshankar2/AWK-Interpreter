# AWK-Interpreter

# What is AWK?
AWK is a scripting language that is mainly used for processing text. You can learn more about the language from the following sources:

[Youtube](https://www.youtube.com/watch?v=oPEnvuj9QrI)

[GeeksforGeeks](https://www.geeksforgeeks.org/linux-unix/awk-command-unixlinux-examples/)

[Baeldung](https://www.baeldung.com/linux/awk-guide)

[Wikipedia](https://en.wikipedia.org/wiki/AWK)

# Project Description
I implemented an interpreter for the AWK scripting language in Java which is comprised of the following core components:

1) A 3-phase agile software development methodology
2) Lexer Phase
3) Parser Phase
4) Interpretation Phase
5) Unit testing

# How to get started

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
3) Open ```Main.java``` and add replace the existing code with the following:

   ```
      Path path = Paths.get(args[0]);
		String data = new String(Files.readAllBytes(path));
		Lexer lexer = new Lexer(data);
		lexer.Lex();
		Parser parser = new Parser(lexer.getList()); 
		ProgramNode program = parser.Parse();
        Interpreter interpreter = new Interpreter(program, "YOUR AWK FILE");
        interpreter.IntepretProgram(program);
   ```
   Replace ```YOUR AWK FILE``` with your awk program text file.

4) Open your terminal and run the following commands:
      ```
         javac -Xlint Main.java
         java Main FILE PATH
      ```
   Replace ```FILE PATH``` with the path to your input data file.

   This will run the interpreter and you will see the results on your terminal.

