# JVM-based-compiler
Compiler for a simple programming language that runs over the Java Virtual Machine.
The language contains basic arithmetic operations and input/output functions. Variables aren't explicitly declared, and every arithmetic operations, assignment and conditions use the polish notation.

## Context Free Grammar (CFG)

![CFG](https://user-images.githubusercontent.com/40024835/174915959-c2fb8ead-456f-4c2f-a5c4-b92c9dd64df6.png)

## Instructions
The compiler take the input program from the "program.lft" file. After the compilation process, if no errors are reported, an Output.class will be generated.
After your program is wrote, follow these steps (java commands):
1) javac Translator.java (compile the "Translator.java file)
2) java -jar jasmin.jar Output.j (generte the Output.class file by using the Jasmin library)
3) java Output (run the Output.class file)

(Maybe i'll upload a build.xml file to automate these three steps)

## Code exemples

```
/*prendi 4 numeri a b c d se a e maggiore di 
c e d metti in un a variabile i a*b, altirmenti incrementi 
b e c fino a che non raggiungono il valore di a*/

read(a); // the read function take value from the keyboard
read(b);
read(c);
read(d);
= i 0; // this is an assignment (in java it would be i = 0;)

cond 
    when(&&(< c a)(< d a)) do = i *(a b)
       else{    
               while(< b a)
                = b +(b 1);
               while(< c a)
                = c +(c 1)
       };
    
print(a b c d);
print(i)
```

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
