.class public primer6
.super java/lang/Object
.field private static scanner Ljava/util/Scanner;

.method public <init>()V
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method

.method public static main([Ljava/lang/String;)V
    .limit stack 50
    .limit locals 50
    new java/util/Scanner
    dup
    getstatic java/lang/System/in Ljava/io/InputStream;
    invokespecial java/util/Scanner/<init>(Ljava/io/InputStream;)V
    putstatic primer6/scanner Ljava/util/Scanner;
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
    iconst_0
    istore 4
    ldc "Paulie “Walnuts” Gualtieri"
    astore 5
    ldc "Tony Soprano"
    astore 6
    getstatic java/lang/System/out Ljava/io/PrintStream;
    iload 1
    invokevirtual java/io/PrintStream/println(I)V
    iload 4
    ifeq else_0
    getstatic java/lang/System/out Ljava/io/PrintStream;
    aload 5
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
    goto end_if_1
else_0:
    iload 1
    ldc 115
if_icmpge cmp_true_4
iconst_0
goto cmp_end_5
cmp_true_4:
iconst_1
cmp_end_5:
    ifeq else_2
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "Nothin"
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
    goto end_if_3
else_2:
    getstatic java/lang/System/out Ljava/io/PrintStream;
    aload 6
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
end_if_3:
end_if_1:
    return
    return
.end method
