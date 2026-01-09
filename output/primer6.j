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
end_if_1:
    iload 1
    ldc 100
    if_icmpeq cmp_true_6
    iconst_0
    goto cmp_end_7
cmp_true_6:
    iconst_1
cmp_end_7:
    ifeq else_4
    getstatic java/lang/System/out Ljava/io/PrintStream;
    aload 5
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
    goto end_if_5
else_4:
    iload 1
    ldc 10
    if_icmpeq cmp_true_10
    iconst_0
    goto cmp_end_11
cmp_true_10:
    iconst_1
cmp_end_11:
    ifeq else_8
    getstatic java/lang/System/out Ljava/io/PrintStream;
    aload 3
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
    goto end_if_9
else_8:
    iload 1
    ldc 30
    if_icmpeq cmp_true_14
    iconst_0
    goto cmp_end_15
cmp_true_14:
    iconst_1
cmp_end_15:
    ifeq else_12
    getstatic java/lang/System/out Ljava/io/PrintStream;
    aload 3
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
    goto end_if_13
else_12:
    iload 1
    ldc 40
    if_icmpeq cmp_true_18
    iconst_0
    goto cmp_end_19
cmp_true_18:
    iconst_1
cmp_end_19:
    ifeq else_16
    getstatic java/lang/System/out Ljava/io/PrintStream;
    aload 5
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
    goto end_if_17
else_16:
end_if_17:
end_if_13:
end_if_9:
end_if_5:
    iload 1
    ldc 0
    if_icmpeq cmp_true_22
    iconst_0
    goto cmp_end_23
cmp_true_22:
    iconst_1
cmp_end_23:
    ifeq else_20
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc 0
    invokevirtual java/io/PrintStream/println(I)V
    goto end_if_21
else_20:
    iload 1
    ldc 1
    if_icmpeq cmp_true_26
    iconst_0
    goto cmp_end_27
cmp_true_26:
    iconst_1
cmp_end_27:
    ifeq else_24
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc 1
    invokevirtual java/io/PrintStream/println(I)V
    goto end_if_25
else_24:
    iload 1
    ldc 2
    if_icmpeq cmp_true_30
    iconst_0
    goto cmp_end_31
cmp_true_30:
    iconst_1
cmp_end_31:
    ifeq else_28
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc 2
    invokevirtual java/io/PrintStream/println(I)V
    goto end_if_29
else_28:
    iload 1
    ldc 3
    if_icmpeq cmp_true_34
    iconst_0
    goto cmp_end_35
cmp_true_34:
    iconst_1
cmp_end_35:
    ifeq else_32
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc 3
    invokevirtual java/io/PrintStream/println(I)V
    goto end_if_33
else_32:
    iload 1
    ldc 4
    if_icmpeq cmp_true_38
    iconst_0
    goto cmp_end_39
cmp_true_38:
    iconst_1
cmp_end_39:
    ifeq else_36
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc 4
    invokevirtual java/io/PrintStream/println(I)V
    goto end_if_37
else_36:
    iload 1
    ldc 5
    if_icmpeq cmp_true_42
    iconst_0
    goto cmp_end_43
cmp_true_42:
    iconst_1
cmp_end_43:
    ifeq else_40
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc 5
    invokevirtual java/io/PrintStream/println(I)V
    goto end_if_41
else_40:
    iload 1
    ldc 6
    if_icmpeq cmp_true_46
    iconst_0
    goto cmp_end_47
cmp_true_46:
    iconst_1
cmp_end_47:
    ifeq else_44
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc 6
    invokevirtual java/io/PrintStream/println(I)V
    goto end_if_45
else_44:
    iload 1
    ldc 7
    if_icmpeq cmp_true_50
    iconst_0
    goto cmp_end_51
cmp_true_50:
    iconst_1
cmp_end_51:
    ifeq else_48
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc 7
    invokevirtual java/io/PrintStream/println(I)V
    goto end_if_49
else_48:
    iload 1
    ldc 8
    if_icmpeq cmp_true_54
    iconst_0
    goto cmp_end_55
cmp_true_54:
    iconst_1
cmp_end_55:
    ifeq else_52
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc 8
    invokevirtual java/io/PrintStream/println(I)V
    goto end_if_53
else_52:
    iload 1
    ldc 9
    if_icmpeq cmp_true_58
    iconst_0
    goto cmp_end_59
cmp_true_58:
    iconst_1
cmp_end_59:
    ifeq else_56
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc 9
    invokevirtual java/io/PrintStream/println(I)V
    goto end_if_57
else_56:
    iload 1
    ldc 10
    if_icmpeq cmp_true_62
    iconst_0
    goto cmp_end_63
cmp_true_62:
    iconst_1
cmp_end_63:
    ifeq else_60
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc 10
    invokevirtual java/io/PrintStream/println(I)V
    goto end_if_61
else_60:
    iload 1
    ldc 11
    if_icmpeq cmp_true_66
    iconst_0
    goto cmp_end_67
cmp_true_66:
    iconst_1
cmp_end_67:
    ifeq else_64
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc 11
    invokevirtual java/io/PrintStream/println(I)V
    goto end_if_65
else_64:
    iload 1
    ldc 12
    if_icmpeq cmp_true_70
    iconst_0
    goto cmp_end_71
cmp_true_70:
    iconst_1
cmp_end_71:
    ifeq else_68
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc 12
    invokevirtual java/io/PrintStream/println(I)V
    goto end_if_69
else_68:
    iload 1
    ldc 13
    if_icmpeq cmp_true_74
    iconst_0
    goto cmp_end_75
cmp_true_74:
    iconst_1
cmp_end_75:
    ifeq else_72
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc 13
    invokevirtual java/io/PrintStream/println(I)V
    goto end_if_73
else_72:
    iload 1
    ldc 14
    if_icmpeq cmp_true_78
    iconst_0
    goto cmp_end_79
cmp_true_78:
    iconst_1
cmp_end_79:
    ifeq else_76
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc 14
    invokevirtual java/io/PrintStream/println(I)V
    goto end_if_77
else_76:
    iload 1
    ldc 15
    if_icmpeq cmp_true_82
    iconst_0
    goto cmp_end_83
cmp_true_82:
    iconst_1
cmp_end_83:
    ifeq else_80
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc 15
    invokevirtual java/io/PrintStream/println(I)V
    goto end_if_81
else_80:
    iload 1
    ldc 16
    if_icmpeq cmp_true_86
    iconst_0
    goto cmp_end_87
cmp_true_86:
    iconst_1
cmp_end_87:
    ifeq else_84
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc 16
    invokevirtual java/io/PrintStream/println(I)V
    goto end_if_85
else_84:
    iload 1
    ldc 17
    if_icmpeq cmp_true_90
    iconst_0
    goto cmp_end_91
cmp_true_90:
    iconst_1
cmp_end_91:
    ifeq else_88
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc 17
    invokevirtual java/io/PrintStream/println(I)V
    goto end_if_89
else_88:
    iload 1
    ldc 18
    if_icmpeq cmp_true_94
    iconst_0
    goto cmp_end_95
cmp_true_94:
    iconst_1
cmp_end_95:
    ifeq else_92
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc 18
    invokevirtual java/io/PrintStream/println(I)V
    goto end_if_93
else_92:
    iload 1
    ldc 19
    if_icmpeq cmp_true_98
    iconst_0
    goto cmp_end_99
cmp_true_98:
    iconst_1
cmp_end_99:
    ifeq else_96
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc 19
    invokevirtual java/io/PrintStream/println(I)V
    goto end_if_97
else_96:
    iload 1
    ldc 20
    if_icmpeq cmp_true_102
    iconst_0
    goto cmp_end_103
cmp_true_102:
    iconst_1
cmp_end_103:
    ifeq else_100
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc 20
    invokevirtual java/io/PrintStream/println(I)V
    goto end_if_101
else_100:
    iload 1
    ldc 21
    if_icmpeq cmp_true_106
    iconst_0
    goto cmp_end_107
cmp_true_106:
    iconst_1
cmp_end_107:
    ifeq else_104
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc 21
    invokevirtual java/io/PrintStream/println(I)V
    goto end_if_105
else_104:
    iload 1
    ldc 22
    if_icmpeq cmp_true_110
    iconst_0
    goto cmp_end_111
cmp_true_110:
    iconst_1
cmp_end_111:
    ifeq else_108
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc 22
    invokevirtual java/io/PrintStream/println(I)V
    goto end_if_109
else_108:
    iload 1
    ldc 23
    if_icmpeq cmp_true_114
    iconst_0
    goto cmp_end_115
cmp_true_114:
    iconst_1
cmp_end_115:
    ifeq else_112
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc 23
    invokevirtual java/io/PrintStream/println(I)V
    goto end_if_113
else_112:
    iload 1
    ldc 24
    if_icmpeq cmp_true_118
    iconst_0
    goto cmp_end_119
cmp_true_118:
    iconst_1
cmp_end_119:
    ifeq else_116
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc 24
    invokevirtual java/io/PrintStream/println(I)V
    goto end_if_117
else_116:
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc 25
    invokevirtual java/io/PrintStream/println(I)V
end_if_117:
end_if_113:
end_if_109:
end_if_105:
end_if_101:
end_if_97:
end_if_93:
end_if_89:
end_if_85:
end_if_81:
end_if_77:
end_if_73:
end_if_69:
end_if_65:
end_if_61:
end_if_57:
end_if_53:
end_if_49:
end_if_45:
end_if_41:
end_if_37:
end_if_33:
end_if_29:
end_if_25:
end_if_21:
    return
.end method
