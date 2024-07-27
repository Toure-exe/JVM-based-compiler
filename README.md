# JVM-based-compiler
Compiler for a simple programming language that runs over the Java Virtual Machine.
The language contains basic arithmetic operations and input/output functions. Variables aren't explicitly declared and every arithmetic operations, assignment and conditions use the polish notation.
Lexer.java read all Tokens and check their validity (aka lexical analysis). 
"Translator.java" implements the syntax analysis by using the recursive descent parser algorithm.

## Context Free Grammar (CFG)

![CFG](https://user-images.githubusercontent.com/40024835/174915959-c2fb8ead-456f-4c2f-a5c4-b92c9dd64df6.png)

## Pseudo-code of the Recursive Descent Parser Algorithm
wikipedia: https://en.wikipedia.org/wiki/Recursive_descent_parser

![parsing_rd](https://user-images.githubusercontent.com/40024835/181624662-d159a3fd-7c1b-4066-8427-f685cfc765ad.png)

### - the "GUIDA" function
about FIRST & FOLLOW: https://www.gatevidyalay.com/first-and-follow-compiler-design/
![guide](https://user-images.githubusercontent.com/40024835/181624825-f6dac413-0d65-4eb9-afe1-e512fb580038.png)

## Token table
In this version && and || are recognized by the Lexer but not accepted by the Parser.

![tokens](https://user-images.githubusercontent.com/40024835/174922084-b591d02a-455f-4f60-bb8d-f4a38d969746.png)

## instructions set
wikipedia (JVM): https://en.wikipedia.org/wiki/List_of_Java_bytecode_instructions

## Usage instructions
The compiler take the input program from the "program.lft" file. After the compilation process, if no errors are reported, an Output.class will be generated.
After your program is wrote, follow these steps (java commands):
1) javac Translator.java (compile the "Translator.java" file)
2) java Translator (run the compiler)
3) java -jar jasmin.jar Output.j (generate the Output.class file by using the Jasmin library)
4) java Output (run the Output.class file)

(Maybe i'll upload a build.xml file to automate these four steps)

![scheme](https://user-images.githubusercontent.com/40024835/174924726-30a630e8-071d-419e-8190-b1871d020e92.png)

## Some code exemples


```
/*inzializare 3 variabili x y e z con lettura da tastiera
 successivamente confrontare che le x sia minore di y
 se non lo e incremento in un ciclo while la x fino ad arrivare al valore di y 
 dopo di che verifico che x sia nimore di z e in quel caso creo un ciclo while che incremente la x di 1 fino ad arrivare a z*/
read(x);
read(y);
read(z);

while(< x y)
    = x +(x 1);

while(< x z)
    = x +(x 1);

print(x y z)
```

```
/*3 variabili x,y,z, se x < y, si incrementa x fino a y, se y e' minore di z si decrementa la z fino a y, stampando x, y, z*/
// the semicolon is used for the separation of every instructions (like in other programming languages), EXPECT for the last one. 
// And if your have curly brackets that contains only one instruction, you can't use the semicolon neither.
read(x);
read(y);
read(z);
    cond  when(< x y) do
            while(< x y){
                 = x +(x 1)
           }
    else print(x);
       cond  when(< y z)  do
             while(< y z){
                = z - z 1
            }
    else print(z);


    print(x);
    print(y);
    print(z)
```


```
//  print testing

print(+(2 3) 4); //5 4
print(4 +(2 3)); //4 5
print(*(2 3 4)); //24
print(-*(2 4)3); //5
print(+(2 - 7 3)); //6
print(+(/ 10 2 3)); //8
print(+(5 - 7 3 10)) //19
```

```
// factorial algorithm

read(n);
= i 2;
= f 1;
while (<= i n){
   = f *(f i);
   = i +(i 1)
};
print(f)


```

