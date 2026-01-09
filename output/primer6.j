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
    ldc 0.43024
    ldc 324.0234
    fadd
    pop
    return
.end method
