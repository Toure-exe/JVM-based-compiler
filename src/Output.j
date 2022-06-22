.class public Output 
.super java/lang/Object

.method public <init>()V
 aload_0
 invokenonvirtual java/lang/Object/<init>()V
 return
.end method

.method public static print(I)V
 .limit stack 2
 getstatic java/lang/System/out Ljava/io/PrintStream;
 iload_0 
 invokestatic java/lang/Integer/toString(I)Ljava/lang/String;
 invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
 return
.end method

.method public static read()I
 .limit stack 3
 new java/util/Scanner
 dup
 getstatic java/lang/System/in Ljava/io/InputStream;
 invokespecial java/util/Scanner/<init>(Ljava/io/InputStream;)V
 invokevirtual java/util/Scanner/next()Ljava/lang/String;
 invokestatic java/lang/Integer.parseInt(Ljava/lang/String;)I
 ireturn
.end method

.method public static run()V
 .limit stack 1024
 .limit locals 256
 ldc 2
 ldc 3
 iadd 
 invokestatic Output/print(I)V
 ldc 4
 invokestatic Output/print(I)V
L2:
 ldc 4
 invokestatic Output/print(I)V
 ldc 2
 ldc 3
 iadd 
 invokestatic Output/print(I)V
L3:
 ldc 2
 ldc 3
 ldc 4
 imul 
 imul 
 invokestatic Output/print(I)V
L4:
 ldc 2
 ldc 4
 imul 
 ldc 3
 isub 
 invokestatic Output/print(I)V
L5:
 ldc 2
 ldc 7
 ldc 3
 isub 
 iadd 
 invokestatic Output/print(I)V
L6:
 ldc 10
 ldc 2
 idiv 
 ldc 3
 iadd 
 invokestatic Output/print(I)V
L7:
 ldc 5
 ldc 7
 ldc 3
 isub 
 ldc 10
 iadd 
 iadd 
 invokestatic Output/print(I)V
L1:
 return
.end method

.method public static main([Ljava/lang/String;)V
 invokestatic Output/run()V
 return
.end method

