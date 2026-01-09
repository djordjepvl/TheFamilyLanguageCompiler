.class public primer6
.super java/lang/Object

.method public <init>()V
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method

.method public static main([Ljava/lang/String;)V
    .limit stack 10
    .limit locals 10
    ldc 115
    istore 1
    ldc 230.4
    fstore 2
    fload 2
    dup
    fstore 2
    pop
    ldc "Silivio Dante"
    astore 3
    ldc "Paulie “Walnuts” Gualtieri"
    astore 4
    ldc "Tony Soprano"
    astore 5
    getstatic java/lang/System/out Ljava/io/PrintStream;
    aload 5
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
    return
.end method
