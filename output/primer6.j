.class public primer6
.super java/lang/Object

; Default constructor
.method public <init>()V
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method

; The "theSitDown" method
.method public static main([Ljava/lang/String;)V
    .limit stack 10
    .limit locals 10

    ; cash t = 115
    ldc 115
    istore 1

    ; slice g = 230.4
    ldc 230.4
    fstore 2

    ; g = (slice)t + 493.4
    iload 1
    i2f             ; Your CastNode
    ldc 493.4
    fadd
    fstore 2

    ; associate a, b, c
    ldc "Silivio Dante"
    astore 3
    ldc "Paulie Walnuts Gualtieri"
    astore 4
    ldc "Tony Soprano"
    astore 5

    ; check (t == 100)
    iload 1
    ldc 100
    if_icmpne LabelElse1

    ; spill b
    getstatic java/lang/System/out Ljava/io/PrintStream;
    aload 4
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
    goto LabelEnd1

LabelElse1:
    ; Nested check (t > 100)
    iload 1
    ldc 100
    if_icmple LabelElse2

    ; spill c
    getstatic java/lang/System/out Ljava/io/PrintStream;
    aload 5
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
    goto LabelEnd1

LabelElse2:
    ; spill a
    getstatic java/lang/System/out Ljava/io/PrintStream;
    aload 3
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V

LabelEnd1:
    ; sleepWithTheFishes
    return
.end method