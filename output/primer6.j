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
    iload 1
    invokevirtual java/io/PrintStream/println(I)V
    iload 1
    ldc 100
if_icmpeq cmp_true_2
iconst_0
goto cmp_end_3
cmp_true_2:
iconst_1
cmp_end_3:
    ifeq else_0
    getstatic java/lang/System/out Ljava/io/PrintStream;
    aload 4
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
    goto end_if_1
else_0:
    iload 1
    ldc 115
if_icmpge cmp_true_6
iconst_0
goto cmp_end_7
cmp_true_6:
iconst_1
cmp_end_7:
    ifeq else_4
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "Nothin"
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
    goto end_if_5
else_4:
    getstatic java/lang/System/out Ljava/io/PrintStream;
    aload 5
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
end_if_5:
end_if_1:
    return
.end method
