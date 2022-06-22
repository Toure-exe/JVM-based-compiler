# JVM-based-compiler
compiler for a simple programming language that runs over the Java Virtual Machine.

## Context Free Grammar (CFG)
![CFG](https://user-images.githubusercontent.com/40024835/174915959-c2fb8ead-456f-4c2f-a5c4-b92c9dd64df6.png)

## Instructions
The compiler take the input program from the "program.lft" file, after the compilation process, if no errors are reported, an Output.class file that will run on the JVM.
After your program is wrote, follow those steps:
1) javac Translator.java (compile the "Translator.java file)
2) java -jar jasmin.jar Output.j (generte the Output.class file by using the Jasmin library)
3)  java Output (run the Output.class file)

## Code exemples

```
/*prendi 4 numeri a b c d se a e maggiore di 
c e d metti in un a variabile i a*b, altirmenti incrementi 
b e c fino a che non raggiungono il valore di a*/

read(a);
read(b);
read(c);
read(d);
= i 0;

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
//  print testing

print(+(2 3) 4); //5 4
print(4 +(2 3)); //4 5
print(*(2 3 4)); //24
print(-*(2 4)3); //5
print(+(2 - 7 3)); //6
print(+(/ 10 2 3)); //8
print(+(5 - 7 3 10)) //19
```
