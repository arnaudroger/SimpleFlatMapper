// ASM: a very small and fast Java bytecode manipulation framework
// Copyright (c) 2000-2011 INRIA, France Telecom
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions
// are met:
// 1. Redistributions of source code must retain the above copyright
//    notice, this list of conditions and the following disclaimer.
// 2. Redistributions in binary form must reproduce the above copyright
//    notice, this list of conditions and the following disclaimer in the
//    documentation and/or other materials provided with the distribution.
// 3. Neither the name of the copyright holders nor the names of its
//    contributors may be used to endorse or promote products derived from
//    this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
// THE POSSIBILITY OF SUCH DAMAGE.
package org.simpleflatmapper.ow2asm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A parser to make a {@link ClassVisitor} visit a ClassFile structure, as defined in the Java
 * Virtual Machine Specification (JVMS). This class parses the ClassFile content and calls the
 * appropriate visit methods of a given {@link ClassVisitor} for each field, method and bytecode
 * instruction encountered.
 *
 * @see <a href="https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-4.html">JVMS 4</a>
 * @author Eric Bruneton
 * @author Eugene Kuleshov
 */
public class ClassReader {

  /**
   * Flag to skip the Code attributes. If this flag is set the Code attributes are neither parsed
   * nor visited.
   */
  public static final int SKIP_CODE = 1;

  /**
   * Flag to skip the SourceFile, SourceDebugExtension, LocalVariableTable, LocalVariableTypeTable
   * and LineNumberTable attributes. If this flag is set these attributes are neither parsed nor
   * visited (i.e. {@link ClassVisitor#visitSource}, {@link MethodVisitor#visitLocalVariable} and
   * {@link MethodVisitor#visitLineNumber} are not be called).
   */
  public static final int SKIP_DEBUG = 2;

  /**
   * Flag to skip the StackMap and StackMapTable attributes. If this flag is set these attributes
   * are neither parsed nor visited (i.e. {@link MethodVisitor#visitFrame} is not called). This flag
   * is useful when the {@link ClassWriter#COMPUTE_FRAMES} option is used: it avoids visiting frames
   * that will be ignored and recomputed from scratch.
   */
  public static final int SKIP_FRAMES = 4;

  /**
   * Flag to expand the stack map frames. By default stack map frames are visited in their original
   * format (i.e. "expanded" for classes whose version is less than V1_6, and "compressed" for the
   * other classes). If this flag is set, stack map frames are always visited in expanded format
   * (this option adds a decompression/recompression step in ClassReader and ClassWriter which
   * degrades performances quite a lot).
   */
  public static final int EXPAND_FRAMES = 8;

  /**
   * Flag to expand the ASM specific instructions into an equivalent sequence of standard bytecode
   * instructions. When resolving a forward jump it may happen that the signed 2 bytes offset
   * reserved for it is not sufficient to store the bytecode offset. In this case the jump
   * instruction is replaced with a temporary ASM specific instruction using an unsigned 2 bytes
   * offset (see {@link Label#resolve}). This internal flag is used to re-read classes containing
   * such instructions, in order to replace them with standard instructions. In addition, when this
   * flag is used, goto_w and jsr_w are <i>not</i> converted into goto and jsr, to make sure that
   * infinite loops where a goto_w is replaced with a goto in ClassReader and converted back to a
   * goto_w in ClassWriter cannot occur.
   */
  static final int EXPAND_ASM_INSNS = 256;

  /** The type of instructions without any argument (e.g. pop). */
  private static final int NOARG_INSN = 0;

  /** The type of instructions with a signed byte argument (e.g. bipush). */
  private static final int BYTE_INSN = 1;

  /** The type of instructions with a signed short argument (e.g. sipush). */
  private static final int SHORT_INSN = 2;

  /** The type of instructions with a local variable index argument (e.g. iload). */
  private static final int LOCAL_VARIABLE_INSN = 3;

  /** The type of instructions with an implicit local variable index argument (e.g. iload_0). */
  private static final int IMPLICIT_LOCAL_VARIABLE_INSN = 4;

  /** The type of instructions with a type descriptor argument (e.g. new). */
  private static final int TYPE_INSN = 5;

  /** The type of field and method invocations instructions (e.g. getfield). */
  private static final int FIELD_OR_METHOD_INSN = 6;

  /** The type of the invokeinterface instruction. */
  private static final int INVOKEINTERFACE_INSN = 7;

  /** The type of the invokedynamic instruction. */
  private static final int INVOKEDYNAMIC_INSN = 8;

  /** The type of instructions with a 2 bytes bytecode offset label (e.g. ifeq). */
  private static final int LABEL_INSN = 9;

  /** The type of instructions with a 4 bytes bytecode offset label (e.g. goto_w). */
  private static final int LABEL_WIDE_INSN = 10;

  /** The type of instructions with a byte constant pool argument (e.g. ldc). */
  private static final int LDC_INSN = 11;

  /** The type of instructions with a short constant pool argument (e.g. ldc_w). */
  private static final int LDC_WIDE_INSN = 12;

  /** The type of the iinc instruction. */
  private static final int IINC_INSN = 13;

  /** The type of the tableswitch instruction. */
  private static final int TABLESWITCH_INSN = 14;

  /** The type of the lookupswitch instruction. */
  private static final int LOOKUPSWITCH_INSN = 15;

  /** The type of the multianewarray instruction. */
  private static final int MULTIANEWARRAY_INSN = 16;

  /** The type of the wide instruction. */
  private static final int WIDE_INSN = 17;

  /** The type of the ASM specific instructions with an unsigned short offset. */
  private static final int ASM_LABEL_INSN = 18;

  /** The type of the ASM specific instructions with an int offset. */
  private static final int ASM_LABEL_WIDE_INSN = 19;

  /**
   * The instruction type of each JVM opcode. The instruction type of opcode 'o' is given by the
   * array element at index 'o', and is one of the *_INSN static constants above.
   *
   * @see <a href="https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html">JVMS 6</a>
   */
  private static final byte[] INSTRUCTION_TYPE = {
    NOARG_INSN, // nop = 0 (0x00)
    NOARG_INSN, // aconst_null = 1 (0x01)
    NOARG_INSN, // iconst_m1 = 2 (0x02)
    NOARG_INSN, // iconst_0 = 3 (0x03)
    NOARG_INSN, // iconst_1 = 4 (0x04)
    NOARG_INSN, // iconst_2 = 5 (0x05)
    NOARG_INSN, // iconst_3 = 6 (0x06)
    NOARG_INSN, // iconst_4 = 7 (0x07)
    NOARG_INSN, // iconst_5 = 8 (0x08)
    NOARG_INSN, // lconst_0 = 9 (0x09)
    NOARG_INSN, // lconst_1 = 10 (0x0a)
    NOARG_INSN, // fconst_0 = 11 (0x0b)
    NOARG_INSN, // fconst_1 = 12 (0x0c)
    NOARG_INSN, // fconst_2 = 13 (0x0d)
    NOARG_INSN, // dconst_0 = 14 (0x0e)
    NOARG_INSN, // dconst_1 = 15 (0x0f)
    BYTE_INSN, // bipush = 16 (0x10)
    SHORT_INSN, // sipush = 17 (0x11)
    LDC_INSN, // ldc = 18 (0x12)
    LDC_WIDE_INSN, // ldc_w = 19 (0x13)
    LDC_WIDE_INSN, // ldc2_w = 20 (0x14)
    LOCAL_VARIABLE_INSN, // iload = 21 (0x15)
    LOCAL_VARIABLE_INSN, // lload = 22 (0x16)
    LOCAL_VARIABLE_INSN, // fload = 23 (0x17)
    LOCAL_VARIABLE_INSN, // dload = 24 (0x18)
    LOCAL_VARIABLE_INSN, // aload = 25 (0x19)
    IMPLICIT_LOCAL_VARIABLE_INSN, // iload_0 = 26 (0x1a)
    IMPLICIT_LOCAL_VARIABLE_INSN, // iload_1 = 27 (0x1b)
    IMPLICIT_LOCAL_VARIABLE_INSN, // iload_2 = 28 (0x1c)
    IMPLICIT_LOCAL_VARIABLE_INSN, // iload_3 = 29 (0x1d)
    IMPLICIT_LOCAL_VARIABLE_INSN, // lload_0 = 30 (0x1e)
    IMPLICIT_LOCAL_VARIABLE_INSN, // lload_1 = 31 (0x1f)
    IMPLICIT_LOCAL_VARIABLE_INSN, // lload_2 = 32 (0x20)
    IMPLICIT_LOCAL_VARIABLE_INSN, // lload_3 = 33 (0x21)
    IMPLICIT_LOCAL_VARIABLE_INSN, // fload_0 = 34 (0x22)
    IMPLICIT_LOCAL_VARIABLE_INSN, // fload_1 = 35 (0x23)
    IMPLICIT_LOCAL_VARIABLE_INSN, // fload_2 = 36 (0x24)
    IMPLICIT_LOCAL_VARIABLE_INSN, // fload_3 = 37 (0x25)
    IMPLICIT_LOCAL_VARIABLE_INSN, // dload_0 = 38 (0x26)
    IMPLICIT_LOCAL_VARIABLE_INSN, // dload_1 = 39 (0x27)
    IMPLICIT_LOCAL_VARIABLE_INSN, // dload_2 = 40 (0x28)
    IMPLICIT_LOCAL_VARIABLE_INSN, // dload_3 = 41 (0x29)
    IMPLICIT_LOCAL_VARIABLE_INSN, // aload_0 = 42 (0x2a)
    IMPLICIT_LOCAL_VARIABLE_INSN, // aload_1 = 43 (0x2b)
    IMPLICIT_LOCAL_VARIABLE_INSN, // aload_2 = 44 (0x2c)
    IMPLICIT_LOCAL_VARIABLE_INSN, // aload_3 = 45 (0x2d)
    NOARG_INSN, // iaload = 46 (0x2e)
    NOARG_INSN, // laload = 47 (0x2f)
    NOARG_INSN, // faload = 48 (0x30)
    NOARG_INSN, // daload = 49 (0x31)
    NOARG_INSN, // aaload = 50 (0x32)
    NOARG_INSN, // baload = 51 (0x33)
    NOARG_INSN, // caload = 52 (0x34)
    NOARG_INSN, // saload = 53 (0x35)
    LOCAL_VARIABLE_INSN, // istore = 54 (0x36)
    LOCAL_VARIABLE_INSN, // lstore = 55 (0x37)
    LOCAL_VARIABLE_INSN, // fstore = 56 (0x38)
    LOCAL_VARIABLE_INSN, // dstore = 57 (0x39)
    LOCAL_VARIABLE_INSN, // astore = 58 (0x3a)
    IMPLICIT_LOCAL_VARIABLE_INSN, // istore_0 = 59 (0x3b)
    IMPLICIT_LOCAL_VARIABLE_INSN, // istore_1 = 60 (0x3c)
    IMPLICIT_LOCAL_VARIABLE_INSN, // istore_2 = 61 (0x3d)
    IMPLICIT_LOCAL_VARIABLE_INSN, // istore_3 = 62 (0x3e)
    IMPLICIT_LOCAL_VARIABLE_INSN, // lstore_0 = 63 (0x3f)
    IMPLICIT_LOCAL_VARIABLE_INSN, // lstore_1 = 64 (0x40)
    IMPLICIT_LOCAL_VARIABLE_INSN, // lstore_2 = 65 (0x41)
    IMPLICIT_LOCAL_VARIABLE_INSN, // lstore_3 = 66 (0x42)
    IMPLICIT_LOCAL_VARIABLE_INSN, // fstore_0 = 67 (0x43)
    IMPLICIT_LOCAL_VARIABLE_INSN, // fstore_1 = 68 (0x44)
    IMPLICIT_LOCAL_VARIABLE_INSN, // fstore_2 = 69 (0x45)
    IMPLICIT_LOCAL_VARIABLE_INSN, // fstore_3 = 70 (0x46)
    IMPLICIT_LOCAL_VARIABLE_INSN, // dstore_0 = 71 (0x47)
    IMPLICIT_LOCAL_VARIABLE_INSN, // dstore_1 = 72 (0x48)
    IMPLICIT_LOCAL_VARIABLE_INSN, // dstore_2 = 73 (0x49)
    IMPLICIT_LOCAL_VARIABLE_INSN, // dstore_3 = 74 (0x4a)
    IMPLICIT_LOCAL_VARIABLE_INSN, // astore_0 = 75 (0x4b)
    IMPLICIT_LOCAL_VARIABLE_INSN, // astore_1 = 76 (0x4c)
    IMPLICIT_LOCAL_VARIABLE_INSN, // astore_2 = 77 (0x4d)
    IMPLICIT_LOCAL_VARIABLE_INSN, // astore_3 = 78 (0x4e)
    NOARG_INSN, // iastore = 79 (0x4f)
    NOARG_INSN, // lastore = 80 (0x50)
    NOARG_INSN, // fastore = 81 (0x51)
    NOARG_INSN, // dastore = 82 (0x52)
    NOARG_INSN, // aastore = 83 (0x53)
    NOARG_INSN, // bastore = 84 (0x54)
    NOARG_INSN, // castore = 85 (0x55)
    NOARG_INSN, // sastore = 86 (0x56)
    NOARG_INSN, // pop = 87 (0x57)
    NOARG_INSN, // pop2 = 88 (0x58)
    NOARG_INSN, // dup = 89 (0x59)
    NOARG_INSN, // dup_x1 = 90 (0x5a)
    NOARG_INSN, // dup_x2 = 91 (0x5b)
    NOARG_INSN, // dup2 = 92 (0x5c)
    NOARG_INSN, // dup2_x1 = 93 (0x5d)
    NOARG_INSN, // dup2_x2 = 94 (0x5e)
    NOARG_INSN, // swap = 95 (0x5f)
    NOARG_INSN, // iadd = 96 (0x60)
    NOARG_INSN, // ladd = 97 (0x61)
    NOARG_INSN, // fadd = 98 (0x62)
    NOARG_INSN, // dadd = 99 (0x63)
    NOARG_INSN, // isub = 100 (0x64)
    NOARG_INSN, // lsub = 101 (0x65)
    NOARG_INSN, // fsub = 102 (0x66)
    NOARG_INSN, // dsub = 103 (0x67)
    NOARG_INSN, // imul = 104 (0x68)
    NOARG_INSN, // lmul = 105 (0x69)
    NOARG_INSN, // fmul = 106 (0x6a)
    NOARG_INSN, // dmul = 107 (0x6b)
    NOARG_INSN, // idiv = 108 (0x6c)
    NOARG_INSN, // ldiv = 109 (0x6d)
    NOARG_INSN, // fdiv = 110 (0x6e)
    NOARG_INSN, // ddiv = 111 (0x6f)
    NOARG_INSN, // irem = 112 (0x70)
    NOARG_INSN, // lrem = 113 (0x71)
    NOARG_INSN, // frem = 114 (0x72)
    NOARG_INSN, // drem = 115 (0x73)
    NOARG_INSN, // ineg = 116 (0x74)
    NOARG_INSN, // lneg = 117 (0x75)
    NOARG_INSN, // fneg = 118 (0x76)
    NOARG_INSN, // dneg = 119 (0x77)
    NOARG_INSN, // ishl = 120 (0x78)
    NOARG_INSN, // lshl = 121 (0x79)
    NOARG_INSN, // ishr = 122 (0x7a)
    NOARG_INSN, // lshr = 123 (0x7b)
    NOARG_INSN, // iushr = 124 (0x7c)
    NOARG_INSN, // lushr = 125 (0x7d)
    NOARG_INSN, // iand = 126 (0x7e)
    NOARG_INSN, // land = 127 (0x7f)
    NOARG_INSN, // ior = 128 (0x80)
    NOARG_INSN, // lor = 129 (0x81)
    NOARG_INSN, // ixor = 130 (0x82)
    NOARG_INSN, // lxor = 131 (0x83)
    IINC_INSN, // iinc = 132 (0x84)
    NOARG_INSN, // i2l = 133 (0x85)
    NOARG_INSN, // i2f = 134 (0x86)
    NOARG_INSN, // i2d = 135 (0x87)
    NOARG_INSN, // l2i = 136 (0x88)
    NOARG_INSN, // l2f = 137 (0x89)
    NOARG_INSN, // l2d = 138 (0x8a)
    NOARG_INSN, // f2i = 139 (0x8b)
    NOARG_INSN, // f2l = 140 (0x8c)
    NOARG_INSN, // f2d = 141 (0x8d)
    NOARG_INSN, // d2i = 142 (0x8e)
    NOARG_INSN, // d2l = 143 (0x8f)
    NOARG_INSN, // d2f = 144 (0x90)
    NOARG_INSN, // i2b = 145 (0x91)
    NOARG_INSN, // i2c = 146 (0x92)
    NOARG_INSN, // i2s = 147 (0x93)
    NOARG_INSN, // lcmp = 148 (0x94)
    NOARG_INSN, // fcmpl = 149 (0x95)
    NOARG_INSN, // fcmpg = 150 (0x96)
    NOARG_INSN, // dcmpl = 151 (0x97)
    NOARG_INSN, // dcmpg = 152 (0x98)
    LABEL_INSN, // ifeq = 153 (0x99)
    LABEL_INSN, // ifne = 154 (0x9a)
    LABEL_INSN, // iflt = 155 (0x9b)
    LABEL_INSN, // ifge = 156 (0x9c)
    LABEL_INSN, // ifgt = 157 (0x9d)
    LABEL_INSN, // ifle = 158 (0x9e)
    LABEL_INSN, // if_icmpeq = 159 (0x9f)
    LABEL_INSN, // if_icmpne = 160 (0xa0)
    LABEL_INSN, // if_icmplt = 161 (0xa1)
    LABEL_INSN, // if_icmpge = 162 (0xa2)
    LABEL_INSN, // if_icmpgt = 163 (0xa3)
    LABEL_INSN, // if_icmple = 164 (0xa4)
    LABEL_INSN, // if_acmpeq = 165 (0xa5)
    LABEL_INSN, // if_acmpne = 166 (0xa6)
    LABEL_INSN, // goto = 167 (0xa7)
    LABEL_INSN, // jsr = 168 (0xa8)
    LOCAL_VARIABLE_INSN, // ret = 169 (0xa9)
    TABLESWITCH_INSN, // tableswitch = 170 (0xaa)
    LOOKUPSWITCH_INSN, // lookupswitch = 171 (0xab)
    NOARG_INSN, // ireturn = 172 (0xac)
    NOARG_INSN, // lreturn = 173 (0xad)
    NOARG_INSN, // freturn = 174 (0xae)
    NOARG_INSN, // dreturn = 175 (0xaf)
    NOARG_INSN, // areturn = 176 (0xb0)
    NOARG_INSN, // return = 177 (0xb1)
    FIELD_OR_METHOD_INSN, // getstatic = 178 (0xb2)
    FIELD_OR_METHOD_INSN, // putstatic = 179 (0xb3)
    FIELD_OR_METHOD_INSN, // getfield = 180 (0xb4)
    FIELD_OR_METHOD_INSN, // putfield = 181 (0xb5)
    FIELD_OR_METHOD_INSN, // invokevirtual = 182 (0xb6)
    FIELD_OR_METHOD_INSN, // invokespecial = 183 (0xb7)
    FIELD_OR_METHOD_INSN, // invokestatic = 184 (0xb8)
    INVOKEINTERFACE_INSN, // invokeinterface = 185 (0xb9)
    INVOKEDYNAMIC_INSN, // invokedynamic = 186 (0xba)
    TYPE_INSN, // new = 187 (0xbb)
    BYTE_INSN, // newarray = 188 (0xbc)
    TYPE_INSN, // anewarray = 189 (0xbd)
    NOARG_INSN, // arraylength = 190 (0xbe)
    NOARG_INSN, // athrow = 191 (0xbf)
    TYPE_INSN, // checkcast = 192 (0xc0)
    TYPE_INSN, // instanceof = 193 (0xc1)
    NOARG_INSN, // monitorenter = 194 (0xc2)
    NOARG_INSN, // monitorexit = 195 (0xc3)
    WIDE_INSN, // wide = 196 (0xc4)
    MULTIANEWARRAY_INSN, // multianewarray = 197 (0xc5)
    LABEL_INSN, // ifnull = 198 (0xc6)
    LABEL_INSN, // ifnonnull = 199 (0xc7)
    LABEL_WIDE_INSN, // goto_w = 200 (0xc8)
    LABEL_WIDE_INSN, // jsr_w = 201 (0xc9)
    ASM_LABEL_INSN, // asm_ifeq = 202 (0xca)
    ASM_LABEL_INSN, // asm_ifne = 203 (0xcb)
    ASM_LABEL_INSN, // asm_iflt = 204 (0xcc)
    ASM_LABEL_INSN, // asm_ifge = 205 (0xcd)
    ASM_LABEL_INSN, // asm_ifgt = 206 (0xce)
    ASM_LABEL_INSN, // asm_ifle = 207 (0xcf)
    ASM_LABEL_INSN, // asm_if_icmpeq = 208 (0xd0)
    ASM_LABEL_INSN, // asm_if_icmpne = 209 (0xd1)
    ASM_LABEL_INSN, // asm_if_icmplt = 210 (0xd2)
    ASM_LABEL_INSN, // asm_if_icmpge = 211 (0xd3)
    ASM_LABEL_INSN, // asm_if_icmpgt = 212 (0xd4)
    ASM_LABEL_INSN, // asm_if_icmple = 213 (0xd5)
    ASM_LABEL_INSN, // asm_if_acmpeq = 214 (0xd6)
    ASM_LABEL_INSN, // asm_if_acmpne = 215 (0xd7)
    ASM_LABEL_INSN, // asm_goto = 216 (0xd8)
    ASM_LABEL_INSN, // asm_jsr = 217 (0xd9)
    ASM_LABEL_INSN, // asm_ifnull = 218 (0xda)
    ASM_LABEL_INSN, // asm_ifnonnull = 219 (0xdb)
    ASM_LABEL_WIDE_INSN // asm_goto_w = 220 (0xdc)
  };

  /**
   * A byte array containing the JVMS ClassFile structure to be parsed. <i>The content of this array
   * must not be modified. This field is intended for {@link Attribute} sub classes, and is normally
   * not needed by class visitors.</i>
   *
   * <p>NOTE: the ClassFile structure can start at any offset within this array, i.e. it does not
   * necessarily start at offset 0. Use {@link #getItem} and {@link #header} to get correct
   * ClassFile element offsets within this byte array.
   */
  public final byte[] b;

  /**
   * The offset in bytes, in {@link #b}, of each cp_info entry of the ClassFile's constant_pool
   * array, <i>plus one</i>. In other words, the offset of constant pool entry i is given by
   * cpInfoOffsets[i] - 1, i.e. its cp_info's tag field is given by b[cpInfoOffsets[i] - 1].
   */
  private final int[] cpInfoOffsets;

  /**
   * The String objects corresponding to the CONSTANT_Utf8 items. This cache avoids multiple parsing
   * of a given CONSTANT_Utf8 constant pool item.
   */
  private final String[] constantUtf8Values;

  /**
   * A conservative estimate of the maximum length of the strings contained in the constant pool of
   * the class.
   */
  private final int maxStringLength;

  /** The offset in bytes, in {@link #b}, of the ClassFile's access_flags field. */
  public final int header;

  // -----------------------------------------------------------------------------------------------
  // Constructors
  // -----------------------------------------------------------------------------------------------

  /**
   * Constructs a new {@link ClassReader} object.
   *
   * @param classFile the JVMS ClassFile structure to be read.
   */
  public ClassReader(final byte[] classFile) {
    this(classFile, 0, classFile.length);
  }

  /**
   * Constructs a new {@link ClassReader} object.
   *
   * @param byteBuffer a byte array containing the JVMS ClassFile structure to be read.
   * @param classFileOffset the offset in byteBuffer of the first byte of the ClassFile to be read.
   * @param classFileLength the length in bytes of the ClassFile to be read.
   */
  public ClassReader(
      final byte[] byteBuffer, final int classFileOffset, final int classFileLength) {
    this.b = byteBuffer;
    // Check the class' major_version. This field is after the magic and minor_version fields, which
    // use 4 and 2 bytes respectively.
    if (readShort(classFileOffset + 6) > Opcodes.V10) {
      throw new IllegalArgumentException();
    }
    // Create the constant pool arrays. The constant_pool_count field is after the magic,
    // minor_version and major_version fields, which use 4, 2 and 2 bytes respectively.
    int constantPoolCount = readUnsignedShort(classFileOffset + 8);
    cpInfoOffsets = new int[constantPoolCount];
    constantUtf8Values = new String[constantPoolCount];
    // Compute the offset of each constant pool entry, as well as a conservative estimate of the
    // maximum length of the constant pool strings. The first constant pool entry is after the
    // magic, minor_version, major_version and constant_pool_count fields, which use 4, 2, 2 and 2
    // bytes respectively.
    int currentCpInfoOffset = classFileOffset + 10;
    int maxStringLength = 0;
    // The offset of the other entries depend on the total size of all the previous entries.
    for (int i = 1; i < constantPoolCount; ++i) {
      cpInfoOffsets[i] = currentCpInfoOffset + 1;
      int cpInfoSize;
      switch (byteBuffer[currentCpInfoOffset]) {
        case Symbol.CONSTANT_FIELDREF_TAG:
        case Symbol.CONSTANT_METHODREF_TAG:
        case Symbol.CONSTANT_INTERFACE_METHODREF_TAG:
        case Symbol.CONSTANT_INTEGER_TAG:
        case Symbol.CONSTANT_FLOAT_TAG:
        case Symbol.CONSTANT_NAME_AND_TYPE_TAG:
        case Symbol.CONSTANT_INVOKE_DYNAMIC_TAG:
          cpInfoSize = 5;
          break;
        case Symbol.CONSTANT_LONG_TAG:
        case Symbol.CONSTANT_DOUBLE_TAG:
          cpInfoSize = 9;
          ++i;
          break;
        case Symbol.CONSTANT_UTF8_TAG:
          cpInfoSize = 3 + readUnsignedShort(currentCpInfoOffset + 1);
          if (cpInfoSize > maxStringLength) {
            // The size in bytes of this CONSTANT_Utf8 structure provides a conservative estimate
            // of the length in characters of the corresponding string, and is much cheaper to
            // compute than this exact length.
            maxStringLength = cpInfoSize;
          }
          break;
        case Symbol.CONSTANT_METHOD_HANDLE_TAG:
          cpInfoSize = 4;
          break;
        case Symbol.CONSTANT_CLASS_TAG:
        case Symbol.CONSTANT_STRING_TAG:
        case Symbol.CONSTANT_METHOD_TYPE_TAG:
        case Symbol.CONSTANT_PACKAGE_TAG:
        case Symbol.CONSTANT_MODULE_TAG:
          cpInfoSize = 3;
          break;
        default:
          throw new AssertionError();
      }
      currentCpInfoOffset += cpInfoSize;
    }
    this.maxStringLength = maxStringLength;
    // The Classfile's access_flags field is just after the last constant pool entry.
    this.header = currentCpInfoOffset;
  }

  /**
   * Constructs a new {@link ClassReader} object.
   *
   * @param inputStream an input stream of the JVMS ClassFile structure to be read. This input
   *     stream must contain nothing more than the ClassFile structure itself. It is read from its
   *     current position to its end.
   * @throws IOException if a problem occurs during reading.
   */
  public ClassReader(final InputStream inputStream) throws IOException {
    this(readStream(inputStream, false));
  }

  /**
   * Constructs a new {@link ClassReader} object.
   *
   * @param className the fully qualified name of the class to be read. The ClassFile structure is
   *     retrieved with the current class loader's {@link ClassLoader#getSystemResourceAsStream}.
   * @throws IOException if an exception occurs during reading.
   */
  public ClassReader(final String className) throws IOException {
    this(
        readStream(
            ClassLoader.getSystemResourceAsStream(className.replace('.', '/') + ".class"), true));
  }

  /**
   * Reads the given input stream and returns its content as a byte array.
   *
   * @param inputStream an input stream.
   * @param close true to close the input stream after reading.
   * @return the content of the given input stream.
   * @throws IOException if a problem occurs during reading.
   */
  private static byte[] readStream(final InputStream inputStream, boolean close)
      throws IOException {
    if (inputStream == null) {
      throw new IOException("Class not found");
    }
    try {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      byte[] data = new byte[inputStream.available()];
      int bytesRead;
      while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
        outputStream.write(data, 0, bytesRead);
      }
      outputStream.flush();
      return outputStream.toByteArray();
    } finally {
      if (close) {
        inputStream.close();
      }
    }
  }

  // -----------------------------------------------------------------------------------------------
  // Accessors
  // -----------------------------------------------------------------------------------------------

  /**
   * Returns the class's access flags (see {@link Opcodes}). This value may not reflect Deprecated
   * and Synthetic flags when bytecode is before 1.5 and those flags are represented by attributes.
   *
   * @return the class access flags.
   * @see ClassVisitor#visit(int, int, String, String, String, String[])
   */
  public int getAccess() {
    return readUnsignedShort(header);
  }

  /**
   * Returns the internal name of the class (see {@link Type#getInternalName()}).
   *
   * @return the internal class name.
   * @see ClassVisitor#visit(int, int, String, String, String, String[])
   */
  public String getClassName() {
    // this_class is just after the access_flags field (using 2 bytes).
    return readClass(header + 2, new char[maxStringLength]);
  }

  /**
   * Returns the internal of name of the super class (see {@link Type#getInternalName()}). For
   * interfaces, the super class is {@link Object}.
   *
   * @return the internal name of the super class, or <tt>null</tt> for {@link Object} class.
   * @see ClassVisitor#visit(int, int, String, String, String, String[])
   */
  public String getSuperName() {
    // super_class is after the access_flags and this_class fields (2 bytes each).
    return readClass(header + 4, new char[maxStringLength]);
  }

  /**
   * Returns the internal names of the implemented interfaces (see {@link Type#getInternalName()}).
   *
   * @return the internal names of the directly implemented interfaces. Inherited implemented
   *     interfaces are not returned.
   * @see ClassVisitor#visit(int, int, String, String, String, String[])
   */
  public String[] getInterfaces() {
    // interfaces_count is after the access_flags, this_class and super_class fields (2 bytes each).
    int currentOffset = header + 6;
    int interfacesCount = readUnsignedShort(currentOffset);
    String[] interfaces = new String[interfacesCount];
    if (interfacesCount > 0) {
      char[] charBuffer = new char[maxStringLength];
      for (int i = 0; i < interfacesCount; ++i) {
        currentOffset += 2;
        interfaces[i] = readClass(currentOffset, charBuffer);
      }
    }
    return interfaces;
  }

  // -----------------------------------------------------------------------------------------------
  // Public methods
  // -----------------------------------------------------------------------------------------------

  /**
   * Makes the given visitor visit the JVMS ClassFile structure passed to the constructor of this
   * {@link ClassReader}.
   *
   * @param classVisitor the visitor that must visit this class.
   * @param parsingOptions the options to use to parse this class. One or more of {@link
   *     #SKIP_CODE}, {@link #SKIP_DEBUG}, {@link #SKIP_FRAMES} or {@link #EXPAND_FRAMES}.
   */
  public void accept(final ClassVisitor classVisitor, final int parsingOptions) {
    accept(classVisitor, new Attribute[0], parsingOptions);
  }

  /**
   * Makes the given visitor visit the JVMS ClassFile structure passed to the constructor of this
   * {@link ClassReader}.
   *
   * @param classVisitor the visitor that must visit this class.
   * @param attributePrototypes prototypes of the attributes that must be parsed during the visit of
   *     the class. Any attribute whose type is not equal to the type of one the prototypes will not
   *     be parsed: its byte array value will be passed unchanged to the ClassWriter. <i>This may
   *     corrupt it if this value contains references to the constant pool, or has syntactic or
   *     semantic links with a class element that has been transformed by a class adapter between
   *     the reader and the writer</i>.
   * @param parsingOptions the options to use to parse this class. One or more of {@link
   *     #SKIP_CODE}, {@link #SKIP_DEBUG}, {@link #SKIP_FRAMES} or {@link #EXPAND_FRAMES}.
   */
  public void accept(
      final ClassVisitor classVisitor,
      final Attribute[] attributePrototypes,
      final int parsingOptions) {
    Context context = new Context();
    context.attributePrototypes = attributePrototypes;
    context.parsingOptions = parsingOptions;
    context.charBuffer = new char[maxStringLength];

    // Read the access_flags, this_class, super_class, interface_count and interfaces fields.
    char[] charBuffer = context.charBuffer;
    int currentOffset = header;
    int accessFlags = readUnsignedShort(currentOffset);
    String thisClass = readClass(currentOffset + 2, charBuffer);
    String superClass = readClass(currentOffset + 4, charBuffer);
    String[] interfaces = new String[readUnsignedShort(currentOffset + 6)];
    currentOffset += 8;
    for (int i = 0; i < interfaces.length; ++i) {
      interfaces[i] = readClass(currentOffset, charBuffer);
      currentOffset += 2;
    }

    // Read the class attributes (the variables are ordered as in Section 4.7 of the JVMS).
    // Attribute offsets exclude the attribute_name_index and attribute_length fields.
    // - The offset of the InnerClasses attribute, or 0.
    int innerClassesOffset = 0;
    // - The offset of the EnclosingMethod attribute, or 0.
    int enclosingMethodOffset = 0;
    // - The string corresponding to the Signature attribute, or null.
    String signature = null;
    // - The string corresponding to the SourceFile attribute, or null.
    String sourceFile = null;
    // - The string corresponding to the SourceDebugExtension attribute, or null.
    String sourceDebugExtension = null;
    // - The offset of the RuntimeVisibleAnnotations attribute, or 0.
    int runtimeVisibleAnnotationsOffset = 0;
    // - The offset of the RuntimeInvisibleAnnotations attribute, or 0.
    int runtimeInvisibleAnnotationsOffset = 0;
    // - The offset of the RuntimeVisibleTypeAnnotations attribute, or 0.
    int runtimeVisibleTypeAnnotationsOffset = 0;
    // - The offset of the RuntimeInvisibleTypeAnnotations attribute, or 0.
    int runtimeInvisibleTypeAnnotationsOffset = 0;
    // - The offset of the Module attribute, or 0.
    int moduleOffset = 0;
    // - The offset of the ModulePackages attribute, or 0.
    int modulePackagesOffset = 0;
    // - The string corresponding to the ModuleMainClass attribute, or null.
    String moduleMainClass = null;
    // - The non standard attributes (linked with their {@link Attribute#nextAttribute} field).
    //   This list in the <i>reverse order</i> or their order in the ClassFile structure.
    Attribute attributes = null;

    int currentAttributeOffset = getFirstAttributeOffset();
    for (int i = readUnsignedShort(currentAttributeOffset - 2); i > 0; --i) {
      // Read the attribute_info's attribute_name and attribute_length fields.
      String attributeName = readUTF8(currentAttributeOffset, charBuffer);
      int attributeLength = readInt(currentAttributeOffset + 2);
      currentAttributeOffset += 6;
      // The tests are sorted in decreasing frequency order (based on frequencies observed on
      // typical classes).
      if ("SourceFile".equals(attributeName)) {
        sourceFile = readUTF8(currentAttributeOffset, charBuffer);
      } else if ("InnerClasses".equals(attributeName)) {
        innerClassesOffset = currentAttributeOffset;
      } else if ("EnclosingMethod".equals(attributeName)) {
        enclosingMethodOffset = currentAttributeOffset;
      } else if ("Signature".equals(attributeName)) {
        signature = readUTF8(currentAttributeOffset, charBuffer);
      } else if ("RuntimeVisibleAnnotations".equals(attributeName)) {
        runtimeVisibleAnnotationsOffset = currentAttributeOffset;
      } else if ("RuntimeVisibleTypeAnnotations".equals(attributeName)) {
        runtimeVisibleTypeAnnotationsOffset = currentAttributeOffset;
      } else if ("Deprecated".equals(attributeName)) {
        accessFlags |= Opcodes.ACC_DEPRECATED;
      } else if ("Synthetic".equals(attributeName)) {
        accessFlags |= Opcodes.ACC_SYNTHETIC;
      } else if ("SourceDebugExtension".equals(attributeName)) {
        sourceDebugExtension =
            readUTF(currentAttributeOffset, attributeLength, new char[attributeLength]);
      } else if ("RuntimeInvisibleAnnotations".equals(attributeName)) {
        runtimeInvisibleAnnotationsOffset = currentAttributeOffset;
      } else if ("RuntimeInvisibleTypeAnnotations".equals(attributeName)) {
        runtimeInvisibleTypeAnnotationsOffset = currentAttributeOffset;
      } else if ("Module".equals(attributeName)) {
        moduleOffset = currentAttributeOffset;
      } else if ("ModuleMainClass".equals(attributeName)) {
        moduleMainClass = readClass(currentAttributeOffset, charBuffer);
      } else if ("ModulePackages".equals(attributeName)) {
        modulePackagesOffset = currentAttributeOffset;
      } else if ("BootstrapMethods".equals(attributeName)) {
        // Read the num_bootstrap_methods field and create an array of this size.
        int[] bootstrapMethodOffsets = new int[readUnsignedShort(currentAttributeOffset)];
        // Compute and store the offset of each 'bootstrap_methods' array field entry.
        int currentBootstrapMethodOffset = currentAttributeOffset + 2;
        for (int j = 0; j < bootstrapMethodOffsets.length; ++j) {
          bootstrapMethodOffsets[j] = currentBootstrapMethodOffset;
          // Skip the bootstrap_method_ref and num_bootstrap_arguments fields (2 bytes each),
          // as well as the bootstrap_arguments array field (of size num_bootstrap_arguments * 2).
          currentBootstrapMethodOffset +=
              4 + readUnsignedShort(currentBootstrapMethodOffset + 2) * 2;
        }
        context.bootstrapMethodOffsets = bootstrapMethodOffsets;
      } else {
        Attribute attribute =
            readAttribute(
                attributePrototypes,
                attributeName,
                currentAttributeOffset,
                attributeLength,
                charBuffer,
                -1,
                null);
        attribute.nextAttribute = attributes;
        attributes = attribute;
      }
      currentAttributeOffset += attributeLength;
    }

    // Visit the class declaration. The minor_version and major_version fields start 6 bytes before
    // the first constant pool entry, which itself starts at cpInfoOffsets[1] - 1 (by definition).
    classVisitor.visit(
        readInt(cpInfoOffsets[1] - 7), accessFlags, thisClass, signature, superClass, interfaces);

    // Visit the SourceFile and SourceDebugExtenstion attributes.
    if ((parsingOptions & SKIP_DEBUG) == 0
        && (sourceFile != null || sourceDebugExtension != null)) {
      classVisitor.visitSource(sourceFile, sourceDebugExtension);
    }

    // Visit the Module, ModulePackages and ModuleMainClass attributes.
    if (moduleOffset != 0) {
      readModule(classVisitor, context, moduleOffset, modulePackagesOffset, moduleMainClass);
    }

    // Visit the EnclosingMethod attribute.
    if (enclosingMethodOffset != 0) {
      String className = readClass(enclosingMethodOffset, charBuffer);
      int methodIndex = readUnsignedShort(enclosingMethodOffset + 2);
      String name = methodIndex == 0 ? null : readUTF8(cpInfoOffsets[methodIndex], charBuffer);
      String type = methodIndex == 0 ? null : readUTF8(cpInfoOffsets[methodIndex] + 2, charBuffer);
      classVisitor.visitOuterClass(className, name, type);
    }

    // Visit the RuntimeVisibleAnnotations attribute.
    if (runtimeVisibleAnnotationsOffset != 0) {
      int numAnnotations = readUnsignedShort(runtimeVisibleAnnotationsOffset);
      int currentAnnotationOffset = runtimeVisibleAnnotationsOffset + 2;
      while (numAnnotations-- > 0) {
        // Parse the type_index field.
        String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
        currentAnnotationOffset += 2;
        // Parse num_element_value_pairs and element_value_pairs and visit these values.
        currentAnnotationOffset =
            readElementValues(
                classVisitor.visitAnnotation(annotationDescriptor, /* visible = */ true),
                currentAnnotationOffset,
                /* named = */ true,
                charBuffer);
      }
    }

    // Visit the RuntimeInvisibleAnnotations attribute.
    if (runtimeInvisibleAnnotationsOffset != 0) {
      int numAnnotations = readUnsignedShort(runtimeInvisibleAnnotationsOffset);
      int currentAnnotationOffset = runtimeInvisibleAnnotationsOffset + 2;
      while (numAnnotations-- > 0) {
        // Parse the type_index field.
        String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
        currentAnnotationOffset += 2;
        // Parse num_element_value_pairs and element_value_pairs and visit these values.
        currentAnnotationOffset =
            readElementValues(
                classVisitor.visitAnnotation(annotationDescriptor, /* visible = */ false),
                currentAnnotationOffset,
                /* named = */ true,
                charBuffer);
      }
    }

    // Visit the RuntimeVisibleTypeAnnotations attribute.
    if (runtimeVisibleTypeAnnotationsOffset != 0) {
      int numAnnotations = readUnsignedShort(runtimeVisibleTypeAnnotationsOffset);
      int currentAnnotationOffset = runtimeVisibleTypeAnnotationsOffset + 2;
      while (numAnnotations-- > 0) {
        // Parse the target_type, target_info and target_path fields.
        currentAnnotationOffset = readTypeAnnotationTarget(context, currentAnnotationOffset);
        // Parse the type_index field.
        String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
        currentAnnotationOffset += 2;
        // Parse num_element_value_pairs and element_value_pairs and visit these values.
        currentAnnotationOffset =
            readElementValues(
                classVisitor.visitTypeAnnotation(
                    context.currentTypeAnnotationTarget,
                    context.currentTypeAnnotationTargetPath,
                    annotationDescriptor,
                    /* visible = */ true),
                currentAnnotationOffset,
                /* named = */ true,
                charBuffer);
      }
    }

    // Visit the RuntimeInvisibleTypeAnnotations attribute.
    if (runtimeInvisibleTypeAnnotationsOffset != 0) {
      int numAnnotations = readUnsignedShort(runtimeInvisibleTypeAnnotationsOffset);
      int currentAnnotationOffset = runtimeInvisibleTypeAnnotationsOffset + 2;
      while (numAnnotations-- > 0) {
        // Parse the target_type, target_info and target_path fields.
        currentAnnotationOffset = readTypeAnnotationTarget(context, currentAnnotationOffset);
        // Parse the type_index field.
        String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
        currentAnnotationOffset += 2;
        // Parse num_element_value_pairs and element_value_pairs and visit these values.
        currentAnnotationOffset =
            readElementValues(
                classVisitor.visitTypeAnnotation(
                    context.currentTypeAnnotationTarget,
                    context.currentTypeAnnotationTargetPath,
                    annotationDescriptor,
                    /* visible = */ false),
                currentAnnotationOffset,
                /* named = */ true,
                charBuffer);
      }
    }

    // Visit the non standard attributes.
    while (attributes != null) {
      // Copy and reset the nextAttribute field so that it can also be used in ClassWriter.
      Attribute nextAttribute = attributes.nextAttribute;
      attributes.nextAttribute = null;
      classVisitor.visitAttribute(attributes);
      attributes = nextAttribute;
    }

    // Visit the InnerClasses attribute.
    if (innerClassesOffset != 0) {
      int numberOfClasses = readUnsignedShort(innerClassesOffset);
      int currentClassesOffset = innerClassesOffset + 2;
      while (numberOfClasses-- > 0) {
        classVisitor.visitInnerClass(
            readClass(currentClassesOffset, charBuffer),
            readClass(currentClassesOffset + 2, charBuffer),
            readUTF8(currentClassesOffset + 4, charBuffer),
            readUnsignedShort(currentClassesOffset + 6));
        currentClassesOffset += 8;
      }
    }

    // Visit the fields and methods.
    int fieldsCount = readUnsignedShort(currentOffset);
    currentOffset += 2;
    while (fieldsCount-- > 0) {
      currentOffset = readField(classVisitor, context, currentOffset);
    }
    int methodsCount = readUnsignedShort(currentOffset);
    currentOffset += 2;
    while (methodsCount-- > 0) {
      currentOffset = readMethod(classVisitor, context, currentOffset);
    }

    // Visit the end of the class.
    classVisitor.visitEnd();
  }

  // ----------------------------------------------------------------------------------------------
  // Methods to parse modules, fields and methods
  // ----------------------------------------------------------------------------------------------

  /**
   * Reads the module attribute and visit it.
   *
   * @param classVisitor the current class visitor
   * @param context information about the class being parsed.
   * @param moduleOffset the offset of the Module attribute (excluding the attribute_info's
   *     attribute_name_index and attribute_length fields).
   * @param modulePackagesOffset the offset of the ModulePackages attribute (excluding the
   *     attribute_info's attribute_name_index and attribute_length fields), or 0.
   * @param moduleMainClass the string corresponding to the ModuleMainClass attribute, or null.
   */
  private void readModule(
      final ClassVisitor classVisitor,
      final Context context,
      final int moduleOffset,
      final int modulePackagesOffset,
      final String moduleMainClass) {
    char[] buffer = context.charBuffer;

    // Read the module_name_index, module_flags and module_version_index fields and visit them.
    int currentOffset = moduleOffset;
    String moduleName = readModule(currentOffset, buffer);
    int moduleFlags = readUnsignedShort(currentOffset + 2);
    String moduleVersion = readUTF8(currentOffset + 4, buffer);
    currentOffset += 6;
    ModuleVisitor moduleVisitor = classVisitor.visitModule(moduleName, moduleFlags, moduleVersion);
    if (moduleVisitor == null) {
      return;
    }

    // Visit the ModuleMainClass attribute.
    if (moduleMainClass != null) {
      moduleVisitor.visitMainClass(moduleMainClass);
    }

    // Visit the ModulePackages attribute.
    if (modulePackagesOffset != 0) {
      int packageCount = readUnsignedShort(modulePackagesOffset);
      int currentPackageOffset = modulePackagesOffset + 2;
      while (packageCount-- > 0) {
        moduleVisitor.visitPackage(readPackage(currentPackageOffset, buffer));
        currentPackageOffset += 2;
      }
    }

    // Read the 'requires_count' and 'requires' fields.
    int requiresCount = readUnsignedShort(currentOffset);
    currentOffset += 2;
    while (requiresCount-- > 0) {
      // Read the requires_index, requires_flags and requires_version fields and visit them.
      String requires = readModule(currentOffset, buffer);
      int requiresFlags = readUnsignedShort(currentOffset + 2);
      String requiresVersion = readUTF8(currentOffset + 4, buffer);
      currentOffset += 6;
      moduleVisitor.visitRequire(requires, requiresFlags, requiresVersion);
    }

    // Read the 'exports_count' and 'exports' fields.
    int exportsCount = readUnsignedShort(currentOffset);
    currentOffset += 2;
    while (exportsCount-- > 0) {
      // Read the exports_index, exports_flags, exports_to_count and exports_to_index fields
      // and visit them.
      String exports = readPackage(currentOffset, buffer);
      int exportsFlags = readUnsignedShort(currentOffset + 2);
      int exportsToCount = readUnsignedShort(currentOffset + 4);
      currentOffset += 6;
      String[] exportsTo = null;
      if (exportsToCount != 0) {
        exportsTo = new String[exportsToCount];
        for (int i = 0; i < exportsToCount; ++i) {
          exportsTo[i] = readModule(currentOffset, buffer);
          currentOffset += 2;
        }
      }
      moduleVisitor.visitExport(exports, exportsFlags, exportsTo);
    }

    // Reads the 'opens_count' and 'opens' fields.
    int opensCount = readUnsignedShort(currentOffset);
    currentOffset += 2;
    while (opensCount-- > 0) {
      // Read the opens_index, opens_flags, opens_to_count and opens_to_index fields and visit them.
      String opens = readPackage(currentOffset, buffer);
      int opensFlags = readUnsignedShort(currentOffset + 2);
      int opensToCount = readUnsignedShort(currentOffset + 4);
      currentOffset += 6;
      String[] opensTo = null;
      if (opensToCount != 0) {
        opensTo = new String[opensToCount];
        for (int i = 0; i < opensToCount; ++i) {
          opensTo[i] = readModule(currentOffset, buffer);
          currentOffset += 2;
        }
      }
      moduleVisitor.visitOpen(opens, opensFlags, opensTo);
    }

    // Read the 'uses_count' and 'uses' fields.
    int usesCount = readUnsignedShort(currentOffset);
    currentOffset += 2;
    while (usesCount-- > 0) {
      moduleVisitor.visitUse(readClass(currentOffset, buffer));
      currentOffset += 2;
    }

    // Read the  'provides_count' and 'provides' fields.
    int providesCount = readUnsignedShort(currentOffset);
    currentOffset += 2;
    while (providesCount-- > 0) {
      // Read the provides_index, provides_with_count and provides_with_index fields and visit them.
      String provides = readClass(currentOffset, buffer);
      int providesWithCount = readUnsignedShort(currentOffset + 2);
      currentOffset += 4;
      String[] providesWith = new String[providesWithCount];
      for (int i = 0; i < providesWithCount; ++i) {
        providesWith[i] = readClass(currentOffset, buffer);
        currentOffset += 2;
      }
      moduleVisitor.visitProvide(provides, providesWith);
    }

    // Visit the end of the module attributes.
    moduleVisitor.visitEnd();
  }

  /**
   * Reads a JVMS field_info structure and makes the given visitor visit it.
   *
   * @param classVisitor the visitor that must visit the field.
   * @param context information about the class being parsed.
   * @param fieldInfoOffset the start offset of the field_info structure.
   * @return the offset of the first byte following the field_info structure.
   */
  private int readField(
      final ClassVisitor classVisitor, final Context context, int fieldInfoOffset) {
    char[] charBuffer = context.charBuffer;

    // Read the access_flags, name_index and descriptor_index fields.
    int currentOffset = fieldInfoOffset;
    int accessFlags = readUnsignedShort(currentOffset);
    String name = readUTF8(currentOffset + 2, charBuffer);
    String descriptor = readUTF8(currentOffset + 4, charBuffer);
    currentOffset += 6;

    // Read the field attributes (the variables are ordered as in Section 4.7 of the JVMS).
    // Attribute offsets exclude the attribute_name_index and attribute_length fields.
    // - The value corresponding to the ConstantValue attribute, or null.
    Object constantValue = null;
    // - The string corresponding to the Signature attribute, or null.
    String signature = null;
    // - The offset of the RuntimeVisibleAnnotations attribute, or 0.
    int runtimeVisibleAnnotationsOffset = 0;
    // - The offset of the RuntimeInvisibleAnnotations attribute, or 0.
    int runtimeInvisibleAnnotationsOffset = 0;
    // - The offset of the RuntimeVisibleTypeAnnotations attribute, or 0.
    int runtimeVisibleTypeAnnotationsOffset = 0;
    // - The offset of the RuntimeInvisibleTypeAnnotations attribute, or 0.
    int runtimeInvisibleTypeAnnotationsOffset = 0;
    // - The non standard attributes (linked with their {@link Attribute#nextAttribute} field).
    //   This list in the <i>reverse order</i> or their order in the ClassFile structure.
    Attribute attributes = null;

    int attributesCount = readUnsignedShort(currentOffset);
    currentOffset += 2;
    while (attributesCount-- > 0) {
      // Read the attribute_info's attribute_name and attribute_length fields.
      String attributeName = readUTF8(currentOffset, charBuffer);
      int attributeLength = readInt(currentOffset + 2);
      currentOffset += 6;
      // The tests are sorted in decreasing frequency order (based on frequencies observed on
      // typical classes).
      if ("ConstantValue".equals(attributeName)) {
        int constantvalueIndex = readUnsignedShort(currentOffset);
        constantValue = constantvalueIndex == 0 ? null : readConst(constantvalueIndex, charBuffer);
      } else if ("Signature".equals(attributeName)) {
        signature = readUTF8(currentOffset, charBuffer);
      } else if ("Deprecated".equals(attributeName)) {
        accessFlags |= Opcodes.ACC_DEPRECATED;
      } else if ("Synthetic".equals(attributeName)) {
        accessFlags |= Opcodes.ACC_SYNTHETIC;
      } else if ("RuntimeVisibleAnnotations".equals(attributeName)) {
        runtimeVisibleAnnotationsOffset = currentOffset;
      } else if ("RuntimeVisibleTypeAnnotations".equals(attributeName)) {
        runtimeVisibleTypeAnnotationsOffset = currentOffset;
      } else if ("RuntimeInvisibleAnnotations".equals(attributeName)) {
        runtimeInvisibleAnnotationsOffset = currentOffset;
      } else if ("RuntimeInvisibleTypeAnnotations".equals(attributeName)) {
        runtimeInvisibleTypeAnnotationsOffset = currentOffset;
      } else {
        Attribute attribute =
            readAttribute(
                context.attributePrototypes,
                attributeName,
                currentOffset,
                attributeLength,
                charBuffer,
                -1,
                null);
        attribute.nextAttribute = attributes;
        attributes = attribute;
      }
      currentOffset += attributeLength;
    }

    // Visit the field declaration.
    FieldVisitor fieldVisitor =
        classVisitor.visitField(accessFlags, name, descriptor, signature, constantValue);
    if (fieldVisitor == null) {
      return currentOffset;
    }

    // Visit the RuntimeVisibleAnnotations attribute.
    if (runtimeVisibleAnnotationsOffset != 0) {
      int numAnnotations = readUnsignedShort(runtimeVisibleAnnotationsOffset);
      int currentAnnotationOffset = runtimeVisibleAnnotationsOffset + 2;
      while (numAnnotations-- > 0) {
        // Parse the type_index field.
        String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
        currentAnnotationOffset += 2;
        // Parse num_element_value_pairs and element_value_pairs and visit these values.
        currentAnnotationOffset =
            readElementValues(
                fieldVisitor.visitAnnotation(annotationDescriptor, /* visible = */ true),
                currentAnnotationOffset,
                /* named = */ true,
                charBuffer);
      }
    }

    // Visit the RuntimeInvisibleAnnotations attribute.
    if (runtimeInvisibleAnnotationsOffset != 0) {
      int numAnnotations = readUnsignedShort(runtimeInvisibleAnnotationsOffset);
      int currentAnnotationOffset = runtimeInvisibleAnnotationsOffset + 2;
      while (numAnnotations-- > 0) {
        // Parse the type_index field.
        String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
        currentAnnotationOffset += 2;
        // Parse num_element_value_pairs and element_value_pairs and visit these values.
        currentAnnotationOffset =
            readElementValues(
                fieldVisitor.visitAnnotation(annotationDescriptor, /* visible = */ false),
                currentAnnotationOffset,
                /* named = */ true,
                charBuffer);
      }
    }

    // Visit the RuntimeVisibleTypeAnnotations attribute.
    if (runtimeVisibleTypeAnnotationsOffset != 0) {
      int numAnnotations = readUnsignedShort(runtimeVisibleTypeAnnotationsOffset);
      int currentAnnotationOffset = runtimeVisibleTypeAnnotationsOffset + 2;
      while (numAnnotations-- > 0) {
        // Parse the target_type, target_info and target_path fields.
        currentAnnotationOffset = readTypeAnnotationTarget(context, currentAnnotationOffset);
        // Parse the type_index field.
        String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
        currentAnnotationOffset += 2;
        // Parse num_element_value_pairs and element_value_pairs and visit these values.
        currentAnnotationOffset =
            readElementValues(
                fieldVisitor.visitTypeAnnotation(
                    context.currentTypeAnnotationTarget,
                    context.currentTypeAnnotationTargetPath,
                    annotationDescriptor,
                    /* visible = */ true),
                currentAnnotationOffset,
                /* named = */ true,
                charBuffer);
      }
    }

    // Visit the RuntimeInvisibleTypeAnnotations attribute.
    if (runtimeInvisibleTypeAnnotationsOffset != 0) {
      int numAnnotations = readUnsignedShort(runtimeInvisibleTypeAnnotationsOffset);
      int currentAnnotationOffset = runtimeInvisibleTypeAnnotationsOffset + 2;
      while (numAnnotations-- > 0) {
        // Parse the target_type, target_info and target_path fields.
        currentAnnotationOffset = readTypeAnnotationTarget(context, currentAnnotationOffset);
        // Parse the type_index field.
        String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
        currentAnnotationOffset += 2;
        // Parse num_element_value_pairs and element_value_pairs and visit these values.
        currentAnnotationOffset =
            readElementValues(
                fieldVisitor.visitTypeAnnotation(
                    context.currentTypeAnnotationTarget,
                    context.currentTypeAnnotationTargetPath,
                    annotationDescriptor,
                    /* visible = */ false),
                currentAnnotationOffset,
                /* named = */ true,
                charBuffer);
      }
    }

    // Visit the non standard attributes.
    while (attributes != null) {
      // Copy and reset the nextAttribute field so that it can also be used in FieldWriter.
      Attribute nextAttribute = attributes.nextAttribute;
      attributes.nextAttribute = null;
      fieldVisitor.visitAttribute(attributes);
      attributes = nextAttribute;
    }

    // Visit the end of the field.
    fieldVisitor.visitEnd();
    return currentOffset;
  }

  /**
   * Reads a JVMS method_info structure and makes the given visitor visit it.
   *
   * @param classVisitor the visitor that must visit the method.
   * @param context information about the class being parsed.
   * @param methodInfoOffset the start offset of the method_info structure.
   * @return the offset of the first byte following the method_info structure.
   */
  private int readMethod(
      final ClassVisitor classVisitor, final Context context, final int methodInfoOffset) {
    char[] charBuffer = context.charBuffer;

    // Read the access_flags, name_index and descriptor_index fields.
    int currentOffset = methodInfoOffset;
    context.currentMethodAccessFlags = readUnsignedShort(currentOffset);
    context.currentMethodName = readUTF8(currentOffset + 2, charBuffer);
    context.currentMethodDescriptor = readUTF8(currentOffset + 4, charBuffer);
    currentOffset += 6;

    // Read the method attributes (the variables are ordered as in Section 4.7 of the JVMS).
    // Attribute offsets exclude the attribute_name_index and attribute_length fields.
    // - The offset of the Code attribute, or 0.
    int codeOffset = 0;
    // - The offset of the Exceptions attribute, or 0.
    int exceptionsOffset = 0;
    // - The strings corresponding to the Exceptions attribute, or null.
    String[] exceptions = null;
    // - The string corresponding to the Signature attribute, or null.
    int signature = 0;
    // - The offset of the RuntimeVisibleAnnotations attribute, or 0.
    int runtimeVisibleAnnotationsOffset = 0;
    // - The offset of the RuntimeInvisibleAnnotations attribute, or 0.
    int runtimeInvisibleAnnotationsOffset = 0;
    // - The offset of the RuntimeVisibleParameterAnnotations attribute, or 0.
    int runtimeVisibleParameterAnnotationsOffset = 0;
    // - The offset of the RuntimeInvisibleParameterAnnotations attribute, or 0.
    int runtimeInvisibleParameterAnnotationsOffset = 0;
    // - The offset of the RuntimeVisibleTypeAnnotations attribute, or 0.
    int runtimeVisibleTypeAnnotationsOffset = 0;
    // - The offset of the RuntimeInvisibleTypeAnnotations attribute, or 0.
    int runtimeInvisibleTypeAnnotationsOffset = 0;
    // - The offset of the AnnotationDefault attribute, or 0.
    int annotationDefaultOffset = 0;
    // - The offset of the MethodParameters attribute, or 0.
    int methodParametersOffset = 0;
    // - The non standard attributes (linked with their {@link Attribute#nextAttribute} field).
    //   This list in the <i>reverse order</i> or their order in the ClassFile structure.
    Attribute attributes = null;

    int attributesCount = readUnsignedShort(currentOffset);
    currentOffset += 2;
    while (attributesCount-- > 0) {
      // Read the attribute_info's attribute_name and attribute_length fields.
      String attributeName = readUTF8(currentOffset, charBuffer);
      int attributeLength = readInt(currentOffset + 2);
      currentOffset += 6;
      // The tests are sorted in decreasing frequency order (based on frequencies observed on
      // typical classes).
      if ("Code".equals(attributeName) && (context.parsingOptions & SKIP_CODE) == 0) {
        codeOffset = currentOffset;
      } else if ("Exceptions".equals(attributeName)) {
        exceptionsOffset = currentOffset;
        exceptions = new String[readUnsignedShort(exceptionsOffset)];
        int currentExceptionOffset = exceptionsOffset + 2;
        for (int i = 0; i < exceptions.length; ++i) {
          exceptions[i] = readClass(currentExceptionOffset, charBuffer);
          currentExceptionOffset += 2;
        }
      } else if ("Signature".equals(attributeName)) {
        signature = readUnsignedShort(currentOffset);
      } else if ("Deprecated".equals(attributeName)) {
        context.currentMethodAccessFlags |= Opcodes.ACC_DEPRECATED;
      } else if ("RuntimeVisibleAnnotations".equals(attributeName)) {
        runtimeVisibleAnnotationsOffset = currentOffset;
      } else if ("RuntimeVisibleTypeAnnotations".equals(attributeName)) {
        runtimeVisibleTypeAnnotationsOffset = currentOffset;
      } else if ("AnnotationDefault".equals(attributeName)) {
        annotationDefaultOffset = currentOffset;
      } else if ("Synthetic".equals(attributeName)) {
        context.currentMethodAccessFlags |= Opcodes.ACC_SYNTHETIC;
      } else if ("RuntimeInvisibleAnnotations".equals(attributeName)) {
        runtimeInvisibleAnnotationsOffset = currentOffset;
      } else if ("RuntimeInvisibleTypeAnnotations".equals(attributeName)) {
        runtimeInvisibleTypeAnnotationsOffset = currentOffset;
      } else if ("RuntimeVisibleParameterAnnotations".equals(attributeName)) {
        runtimeVisibleParameterAnnotationsOffset = currentOffset;
      } else if ("RuntimeInvisibleParameterAnnotations".equals(attributeName)) {
        runtimeInvisibleParameterAnnotationsOffset = currentOffset;
      } else if ("MethodParameters".equals(attributeName)) {
        methodParametersOffset = currentOffset;
      } else {
        Attribute attribute =
            readAttribute(
                context.attributePrototypes,
                attributeName,
                currentOffset,
                attributeLength,
                charBuffer,
                -1,
                null);
        attribute.nextAttribute = attributes;
        attributes = attribute;
      }
      currentOffset += attributeLength;
    }

    // Visit the method declaration.
    MethodVisitor methodVisitor =
        classVisitor.visitMethod(
            context.currentMethodAccessFlags,
            context.currentMethodName,
            context.currentMethodDescriptor,
            signature == 0 ? null : readUTF(signature, charBuffer),
            exceptions);
    if (methodVisitor == null) {
      return currentOffset;
    }

    // If the returned MethodVisitor is in fact a MethodWriter, it means there is no method
    // adapter between the reader and the writer. If, in addition, the writer's constant pool was
    // copied from this reader (mw.getSource() == this), and the signature and exceptions of the
    // method have not been changed, then it is possible to skip all visit events and just copy the
    // original code of the method to the writer (the access_flags, name_index and descriptor_index
    // can have been changed, this is not important since they are not copied from the reader).
    if (methodVisitor instanceof MethodWriter) {
      MethodWriter methodWriter = (MethodWriter) methodVisitor;
      if (methodWriter.getSource() == this && signature == methodWriter.signatureIndex) {
        boolean sameExceptions = false;
        if (exceptions == null) {
          sameExceptions = methodWriter.numberOfExceptions == 0;
        } else if (exceptions.length == methodWriter.numberOfExceptions) {
          sameExceptions = true;
          int currentExceptionOffset = exceptionsOffset + 2;
          for (int i = 0; i < exceptions.length; ++i) {
            if (methodWriter.exceptionIndexTable[i] != readUnsignedShort(currentExceptionOffset)) {
              sameExceptions = false;
              break;
            }
            currentExceptionOffset += 2;
          }
        }
        if (sameExceptions) {
          // We do not copy directly the code into methodWriter to save a byte array copy operation.
          // The real copy will be done in {@link MethodWriter#putMethodInfo}.
          methodWriter.sourceOffset = methodInfoOffset + 6;
          methodWriter.sourceLength = currentOffset - methodWriter.sourceOffset;
          return currentOffset;
        }
      }
    }

    // Visit the MethodParameters attribute.
    if (methodParametersOffset != 0) {
      int parametersCount = readByte(methodParametersOffset);
      int currentParameterOffset = methodParametersOffset + 1;
      while (parametersCount-- > 0) {
        // Read the name_index and access_flags fields and visit them.
        methodVisitor.visitParameter(
            readUTF8(currentParameterOffset, charBuffer),
            readUnsignedShort(currentParameterOffset + 2));
        currentParameterOffset += 4;
      }
    }

    // Visit the AnnotationDefault attribute.
    if (annotationDefaultOffset != 0) {
      AnnotationVisitor annotationVisitor = methodVisitor.visitAnnotationDefault();
      readElementValue(annotationVisitor, annotationDefaultOffset, null, charBuffer);
      if (annotationVisitor != null) {
        annotationVisitor.visitEnd();
      }
    }

    // Visit the RuntimeVisibleAnnotations attribute.
    if (runtimeVisibleAnnotationsOffset != 0) {
      int numAnnotations = readUnsignedShort(runtimeVisibleAnnotationsOffset);
      int currentAnnotationOffset = runtimeVisibleAnnotationsOffset + 2;
      while (numAnnotations-- > 0) {
        // Parse the type_index field.
        String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
        currentAnnotationOffset += 2;
        // Parse num_element_value_pairs and element_value_pairs and visit these values.
        currentAnnotationOffset =
            readElementValues(
                methodVisitor.visitAnnotation(annotationDescriptor, /* visible = */ true),
                currentAnnotationOffset,
                /* named = */ true,
                charBuffer);
      }
    }

    // Visit the RuntimeInvisibleAnnotations attribute.
    if (runtimeInvisibleAnnotationsOffset != 0) {
      int numAnnotations = readUnsignedShort(runtimeInvisibleAnnotationsOffset);
      int currentAnnotationOffset = runtimeInvisibleAnnotationsOffset + 2;
      while (numAnnotations-- > 0) {
        // Parse the type_index field.
        String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
        currentAnnotationOffset += 2;
        // Parse num_element_value_pairs and element_value_pairs and visit these values.
        currentAnnotationOffset =
            readElementValues(
                methodVisitor.visitAnnotation(annotationDescriptor, /* visible = */ false),
                currentAnnotationOffset,
                /* named = */ true,
                charBuffer);
      }
    }

    // Visit the RuntimeVisibleTypeAnnotations attribute.
    if (runtimeVisibleTypeAnnotationsOffset != 0) {
      int numAnnotations = readUnsignedShort(runtimeVisibleTypeAnnotationsOffset);
      int currentAnnotationOffset = runtimeVisibleTypeAnnotationsOffset + 2;
      while (numAnnotations-- > 0) {
        // Parse the target_type, target_info and target_path fields.
        currentAnnotationOffset = readTypeAnnotationTarget(context, currentAnnotationOffset);
        // Parse the type_index field.
        String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
        currentAnnotationOffset += 2;
        // Parse num_element_value_pairs and element_value_pairs and visit these values.
        currentAnnotationOffset =
            readElementValues(
                methodVisitor.visitTypeAnnotation(
                    context.currentTypeAnnotationTarget,
                    context.currentTypeAnnotationTargetPath,
                    annotationDescriptor,
                    /* visible = */ true),
                currentAnnotationOffset,
                /* named = */ true,
                charBuffer);
      }
    }

    // Visit the RuntimeInvisibleTypeAnnotations attribute.
    if (runtimeInvisibleTypeAnnotationsOffset != 0) {
      int numAnnotations = readUnsignedShort(runtimeInvisibleTypeAnnotationsOffset);
      int currentAnnotationOffset = runtimeInvisibleTypeAnnotationsOffset + 2;
      while (numAnnotations-- > 0) {
        // Parse the target_type, target_info and target_path fields.
        currentAnnotationOffset = readTypeAnnotationTarget(context, currentAnnotationOffset);
        // Parse the type_index field.
        String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
        currentAnnotationOffset += 2;
        // Parse num_element_value_pairs and element_value_pairs and visit these values.
        currentAnnotationOffset =
            readElementValues(
                methodVisitor.visitTypeAnnotation(
                    context.currentTypeAnnotationTarget,
                    context.currentTypeAnnotationTargetPath,
                    annotationDescriptor,
                    /* visible = */ false),
                currentAnnotationOffset,
                /* named = */ true,
                charBuffer);
      }
    }

    // Visit the RuntimeVisibleParameterAnnotations attribute.
    if (runtimeVisibleParameterAnnotationsOffset != 0) {
      readParameterAnnotations(
          methodVisitor, context, runtimeVisibleParameterAnnotationsOffset, /* visible = */ true);
    }

    // Visit the RuntimeInvisibleParameterAnnotations attribute.
    if (runtimeInvisibleParameterAnnotationsOffset != 0) {
      readParameterAnnotations(
          methodVisitor,
          context,
          runtimeInvisibleParameterAnnotationsOffset,
          /* visible = */ false);
    }

    // Visit the non standard attributes.
    while (attributes != null) {
      // Copy and reset the nextAttribute field so that it can also be used in MethodWriter.
      Attribute nextAttribute = attributes.nextAttribute;
      attributes.nextAttribute = null;
      methodVisitor.visitAttribute(attributes);
      attributes = nextAttribute;
    }

    // Visit the Code attribute.
    if (codeOffset != 0) {
      methodVisitor.visitCode();
      readCode(methodVisitor, context, codeOffset);
    }

    // Visit the end of the method.
    methodVisitor.visitEnd();
    return currentOffset;
  }

  // ----------------------------------------------------------------------------------------------
  // Methods to parse a Code attribute
  // ----------------------------------------------------------------------------------------------

  /**
   * Reads a JVMS 'Code' attribute and makes the given visitor visit it.
   *
   * @param methodVisitor the visitor that must visit the Code attribute.
   * @param context information about the class being parsed.
   * @param codeOffset the start offset in {@link #b} of the Code attribute, excluding its
   *     attribute_name_index and attribute_length fields.
   */
  private void readCode(
      final MethodVisitor methodVisitor, final Context context, final int codeOffset) {
    int currentOffset = codeOffset;

    // Read the max_stack, max_locals and code_length fields.
    final byte[] b = this.b;
    final char[] charBuffer = context.charBuffer;
    final int maxStack = readUnsignedShort(currentOffset);
    final int maxLocals = readUnsignedShort(currentOffset + 2);
    final int codeLength = readInt(currentOffset + 4);
    currentOffset += 8;

    // Read the bytecode 'code' array to create a label for each referenced instruction.
    final int bytecodeStartOffset = currentOffset;
    final int bytecodeEndOffset = currentOffset + codeLength;
    final Label[] labels = context.currentMethodLabels = new Label[codeLength + 1];
    while (currentOffset < bytecodeEndOffset) {
      final int bytecodeOffset = currentOffset - bytecodeStartOffset;
      final int opcode = b[currentOffset] & 0xFF;
      switch (INSTRUCTION_TYPE[opcode]) {
        case NOARG_INSN:
        case IMPLICIT_LOCAL_VARIABLE_INSN:
          currentOffset += 1;
          break;
        case LABEL_INSN:
          createLabel(bytecodeOffset + readShort(currentOffset + 1), labels);
          currentOffset += 3;
          break;
        case ASM_LABEL_INSN:
          createLabel(bytecodeOffset + readUnsignedShort(currentOffset + 1), labels);
          currentOffset += 3;
          break;
        case LABEL_WIDE_INSN:
        case ASM_LABEL_WIDE_INSN:
          createLabel(bytecodeOffset + readInt(currentOffset + 1), labels);
          currentOffset += 5;
          break;
        case WIDE_INSN:
          if ((b[currentOffset + 1] & 0xFF) == Opcodes.IINC) {
            currentOffset += 6;
          } else {
            currentOffset += 4;
          }
          break;
        case TABLESWITCH_INSN:
          // Skip 0 to 3 padding bytes.
          currentOffset += 4 - (bytecodeOffset & 3);
          // Read the default label and the number of table entries.
          createLabel(bytecodeOffset + readInt(currentOffset), labels);
          int numTableEntries = readInt(currentOffset + 8) - readInt(currentOffset + 4) + 1;
          currentOffset += 12;
          // Read the table labels.
          while (numTableEntries-- > 0) {
            createLabel(bytecodeOffset + readInt(currentOffset), labels);
            currentOffset += 4;
          }
          break;
        case LOOKUPSWITCH_INSN:
          // Skip 0 to 3 padding bytes.
          currentOffset += 4 - (bytecodeOffset & 3);
          // Read the default label and the number of switch cases.
          createLabel(bytecodeOffset + readInt(currentOffset), labels);
          int numSwitchCases = readInt(currentOffset + 4);
          currentOffset += 8;
          // Read the switch labels.
          while (numSwitchCases-- > 0) {
            createLabel(bytecodeOffset + readInt(currentOffset + 4), labels);
            currentOffset += 8;
          }
          break;
        case LOCAL_VARIABLE_INSN:
        case BYTE_INSN:
        case LDC_INSN:
          currentOffset += 2;
          break;
        case SHORT_INSN:
        case LDC_WIDE_INSN:
        case FIELD_OR_METHOD_INSN:
        case TYPE_INSN:
        case IINC_INSN:
          currentOffset += 3;
          break;
        case INVOKEINTERFACE_INSN:
        case INVOKEDYNAMIC_INSN:
          currentOffset += 5;
          break;
        case MULTIANEWARRAY_INSN:
          currentOffset += 4;
          break;
        default:
          throw new AssertionError();
      }
    }

    // Read the 'exception_table_length' and 'exception_table' field to create a label for each
    // referenced instruction, and to make methodVisitor visit the corresponding try catch blocks.
    {
      int exceptionTableLength = readUnsignedShort(currentOffset);
      currentOffset += 2;
      while (exceptionTableLength-- > 0) {
        Label start = createLabel(readUnsignedShort(currentOffset), labels);
        Label end = createLabel(readUnsignedShort(currentOffset + 2), labels);
        Label handler = createLabel(readUnsignedShort(currentOffset + 4), labels);
        String catchType =
            readUTF8(cpInfoOffsets[readUnsignedShort(currentOffset + 6)], charBuffer);
        currentOffset += 8;
        methodVisitor.visitTryCatchBlock(start, end, handler, catchType);
      }
    }

    // Read the Code attributes to create a label for each referenced instruction (the variables
    // are ordered as in Section 4.7 of the JVMS). Attribute offsets exclude the
    // attribute_name_index and attribute_length fields.
    // - The offset of the current 'stack_map_frame' in the StackMap[Table] attribute, or 0.
    // Initially, this is the offset of the first 'stack_map_frame' entry. Then this offset is
    // updated after each stack_map_frame is read.
    int stackMapFrameOffset = 0;
    // - The end offset of the StackMap[Table] attribute, or 0.
    int stackMapTableEndOffset = 0;
    // - Whether the stack map frames are compressed (i.e. in a StackMapTable) or not.
    boolean compressedFrames = true;
    // - The offset of the LocalVariableTable attribute, or 0.
    int localVariableTableOffset = 0;
    // - The offset of the LocalVariableTypeTable attribute, or 0.
    int localVariableTypeTableOffset = 0;
    // - The offset of each 'type_annotation' entry in the RuntimeVisibleTypeAnnotations
    // attribute, or null.
    int[] visibleTypeAnnotationOffsets = null;
    // - The offset of each 'type_annotation' entry in the RuntimeInvisibleTypeAnnotations
    // attribute, or null.
    int[] invisibleTypeAnnotationOffsets = null;
    // - The non standard attributes (linked with their {@link Attribute#nextAttribute} field).
    //   This list in the <i>reverse order</i> or their order in the ClassFile structure.
    Attribute attributes = null;

    int attributesCount = readUnsignedShort(currentOffset);
    currentOffset += 2;
    while (attributesCount-- > 0) {
      // Read the attribute_info's attribute_name and attribute_length fields.
      String attributeName = readUTF8(currentOffset, charBuffer);
      int attributeLength = readInt(currentOffset + 2);
      currentOffset += 6;
      if ("LocalVariableTable".equals(attributeName)) {
        if ((context.parsingOptions & SKIP_DEBUG) == 0) {
          localVariableTableOffset = currentOffset;
          // Parse the attribute to find the corresponding (debug only) labels.
          int localVariableTableLength = readUnsignedShort(currentOffset);
          currentOffset += 2;
          while (localVariableTableLength-- > 0) {
            int startPc = readUnsignedShort(currentOffset);
            createDebugLabel(startPc, labels);
            int length = readUnsignedShort(currentOffset + 2);
            createDebugLabel(startPc + length, labels);
            // Skip the name_index, descriptor_index and index fields (2 bytes each).
            currentOffset += 10;
          }
          continue;
        }
      } else if ("LocalVariableTypeTable".equals(attributeName)) {
        localVariableTypeTableOffset = currentOffset;
        // Here we do not extract the labels corresponding to the attribute content. We assume they
        // are the same or a subset of those of the LocalVariableTable attribute.
      } else if ("LineNumberTable".equals(attributeName)) {
        if ((context.parsingOptions & SKIP_DEBUG) == 0) {
          // Parse the attribute to find the corresponding (debug only) labels.
          int lineNumberTableLength = readUnsignedShort(currentOffset);
          currentOffset += 2;
          while (lineNumberTableLength-- > 0) {
            int startPc = readUnsignedShort(currentOffset);
            createDebugLabel(startPc, labels);
            Label startLabel = labels[startPc];
            // TODO find another method, nextChangedBlock should not be used for this!
            while (startLabel.line > 0) {
              if (startLabel.nextChangedBlock == null) {
                startLabel.nextChangedBlock = new Label();
              }
              startLabel = startLabel.nextChangedBlock;
            }
            startLabel.line = readUnsignedShort(currentOffset + 2);
            currentOffset += 4;
          }
          continue;
        }
      } else if ("RuntimeVisibleTypeAnnotations".equals(attributeName)) {
        visibleTypeAnnotationOffsets =
            readTypeAnnotations(methodVisitor, context, currentOffset, /* visible = */ true);
        // Here we do not extract the labels corresponding to the attribute content. This would
        // require a full parsing of the attribute, which would need to be repeated when parsing
        // the bytecode instructions (see below). Instead, the content of the attribute is read one
        // type annotation at a time (i.e. after a type annotation has been visited, the next type
        // annotation is read), and the labels it contains are also extracted one annotation at a
        // time. This assumes that type annotations are ordered by increasing bytecode offset.
      } else if ("RuntimeInvisibleTypeAnnotations".equals(attributeName)) {
        invisibleTypeAnnotationOffsets =
            readTypeAnnotations(methodVisitor, context, currentOffset, /* visible = */ false);
        // Same comment as above for the RuntimeVisibleTypeAnnotations attribute.
      } else if ("StackMapTable".equals(attributeName)) {
        if ((context.parsingOptions & SKIP_FRAMES) == 0) {
          stackMapFrameOffset = currentOffset + 2;
          stackMapTableEndOffset = currentOffset + attributeLength;
        }
        // Here we do not extract the labels corresponding to the attribute content. This would
        // require a full parsing of the attribute, which would need to be repeated when parsing
        // the bytecode instructions (see below). Instead, the content of the attribute is read one
        // frame at a time (i.e. after a frame has been visited, the next frame is read), and the
        // labels it contains are also extracted one frame at a time. Thanks to the ordering of
        // frames, having only a "one frame lookahead" is not a problem, i.e. it is not possible to
        // see an offset smaller than the offset of the current instruction and for which no Label
        // exist. Except for UNINITIALIZED type offsets. We solve this by parsing the stack map
        // table without a full decoding (see below).
      } else if ("StackMap".equals(attributeName)) {
        if ((context.parsingOptions & SKIP_FRAMES) == 0) {
          stackMapFrameOffset = currentOffset + 2;
          stackMapTableEndOffset = currentOffset + attributeLength;
          compressedFrames = false;
        }
        // IMPORTANT! Here we assume that the frames are ordered, as in the StackMapTable attribute,
        // although this is not guaranteed by the attribute format. This allows an incremental
        // extraction of the labels corresponding to this attribute (see the comment above for the
        // StackMapTable attribute).
      } else {
        Attribute attribute =
            readAttribute(
                context.attributePrototypes,
                attributeName,
                currentOffset,
                attributeLength,
                charBuffer,
                codeOffset,
                labels);
        attribute.nextAttribute = attributes;
        attributes = attribute;
      }
      currentOffset += attributeLength;
    }

    // Initialize the context fields related to stack map frames, and generate the first
    // (implicit) stack map frame, if needed.
    final boolean expandFrames = (context.parsingOptions & EXPAND_FRAMES) != 0;
    if (stackMapFrameOffset != 0) {
      // The bytecode offset of the first explicit frame is not offset_delta + 1 but only
      // offset_delta. Setting the implicit frame offset to -1 allows us to use of the
      // "offset_delta + 1" rule in all cases.
      context.currentFrameOffset = -1;
      context.currentFrameType = 0;
      context.currentFrameLocalCount = 0;
      context.currentFrameLocalCountDelta = 0;
      context.currentFrameLocalTypes = new Object[maxLocals];
      context.currentFrameStackCount = 0;
      context.currentFrameStackTypes = new Object[maxStack];
      if (expandFrames) {
        computeImplicitFrame(context);
      }
      // Find the labels for UNINITIALIZED frame types. Instead of decoding each element of the
      // stack map table, we look for 3 consecutive bytes that "look like" an UNINITIALIZED type
      // (tag ITEM_Uninitialized, offset within bytecode bounds, NEW instruction at this offset).
      // We may find false positives (i.e. not real UNINITIALIZED types), but this should be rare,
      // and the only consequence will be the creation of an unneeded label. This is better than
      // creating a label for each NEW instruction, and faster than fully decoding the whole stack
      // map table.
      for (int offset = stackMapFrameOffset; offset < stackMapTableEndOffset - 2; ++offset) {
        if (b[offset] == Frame.ITEM_UNINITIALIZED) {
          int potentialBytecodeOffset = readUnsignedShort(offset + 1);
          if (potentialBytecodeOffset >= 0 && potentialBytecodeOffset < codeLength) {
            if ((b[bytecodeStartOffset + potentialBytecodeOffset] & 0xFF) == Opcodes.NEW) {
              createLabel(potentialBytecodeOffset, labels);
            }
          }
        }
      }
    }
    if (expandFrames && (context.parsingOptions & EXPAND_ASM_INSNS) != 0) {
      // Expanding the ASM specific instructions can introduce F_INSERT frames, even if the method
      // does not currently have any frame. These inserted frames must be computed by simulating the
      // effect of the bytecode instructions, one by one, starting from the implicit first frame.
      // For this, MethodWriter needs to know maxLocals before the first instruction is visited. To
      // ensure this, we visit the implicit first frame here (passing only maxLocals - the rest is
      // computed in MethodWriter).
      methodVisitor.visitFrame(Opcodes.F_NEW, maxLocals, null, 0, null);
    }

    // Visit the bytecode instructions. First, introduce state variables for the incremental parsing
    // of the type annotations.

    // Index of the next runtime visible type annotation to read (in the
    // visibleTypeAnnotationOffsets array).
    int currentVisibleTypeAnnotationIndex = 0;
    // The bytecode offset of the next runtime visible type annotation to read, or -1.
    int currentVisibleTypeAnnotationBytecodeOffset =
        getTypeAnnotationBytecodeOffset(visibleTypeAnnotationOffsets, 0);
    // Index of the next runtime invisible type annotation to read (in the
    // invisibleTypeAnnotationOffsets array).
    int currentInvisibleTypeAnnotationIndex = 0;
    // The bytecode offset of the next runtime invisible type annotation to read, or -1.
    int currentInvisibleTypeAnnotationBytecodeOffset =
        getTypeAnnotationBytecodeOffset(invisibleTypeAnnotationOffsets, 0);

    // Whether a F_INSERT stack map frame must be inserted before the current instruction.
    boolean insertFrame = false;

    // The delta to add to a goto_w or jsr_w opcode to get the corresponding goto or jsr opcode,
    // or 0 if goto_w and jsr_w must be left unchanged (i.e. when expanding ASM specific
    // instructions).
    final int opcodeDelta = (context.parsingOptions & EXPAND_ASM_INSNS) == 0 ? -33 : 0;

    currentOffset = bytecodeStartOffset;
    while (currentOffset < bytecodeEndOffset) {
      final int currentBytecodeOffset = currentOffset - bytecodeStartOffset;

      // Visit the label and the line number(s) for this bytecode offset, if any.
      Label currentLabel = labels[currentBytecodeOffset];
      if (currentLabel != null) {
        Label next = currentLabel.nextChangedBlock;
        currentLabel.nextChangedBlock = null;
        methodVisitor.visitLabel(currentLabel);
        if ((context.parsingOptions & SKIP_DEBUG) == 0 && currentLabel.line > 0) {
          methodVisitor.visitLineNumber(currentLabel.line, currentLabel);
          while (next != null) {
            methodVisitor.visitLineNumber(next.line, currentLabel);
            next = next.nextChangedBlock;
          }
        }
      }

      // Visit the stack map frame for this bytecode offset, if any.
      while (stackMapFrameOffset != 0
          && (context.currentFrameOffset == currentBytecodeOffset
              || context.currentFrameOffset == -1)) {
        // If there is a stack map frame for this offset, make methodVisitor visit it, and read the
        // next stack map frame if there is one.
        if (context.currentFrameOffset != -1) {
          if (!compressedFrames || expandFrames) {
            methodVisitor.visitFrame(
                Opcodes.F_NEW,
                context.currentFrameLocalCount,
                context.currentFrameLocalTypes,
                context.currentFrameStackCount,
                context.currentFrameStackTypes);
          } else {
            methodVisitor.visitFrame(
                context.currentFrameType,
                context.currentFrameLocalCountDelta,
                context.currentFrameLocalTypes,
                context.currentFrameStackCount,
                context.currentFrameStackTypes);
          }
          // Since there is already a stack map frame for this bytecode offset, there is no need to
          // insert a new one.
          insertFrame = false;
        }
        if (stackMapFrameOffset < stackMapTableEndOffset) {
          stackMapFrameOffset =
              readStackMapFrame(stackMapFrameOffset, compressedFrames, expandFrames, context);
        } else {
          stackMapFrameOffset = 0;
        }
      }

      // Inserts a stack map frame for this bytecode offset, if requested by setting insertFrame to
      // true during the previous iteration. The actual frame content is computed in MethodWriter.
      if (insertFrame) {
        if ((context.parsingOptions & EXPAND_FRAMES) != 0) {
          methodVisitor.visitFrame(Frame.F_INSERT, 0, null, 0, null);
        }
        insertFrame = false;
      }

      // Visit the instruction at this bytecode offset.
      int opcode = b[currentOffset] & 0xFF;
      switch (INSTRUCTION_TYPE[opcode]) {
        case NOARG_INSN:
          methodVisitor.visitInsn(opcode);
          currentOffset += 1;
          break;
        case IMPLICIT_LOCAL_VARIABLE_INSN:
          if (opcode > Opcodes.ISTORE) {
            opcode -= 59; // ISTORE_0
            methodVisitor.visitVarInsn(Opcodes.ISTORE + (opcode >> 2), opcode & 0x3);
          } else {
            opcode -= 26; // ILOAD_0
            methodVisitor.visitVarInsn(Opcodes.ILOAD + (opcode >> 2), opcode & 0x3);
          }
          currentOffset += 1;
          break;
        case LABEL_INSN:
          methodVisitor.visitJumpInsn(
              opcode, labels[currentBytecodeOffset + readShort(currentOffset + 1)]);
          currentOffset += 3;
          break;
        case LABEL_WIDE_INSN:
          methodVisitor.visitJumpInsn(
              opcode + opcodeDelta, labels[currentBytecodeOffset + readInt(currentOffset + 1)]);
          currentOffset += 5;
          break;
        case ASM_LABEL_INSN:
          {
            // A forward jump with an offset > 32767. In this case we automatically replace ASM_GOTO
            // with GOTO_W, ASM_JSR with JSR_W and ASM_IFxxx <l> with IFNOTxxx <L> GOTO_W <l> L:...,
            // where IFNOTxxx is the "opposite" opcode of ASMS_IFxxx (e.g. IFNE for ASM_IFEQ) and
            // where <L> designates the instruction just after the GOTO_W.
            // First, change the ASM specific opcodes ASM_IFEQ ... ASM_JSR, ASM_IFNULL and
            // ASM_IFNONNULL to IFEQ ... JSR, IFNULL and IFNONNULL.
            opcode = opcode < 218 ? opcode - 49 : opcode - 20;
            Label target = labels[currentBytecodeOffset + readUnsignedShort(currentOffset + 1)];
            if (opcode == Opcodes.GOTO || opcode == Opcodes.JSR) {
              // Replace GOTO with GOTO_W and JSR with JSR_W.
              methodVisitor.visitJumpInsn(opcode + 33, target);
            } else {
              // Compute the "opposite" of opcode. This can be done by flipping the least
              // significant bit for IFNULL and IFNONNULL, and similarly for IFEQ ... IF_ACMPEQ
              // (with a pre and post offset by 1).
              opcode = opcode <= 166 ? ((opcode + 1) ^ 1) - 1 : opcode ^ 1;
              Label endif = createLabel(currentBytecodeOffset + 3, labels);
              methodVisitor.visitJumpInsn(opcode, endif);
              methodVisitor.visitJumpInsn(200 /* GOTO_W */, target);
              // endif designates the instruction just after GOTO_W, and is visited as part of the
              // next instruction. Since it is a jump target, we need to insert a frame here.
              insertFrame = true;
            }
            currentOffset += 3;
            break;
          }
        case ASM_LABEL_WIDE_INSN:
          {
            // Replaces ASM_GOTO_W with GOTO_W.
            methodVisitor.visitJumpInsn(
                200, labels[currentBytecodeOffset + readInt(currentOffset + 1)]);
            // The instruction just after is a jump target (because ASM_GOTO_W is used in patterns
            // IFNOTxxx <L> ASM_GOTO_W <l> L:..., see MethodWriter), so we need to insert a frame
            // here.
            insertFrame = true;
            currentOffset += 5;
            break;
          }
        case WIDE_INSN:
          opcode = b[currentOffset + 1] & 0xFF;
          if (opcode == Opcodes.IINC) {
            methodVisitor.visitIincInsn(
                readUnsignedShort(currentOffset + 2), readShort(currentOffset + 4));
            currentOffset += 6;
          } else {
            methodVisitor.visitVarInsn(opcode, readUnsignedShort(currentOffset + 2));
            currentOffset += 4;
          }
          break;
        case TABLESWITCH_INSN:
          {
            // Skip 0 to 3 padding bytes.
            currentOffset += 4 - (currentBytecodeOffset & 3);
            // Read the instruction.
            Label defaultLabel = labels[currentBytecodeOffset + readInt(currentOffset)];
            int low = readInt(currentOffset + 4);
            int high = readInt(currentOffset + 8);
            currentOffset += 12;
            Label[] table = new Label[high - low + 1];
            for (int i = 0; i < table.length; ++i) {
              table[i] = labels[currentBytecodeOffset + readInt(currentOffset)];
              currentOffset += 4;
            }
            methodVisitor.visitTableSwitchInsn(low, high, defaultLabel, table);
            break;
          }
        case LOOKUPSWITCH_INSN:
          {
            // Skip 0 to 3 padding bytes.
            currentOffset += 4 - (currentBytecodeOffset & 3);
            // Read the instruction.
            Label defaultLabel = labels[currentBytecodeOffset + readInt(currentOffset)];
            int nPairs = readInt(currentOffset + 4);
            currentOffset += 8;
            int[] keys = new int[nPairs];
            Label[] values = new Label[nPairs];
            for (int i = 0; i < nPairs; ++i) {
              keys[i] = readInt(currentOffset);
              values[i] = labels[currentBytecodeOffset + readInt(currentOffset + 4)];
              currentOffset += 8;
            }
            methodVisitor.visitLookupSwitchInsn(defaultLabel, keys, values);
            break;
          }
        case LOCAL_VARIABLE_INSN:
          methodVisitor.visitVarInsn(opcode, b[currentOffset + 1] & 0xFF);
          currentOffset += 2;
          break;
        case BYTE_INSN:
          methodVisitor.visitIntInsn(opcode, b[currentOffset + 1]);
          currentOffset += 2;
          break;
        case SHORT_INSN:
          methodVisitor.visitIntInsn(opcode, readShort(currentOffset + 1));
          currentOffset += 3;
          break;
        case LDC_INSN:
          methodVisitor.visitLdcInsn(readConst(b[currentOffset + 1] & 0xFF, charBuffer));
          currentOffset += 2;
          break;
        case LDC_WIDE_INSN:
          methodVisitor.visitLdcInsn(readConst(readUnsignedShort(currentOffset + 1), charBuffer));
          currentOffset += 3;
          break;
        case FIELD_OR_METHOD_INSN:
        case INVOKEINTERFACE_INSN:
          {
            int cpInfoOffset = cpInfoOffsets[readUnsignedShort(currentOffset + 1)];
            int nameAndTypeCpInfoOffset = cpInfoOffsets[readUnsignedShort(cpInfoOffset + 2)];
            String owner = readClass(cpInfoOffset, charBuffer);
            String name = readUTF8(nameAndTypeCpInfoOffset, charBuffer);
            String desc = readUTF8(nameAndTypeCpInfoOffset + 2, charBuffer);
            if (opcode < Opcodes.INVOKEVIRTUAL) {
              methodVisitor.visitFieldInsn(opcode, owner, name, desc);
            } else {
              boolean itf = b[cpInfoOffset - 1] == Symbol.CONSTANT_INTERFACE_METHODREF_TAG;
              methodVisitor.visitMethodInsn(opcode, owner, name, desc, itf);
            }
            if (opcode == Opcodes.INVOKEINTERFACE) {
              currentOffset += 5;
            } else {
              currentOffset += 3;
            }
            break;
          }
        case INVOKEDYNAMIC_INSN:
          {
            int cpInfoOffset = cpInfoOffsets[readUnsignedShort(currentOffset + 1)];
            int nameAndTypeCpInfoOffset = cpInfoOffsets[readUnsignedShort(cpInfoOffset + 2)];
            String name = readUTF8(nameAndTypeCpInfoOffset, charBuffer);
            String desc = readUTF8(nameAndTypeCpInfoOffset + 2, charBuffer);
            int bootstrapMethodOffset =
                context.bootstrapMethodOffsets[readUnsignedShort(cpInfoOffset)];
            Handle handle =
                (Handle) readConst(readUnsignedShort(bootstrapMethodOffset), charBuffer);
            Object[] boostrapMethodArguments =
                new Object[readUnsignedShort(bootstrapMethodOffset + 2)];
            bootstrapMethodOffset += 4;
            for (int i = 0; i < boostrapMethodArguments.length; i++) {
              boostrapMethodArguments[i] =
                  readConst(readUnsignedShort(bootstrapMethodOffset), charBuffer);
              bootstrapMethodOffset += 2;
            }
            methodVisitor.visitInvokeDynamicInsn(name, desc, handle, boostrapMethodArguments);
            currentOffset += 5;
            break;
          }
        case TYPE_INSN:
          methodVisitor.visitTypeInsn(opcode, readClass(currentOffset + 1, charBuffer));
          currentOffset += 3;
          break;
        case IINC_INSN:
          methodVisitor.visitIincInsn(b[currentOffset + 1] & 0xFF, b[currentOffset + 2]);
          currentOffset += 3;
          break;
        case MULTIANEWARRAY_INSN:
          methodVisitor.visitMultiANewArrayInsn(
              readClass(currentOffset + 1, charBuffer), b[currentOffset + 3] & 0xFF);
          currentOffset += 4;
          break;
        default:
          throw new AssertionError();
      }

      // Visit the runtime visible instruction annotations, if any.
      while (visibleTypeAnnotationOffsets != null
          && currentVisibleTypeAnnotationIndex < visibleTypeAnnotationOffsets.length
          && currentVisibleTypeAnnotationBytecodeOffset <= currentBytecodeOffset) {
        if (currentVisibleTypeAnnotationBytecodeOffset == currentBytecodeOffset) {
          // Parse the target_type, target_info and target_path fields.
          int currentAnnotationOffset =
              readTypeAnnotationTarget(
                  context, visibleTypeAnnotationOffsets[currentVisibleTypeAnnotationIndex]);
          // Parse the type_index field.
          String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
          currentAnnotationOffset += 2;
          // Parse num_element_value_pairs and element_value_pairs and visit these values.
          readElementValues(
              methodVisitor.visitInsnAnnotation(
                  context.currentTypeAnnotationTarget,
                  context.currentTypeAnnotationTargetPath,
                  annotationDescriptor,
                  /* visible = */ true),
              currentAnnotationOffset,
              /* named = */ true,
              charBuffer);
        }
        currentVisibleTypeAnnotationBytecodeOffset =
            getTypeAnnotationBytecodeOffset(
                visibleTypeAnnotationOffsets, ++currentVisibleTypeAnnotationIndex);
      }

      // Visit the runtime invisible instruction annotations, if any.
      while (invisibleTypeAnnotationOffsets != null
          && currentInvisibleTypeAnnotationIndex < invisibleTypeAnnotationOffsets.length
          && currentInvisibleTypeAnnotationBytecodeOffset <= currentBytecodeOffset) {
        if (currentInvisibleTypeAnnotationBytecodeOffset == currentBytecodeOffset) {
          // Parse the target_type, target_info and target_path fields.
          int currentAnnotationOffset =
              readTypeAnnotationTarget(
                  context, invisibleTypeAnnotationOffsets[currentInvisibleTypeAnnotationIndex]);
          // Parse the type_index field.
          String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
          currentAnnotationOffset += 2;
          // Parse num_element_value_pairs and element_value_pairs and visit these values.
          readElementValues(
              methodVisitor.visitInsnAnnotation(
                  context.currentTypeAnnotationTarget,
                  context.currentTypeAnnotationTargetPath,
                  annotationDescriptor,
                  /* visible = */ false),
              currentAnnotationOffset,
              /* named = */ true,
              charBuffer);
        }
        currentInvisibleTypeAnnotationBytecodeOffset =
            getTypeAnnotationBytecodeOffset(
                invisibleTypeAnnotationOffsets, ++currentInvisibleTypeAnnotationIndex);
      }
    }
    if (labels[codeLength] != null) {
      methodVisitor.visitLabel(labels[codeLength]);
    }

    // Visit LocalVariableTable and LocalVariableTypeTable attributes.
    if (localVariableTableOffset != 0 && (context.parsingOptions & SKIP_DEBUG) == 0) {
      // The (start_pc, index, signature_index) fields of each entry of the LocalVariableTypeTable.
      int[] typeTable = null;
      if (localVariableTypeTableOffset != 0) {
        typeTable = new int[readUnsignedShort(localVariableTypeTableOffset) * 3];
        currentOffset = localVariableTypeTableOffset + 2;
        for (int i = typeTable.length; i > 0; ) {
          typeTable[--i] = currentOffset + 6; // offset of the signature_index field
          typeTable[--i] = readUnsignedShort(currentOffset + 8); // value of the index field
          typeTable[--i] = readUnsignedShort(currentOffset); // value of the start_pc field
          currentOffset += 10;
        }
      }
      int localVariableTableLength = readUnsignedShort(localVariableTableOffset);
      currentOffset = localVariableTableOffset + 2;
      while (localVariableTableLength-- > 0) {
        int startPc = readUnsignedShort(currentOffset);
        int length = readUnsignedShort(currentOffset + 2);
        String name = readUTF8(currentOffset + 4, charBuffer);
        String descriptor = readUTF8(currentOffset + 6, charBuffer);
        int index = readUnsignedShort(currentOffset + 8);
        currentOffset += 10;
        String signature = null;
        if (typeTable != null) {
          for (int i = 0; i < typeTable.length; i += 3) {
            if (typeTable[i] == startPc && typeTable[i + 1] == index) {
              signature = readUTF8(typeTable[i + 2], charBuffer);
              break;
            }
          }
        }
        methodVisitor.visitLocalVariable(
            name, descriptor, signature, labels[startPc], labels[startPc + length], index);
      }
    }

    // Visit the local variable type annotations of the RuntimeVisibleTypeAnnotations attribute.
    if (visibleTypeAnnotationOffsets != null) {
      for (int i = 0; i < visibleTypeAnnotationOffsets.length; ++i) {
        int targetType = readByte(visibleTypeAnnotationOffsets[i]);
        if (targetType == TypeReference.LOCAL_VARIABLE
            || targetType == TypeReference.RESOURCE_VARIABLE) {
          // Parse the target_type, target_info and target_path fields.
          currentOffset = readTypeAnnotationTarget(context, visibleTypeAnnotationOffsets[i]);
          // Parse the type_index field.
          String annotationDescriptor = readUTF8(currentOffset, charBuffer);
          currentOffset += 2;
          // Parse num_element_value_pairs and element_value_pairs and visit these values.
          currentOffset =
              readElementValues(
                  methodVisitor.visitLocalVariableAnnotation(
                      context.currentTypeAnnotationTarget,
                      context.currentTypeAnnotationTargetPath,
                      context.currentLocalVariableAnnotationRangeStarts,
                      context.currentLocalVariableAnnotationRangeEnds,
                      context.currentLocalVariableAnnotationRangeIndices,
                      annotationDescriptor,
                      /* visible = */ true),
                  currentOffset,
                  /* named = */ true,
                  charBuffer);
        }
      }
    }

    // Visit the local variable type annotations of the RuntimeInvisibleTypeAnnotations attribute.
    if (invisibleTypeAnnotationOffsets != null) {
      for (int i = 0; i < invisibleTypeAnnotationOffsets.length; ++i) {
        int targetType = readByte(visibleTypeAnnotationOffsets[i]);
        if (targetType == TypeReference.LOCAL_VARIABLE
            || targetType == TypeReference.RESOURCE_VARIABLE) {
          // Parse the target_type, target_info and target_path fields.
          currentOffset = readTypeAnnotationTarget(context, invisibleTypeAnnotationOffsets[i]);
          // Parse the type_index field.
          String annotationDescriptor = readUTF8(currentOffset, charBuffer);
          currentOffset += 2;
          // Parse num_element_value_pairs and element_value_pairs and visit these values.
          currentOffset =
              readElementValues(
                  methodVisitor.visitLocalVariableAnnotation(
                      context.currentTypeAnnotationTarget,
                      context.currentTypeAnnotationTargetPath,
                      context.currentLocalVariableAnnotationRangeStarts,
                      context.currentLocalVariableAnnotationRangeEnds,
                      context.currentLocalVariableAnnotationRangeIndices,
                      annotationDescriptor,
                      /* visible = */ false),
                  currentOffset,
                  /* named = */ true,
                  charBuffer);
        }
      }
    }

    // Visit the non standard attributes.
    while (attributes != null) {
      // Copy and reset the nextAttribute field so that it can also be used in MethodWriter.
      Attribute nextAttribute = attributes.nextAttribute;
      attributes.nextAttribute = null;
      methodVisitor.visitAttribute(attributes);
      attributes = nextAttribute;
    }

    // Visit the max stack and max locals values.
    methodVisitor.visitMaxs(maxStack, maxLocals);
  }

  /**
   * Returns the label corresponding to the given bytecode offset. The default implementation of
   * this method creates a label for the given offset if it has not been already created.
   *
   * @param bytecodeOffset a bytecode offset in a method.
   * @param labels the already created labels, indexed by their offset. If a label already exists
   *     for bytecodeOffset this method must not create a new one. Otherwise it must store the new
   *     label in this array.
   * @return a non null Label, which must be equal to labels[bytecodeOffset].
   */
  protected Label readLabel(final int bytecodeOffset, final Label[] labels) {
    if (labels[bytecodeOffset] == null) {
      labels[bytecodeOffset] = new Label();
    }
    return labels[bytecodeOffset];
  }

  /**
   * Creates a label without the {@link Label#DEBUG_ONLY} flag set, for the given bytecode offset.
   * The label is created with a call to {@link #readLabel} and its {@link Label#DEBUG_ONLY} flag is
   * cleared.
   *
   * @param bytecodeOffset a bytecode offset in a method.
   * @param labels the already created labels, indexed by their offset.
   * @return a Label without the {@link Label#DEBUG_ONLY} flag set.
   */
  private Label createLabel(final int bytecodeOffset, final Label[] labels) {
    Label label = readLabel(bytecodeOffset, labels);
    label.status &= ~Label.DEBUG_ONLY;
    return label;
  }

  /**
   * Creates a label with the {@link Label#DEBUG_ONLY} flag set, if there is no already existing
   * label for the given bytecode offset (otherwise does nothing). The label is created with a call
   * to {@link #readLabel}.
   *
   * @param bytecodeOffset a bytecode offset in a method.
   * @param labels the already created labels, indexed by their offset.
   */
  private void createDebugLabel(final int bytecodeOffset, final Label[] labels) {
    if (labels[bytecodeOffset] == null) {
      readLabel(bytecodeOffset, labels).status |= Label.DEBUG_ONLY;
    }
  }

  // ----------------------------------------------------------------------------------------------
  // Methods to parse annotations, type annotations and parameter annotations
  // ----------------------------------------------------------------------------------------------

  /**
   * Parses a Runtime[In]VisibleTypeAnnotations attribute to find the offset of each type_annotation
   * entry it contains, to find the corresponding labels, and to visit the try catch block
   * annotations.
   *
   * @param methodVisitor the method visitor to be used to visit the try catch block annotations.
   * @param context information about the class being parsed.
   * @param runtimeTypeAnnotationsOffset the start offset of a Runtime[In]VisibleTypeAnnotations
   *     attribute, excluding the attribute_info's attribute_name_index and attribute_length fields.
   * @param visible true if the attribute to parse is a RuntimeVisibleTypeAnnotations attribute,
   *     false it is a RuntimeInvisibleTypeAnnotations attribute.
   * @return the start offset of each entry of the Runtime[In]VisibleTypeAnnotations_attribute's
   *     'annotations' array field.
   */
  private int[] readTypeAnnotations(
      final MethodVisitor methodVisitor,
      final Context context,
      final int runtimeTypeAnnotationsOffset,
      final boolean visible) {
    char[] charBuffer = context.charBuffer;
    int currentOffset = runtimeTypeAnnotationsOffset;
    // Read the num_annotations field and create an array to store the type_annotation offsets.
    int[] typeAnnotationsOffsets = new int[readUnsignedShort(currentOffset)];
    currentOffset += 2;
    // Parse the 'annotations' array field.
    for (int i = 0; i < typeAnnotationsOffsets.length; ++i) {
      typeAnnotationsOffsets[i] = currentOffset;
      // Parse the type_annotation's target_type and the target_info fields. The size of the
      // target_info field depends on the value of target_type.
      int targetType = readInt(currentOffset);
      switch (targetType >>> 24) {
        case TypeReference.CLASS_TYPE_PARAMETER:
        case TypeReference.METHOD_TYPE_PARAMETER:
        case TypeReference.METHOD_FORMAL_PARAMETER:
          currentOffset += 2;
          break;
        case TypeReference.FIELD:
        case TypeReference.METHOD_RETURN:
        case TypeReference.METHOD_RECEIVER:
          currentOffset += 1;
          break;
        case TypeReference.LOCAL_VARIABLE:
        case TypeReference.RESOURCE_VARIABLE:
          // A localvar_target has a variable size, which depends on the value of their table_length
          // field. It also reference bytecode offsets, for which we need labels.
          int tableLength = readUnsignedShort(currentOffset + 1);
          currentOffset += 3;
          while (tableLength-- > 0) {
            int startPc = readUnsignedShort(currentOffset);
            int length = readUnsignedShort(currentOffset + 2);
            // Skip the index field (2 bytes).
            currentOffset += 6;
            createLabel(startPc, context.currentMethodLabels);
            createLabel(startPc + length, context.currentMethodLabels);
          }
          break;
        case TypeReference.CAST:
        case TypeReference.CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT:
        case TypeReference.METHOD_INVOCATION_TYPE_ARGUMENT:
        case TypeReference.CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT:
        case TypeReference.METHOD_REFERENCE_TYPE_ARGUMENT:
          currentOffset += 4;
          break;
        case TypeReference.CLASS_EXTENDS:
        case TypeReference.CLASS_TYPE_PARAMETER_BOUND:
        case TypeReference.METHOD_TYPE_PARAMETER_BOUND:
        case TypeReference.THROWS:
        case TypeReference.EXCEPTION_PARAMETER:
        case TypeReference.INSTANCEOF:
        case TypeReference.NEW:
        case TypeReference.CONSTRUCTOR_REFERENCE:
        case TypeReference.METHOD_REFERENCE:
          currentOffset += 3;
          break;
        default:
          throw new AssertionError();
      }
      // Parse the rest of the type_annotation structure, starting with the target_path structure
      // (whose size depends on its path_length field).
      int pathLength = readByte(currentOffset);
      if ((targetType >>> 24) == TypeReference.EXCEPTION_PARAMETER) {
        // Parse the target_path structure and create a corresponding TypePath.
        TypePath path = pathLength == 0 ? null : new TypePath(b, currentOffset);
        currentOffset += 1 + 2 * pathLength;
        // Parse the type_index field.
        String annotationDescriptor = readUTF8(currentOffset, charBuffer);
        currentOffset += 2;
        // Parse num_element_value_pairs and element_value_pairs and visit these values.
        currentOffset =
            readElementValues(
                methodVisitor.visitTryCatchAnnotation(
                    targetType & 0xFFFFFF00, path, annotationDescriptor, visible),
                currentOffset,
                /* named = */ true,
                charBuffer);
      } else {
        // We don't want to visit the other target_type annotations, so we just skip them (which
        // requires some parsing because the element_value_pairs array has a variable size). First,
        // skip the target_path structure:
        currentOffset += 3 + 2 * pathLength;
        // Then skip the num_element_value_pairs and element_value_pairs fields (by reading them
        // with a null AnnotationVisitor).
        currentOffset =
            readElementValues(
                /* annotationVisitor = */ null, currentOffset, /* named = */ true, charBuffer);
      }
    }
    return typeAnnotationsOffsets;
  }

  /**
   * Returns the bytecode offset corresponding to the specified JVMS 'type_annotation' structure, or
   * -1 if there is no such type_annotation of if it does not have a bytecode offset.
   *
   * @param typeAnnotationOffsets the offset of each 'type_annotation' entry in a
   *     Runtime[In]VisibleTypeAnnotations attribute, or null.
   * @param typeAnnotationIndex the index a 'type_annotation' entry in typeAnnotationOffsets.
   * @return bytecode offset corresponding to the specified JVMS 'type_annotation' structure, or -1
   *     if there is no such type_annotation of if it does not have a bytecode offset.
   */
  private int getTypeAnnotationBytecodeOffset(
      int[] typeAnnotationOffsets, int typeAnnotationIndex) {
    if (typeAnnotationOffsets == null
        || typeAnnotationIndex >= typeAnnotationOffsets.length
        || readByte(typeAnnotationOffsets[typeAnnotationIndex]) < TypeReference.INSTANCEOF) {
      return -1;
    }
    return readUnsignedShort(typeAnnotationOffsets[typeAnnotationIndex] + 1);
  }

  /**
   * Parses the header of a JVMS type_annotation structure to extract its target_type, target_info
   * and target_path (the result is stored in the given context), and returns the start offset of
   * the rest of the type_annotation structure.
   *
   * @param context information about the class being parsed. This is where the extracted
   *     target_type and target_path must be stored.
   * @param typeAnnotationOffset the start offset of a type_annotation structure.
   * @return the start offset of the rest of the type_annotation structure.
   */
  private int readTypeAnnotationTarget(final Context context, final int typeAnnotationOffset) {
    int currentOffset = typeAnnotationOffset;
    // Parse and store the target_type structure.
    int targetType = readInt(typeAnnotationOffset);
    switch (targetType >>> 24) {
      case TypeReference.CLASS_TYPE_PARAMETER:
      case TypeReference.METHOD_TYPE_PARAMETER:
      case TypeReference.METHOD_FORMAL_PARAMETER:
        targetType &= 0xFFFF0000;
        currentOffset += 2;
        break;
      case TypeReference.FIELD:
      case TypeReference.METHOD_RETURN:
      case TypeReference.METHOD_RECEIVER:
        targetType &= 0xFF000000;
        currentOffset += 1;
        break;
      case TypeReference.LOCAL_VARIABLE:
      case TypeReference.RESOURCE_VARIABLE:
        targetType &= 0xFF000000;
        int tableLength = readUnsignedShort(currentOffset + 1);
        currentOffset += 3;
        context.currentLocalVariableAnnotationRangeStarts = new Label[tableLength];
        context.currentLocalVariableAnnotationRangeEnds = new Label[tableLength];
        context.currentLocalVariableAnnotationRangeIndices = new int[tableLength];
        for (int i = 0; i < tableLength; ++i) {
          int startPc = readUnsignedShort(currentOffset);
          int length = readUnsignedShort(currentOffset + 2);
          int index = readUnsignedShort(currentOffset + 4);
          currentOffset += 6;
          context.currentLocalVariableAnnotationRangeStarts[i] =
              createLabel(startPc, context.currentMethodLabels);
          context.currentLocalVariableAnnotationRangeEnds[i] =
              createLabel(startPc + length, context.currentMethodLabels);
          context.currentLocalVariableAnnotationRangeIndices[i] = index;
        }
        break;
      case TypeReference.CAST:
      case TypeReference.CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT:
      case TypeReference.METHOD_INVOCATION_TYPE_ARGUMENT:
      case TypeReference.CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT:
      case TypeReference.METHOD_REFERENCE_TYPE_ARGUMENT:
        targetType &= 0xFF0000FF;
        currentOffset += 4;
        break;
      case TypeReference.CLASS_EXTENDS:
      case TypeReference.CLASS_TYPE_PARAMETER_BOUND:
      case TypeReference.METHOD_TYPE_PARAMETER_BOUND:
      case TypeReference.THROWS:
      case TypeReference.EXCEPTION_PARAMETER:
        targetType &= 0xFFFFFF00;
        currentOffset += 3;
        break;
      case TypeReference.INSTANCEOF:
      case TypeReference.NEW:
      case TypeReference.CONSTRUCTOR_REFERENCE:
      case TypeReference.METHOD_REFERENCE:
        targetType &= 0xFF000000;
        currentOffset += 3;
        break;
      default:
        throw new AssertionError();
    }
    context.currentTypeAnnotationTarget = targetType;
    // Parse and store the target_path structure.
    int pathLength = readByte(currentOffset);
    context.currentTypeAnnotationTargetPath =
        pathLength == 0 ? null : new TypePath(b, currentOffset);
    // Return the start offset of the rest of the type_annotation structure.
    return currentOffset + 1 + 2 * pathLength;
  }

  /**
   * Reads a Runtime[In]VisibleParameterAnnotations attribute and makes the given visitor visit it.
   *
   * @param methodVisitor the visitor that must visit the parameter annotations.
   * @param context information about the class being parsed.
   * @param runtimeParameterAnnotationsOffset the start offset of a
   *     Runtime[In]VisibleParameterAnnotations attribute, excluding the attribute_info's
   *     attribute_name_index and attribute_length fields.
   * @param visible true if the attribute to parse is a RuntimeVisibleParameterAnnotations
   *     attribute, false it is a RuntimeInvisibleParameterAnnotations attribute.
   */
  private void readParameterAnnotations(
      final MethodVisitor methodVisitor,
      final Context context,
      final int runtimeParameterAnnotationsOffset,
      final boolean visible) {
    int currentOffset = runtimeParameterAnnotationsOffset;
    int numParameters = b[currentOffset++] & 0xFF;
    methodVisitor.visitAnnotableParameterCount(numParameters, visible);
    char[] charBuffer = context.charBuffer;
    for (int i = 0; i < numParameters; ++i) {
      int numAnnotations = readUnsignedShort(currentOffset);
      currentOffset += 2;
      while (numAnnotations-- > 0) {
        // Parse the type_index field.
        String annotationDescriptor = readUTF8(currentOffset, charBuffer);
        currentOffset += 2;
        // Parse num_element_value_pairs and element_value_pairs and visit these values.
        currentOffset =
            readElementValues(
                methodVisitor.visitParameterAnnotation(i, annotationDescriptor, visible),
                currentOffset,
                /* named = */ true,
                charBuffer);
      }
    }
  }

  /**
   * Reads the element values of a JVMS 'annotation' structure and makes the given visitor visit
   * them. This method can also be used to read the values of the JVMS 'array_value' field of an
   * annotation's 'element_value'.
   *
   * @param annotationVisitor the visitor that must visit the values.
   * @param annotationOffset the start offset of an 'annotation' structure (excluding its type_index
   *     field) or of an 'array_value' structure.
   * @param named if the annotation values are named or not. This should be true to parse the values
   *     of a JVMS 'annotation' structure, and false to parse the JVMS 'array_value' of an
   *     annotation's element_value.
   * @param charBuffer the buffer used to read strings in the constant pool.
   * @return the end offset of the JVMS 'annotation' or 'array_value' structure.
   */
  private int readElementValues(
      final AnnotationVisitor annotationVisitor,
      final int annotationOffset,
      final boolean named,
      final char[] charBuffer) {
    int currentOffset = annotationOffset;
    // Read the num_element_value_pairs field (or num_values field for an array_value).
    int numElementValuePairs = readUnsignedShort(currentOffset);
    currentOffset += 2;
    if (named) {
      // Parse the element_value_pairs array.
      while (numElementValuePairs-- > 0) {
        String elementName = readUTF8(currentOffset, charBuffer);
        currentOffset =
            readElementValue(annotationVisitor, currentOffset + 2, elementName, charBuffer);
      }
    } else {
      // Parse the array_value array.
      while (numElementValuePairs-- > 0) {
        currentOffset =
            readElementValue(annotationVisitor, currentOffset, /* named = */ null, charBuffer);
      }
    }
    if (annotationVisitor != null) {
      annotationVisitor.visitEnd();
    }
    return currentOffset;
  }

  /**
   * Reads a JVMS 'element_value' structure and makes the given visitor visit it.
   *
   * @param annotationVisitor the visitor that must visit the element_value structure.
   * @param elementValueOffset the start offset in {@link #b} of the element_value structure to be
   *     read.
   * @param elementName the name of the element_value structure to be read, or <tt>null</tt>.
   * @param charBuffer the buffer used to read strings in the constant pool.
   * @return the end offset of the JVMS 'element_value' structure.
   */
  private int readElementValue(
      final AnnotationVisitor annotationVisitor,
      final int elementValueOffset,
      final String elementName,
      final char[] charBuffer) {
    int currentOffset = elementValueOffset;
    if (annotationVisitor == null) {
      switch (b[currentOffset] & 0xFF) {
        case 'e': // enum_const_value
          return currentOffset + 5;
        case '@': // annotation_value
          return readElementValues(null, currentOffset + 3, /* named = */ true, charBuffer);
        case '[': // array_value
          return readElementValues(null, currentOffset + 1, /* named = */ false, charBuffer);
        default:
          return currentOffset + 3;
      }
    }
    switch (b[currentOffset++] & 0xFF) {
      case 'B': // const_value_index, CONSTANT_Integer
        annotationVisitor.visit(
            elementName, (byte) readInt(cpInfoOffsets[readUnsignedShort(currentOffset)]));
        currentOffset += 2;
        break;
      case 'C': // const_value_index, CONSTANT_Integer
        annotationVisitor.visit(
            elementName, (char) readInt(cpInfoOffsets[readUnsignedShort(currentOffset)]));
        currentOffset += 2;
        break;
      case 'D': // const_value_index, CONSTANT_Double
      case 'F': // const_value_index, CONSTANT_Float
      case 'I': // const_value_index, CONSTANT_Integer
      case 'J': // const_value_index, CONSTANT_Long
        annotationVisitor.visit(
            elementName, readConst(readUnsignedShort(currentOffset), charBuffer));
        currentOffset += 2;
        break;
      case 'S': // const_value_index, CONSTANT_Integer
        annotationVisitor.visit(
            elementName, (short) readInt(cpInfoOffsets[readUnsignedShort(currentOffset)]));
        currentOffset += 2;
        break;

      case 'Z': // const_value_index, CONSTANT_Integer
        annotationVisitor.visit(
            elementName,
            readInt(cpInfoOffsets[readUnsignedShort(currentOffset)]) == 0
                ? Boolean.FALSE
                : Boolean.TRUE);
        currentOffset += 2;
        break;
      case 's': // const_value_index, CONSTANT_Utf8
        annotationVisitor.visit(elementName, readUTF8(currentOffset, charBuffer));
        currentOffset += 2;
        break;
      case 'e': // enum_const_value
        annotationVisitor.visitEnum(
            elementName,
            readUTF8(currentOffset, charBuffer),
            readUTF8(currentOffset + 2, charBuffer));
        currentOffset += 4;
        break;
      case 'c': // class_info
        annotationVisitor.visit(elementName, Type.getType(readUTF8(currentOffset, charBuffer)));
        currentOffset += 2;
        break;
      case '@': // annotation_value
        currentOffset =
            readElementValues(
                annotationVisitor.visitAnnotation(elementName, readUTF8(currentOffset, charBuffer)),
                currentOffset + 2,
                true,
                charBuffer);
        break;
      case '[': // array_value
        int numValues = readUnsignedShort(currentOffset);
        currentOffset += 2;
        if (numValues == 0) {
          return readElementValues(
              annotationVisitor.visitArray(elementName),
              currentOffset - 2,
              /* named = */ false,
              charBuffer);
        }
        switch (this.b[currentOffset] & 0xFF) {
          case 'B':
            byte[] byteValues = new byte[numValues];
            for (int i = 0; i < numValues; i++) {
              byteValues[i] = (byte) readInt(cpInfoOffsets[readUnsignedShort(currentOffset + 1)]);
              currentOffset += 3;
            }
            annotationVisitor.visit(elementName, byteValues);
            break;
          case 'Z':
            boolean[] booleanValues = new boolean[numValues];
            for (int i = 0; i < numValues; i++) {
              booleanValues[i] = readInt(cpInfoOffsets[readUnsignedShort(currentOffset + 1)]) != 0;
              currentOffset += 3;
            }
            annotationVisitor.visit(elementName, booleanValues);
            break;
          case 'S':
            short[] shortValues = new short[numValues];
            for (int i = 0; i < numValues; i++) {
              shortValues[i] = (short) readInt(cpInfoOffsets[readUnsignedShort(currentOffset + 1)]);
              currentOffset += 3;
            }
            annotationVisitor.visit(elementName, shortValues);
            break;
          case 'C':
            char[] charValues = new char[numValues];
            for (int i = 0; i < numValues; i++) {
              charValues[i] = (char) readInt(cpInfoOffsets[readUnsignedShort(currentOffset + 1)]);
              currentOffset += 3;
            }
            annotationVisitor.visit(elementName, charValues);
            break;
          case 'I':
            int[] intValues = new int[numValues];
            for (int i = 0; i < numValues; i++) {
              intValues[i] = readInt(cpInfoOffsets[readUnsignedShort(currentOffset + 1)]);
              currentOffset += 3;
            }
            annotationVisitor.visit(elementName, intValues);
            break;
          case 'J':
            long[] longValues = new long[numValues];
            for (int i = 0; i < numValues; i++) {
              longValues[i] = readLong(cpInfoOffsets[readUnsignedShort(currentOffset + 1)]);
              currentOffset += 3;
            }
            annotationVisitor.visit(elementName, longValues);
            break;
          case 'F':
            float[] floatValues = new float[numValues];
            for (int i = 0; i < numValues; i++) {
              floatValues[i] =
                  Float.intBitsToFloat(
                      readInt(cpInfoOffsets[readUnsignedShort(currentOffset + 1)]));
              currentOffset += 3;
            }
            annotationVisitor.visit(elementName, floatValues);
            break;
          case 'D':
            double[] doubleValues = new double[numValues];
            for (int i = 0; i < numValues; i++) {
              doubleValues[i] =
                  Double.longBitsToDouble(
                      readLong(cpInfoOffsets[readUnsignedShort(currentOffset + 1)]));
              currentOffset += 3;
            }
            annotationVisitor.visit(elementName, doubleValues);
            break;
          default:
            currentOffset =
                readElementValues(
                    annotationVisitor.visitArray(elementName),
                    currentOffset - 2,
                    /* named = */ false,
                    charBuffer);
            break;
        }
        break;
      default:
        throw new AssertionError();
    }
    return currentOffset;
  }

  // ----------------------------------------------------------------------------------------------
  // Methods to parse stack map frames
  // ----------------------------------------------------------------------------------------------

  /**
   * Computes the implicit frame of the method currently being parsed (as defined in the given
   * {@link Context}) and stores it in the given context.
   *
   * @param context information about the class being parsed.
   */
  private void computeImplicitFrame(final Context context) {
    String methodDescriptor = context.currentMethodDescriptor;
    Object[] locals = context.currentFrameLocalTypes;
    int nLocal = 0;
    if ((context.currentMethodAccessFlags & Opcodes.ACC_STATIC) == 0) {
      if ("<init>".equals(context.currentMethodName)) {
        locals[nLocal++] = Opcodes.UNINITIALIZED_THIS;
      } else {
        locals[nLocal++] = readClass(header + 2, context.charBuffer);
      }
    }
    // Parse the method descriptor, one argument type descriptor at each iteration. Start by
    // skipping the first method descriptor character, which is always '('.
    int currentMethodDescritorOffset = 1;
    while (true) {
      int currentArgumentDescriptorStartOffset = currentMethodDescritorOffset;
      switch (methodDescriptor.charAt(currentMethodDescritorOffset++)) {
        case 'Z':
        case 'C':
        case 'B':
        case 'S':
        case 'I':
          locals[nLocal++] = Opcodes.INTEGER;
          break;
        case 'F':
          locals[nLocal++] = Opcodes.FLOAT;
          break;
        case 'J':
          locals[nLocal++] = Opcodes.LONG;
          break;
        case 'D':
          locals[nLocal++] = Opcodes.DOUBLE;
          break;
        case '[':
          while (methodDescriptor.charAt(currentMethodDescritorOffset) == '[') {
            ++currentMethodDescritorOffset;
          }
          if (methodDescriptor.charAt(currentMethodDescritorOffset) == 'L') {
            ++currentMethodDescritorOffset;
            while (methodDescriptor.charAt(currentMethodDescritorOffset) != ';') {
              ++currentMethodDescritorOffset;
            }
          }
          locals[nLocal++] =
              methodDescriptor.substring(
                  currentArgumentDescriptorStartOffset, ++currentMethodDescritorOffset);
          break;
        case 'L':
          while (methodDescriptor.charAt(currentMethodDescritorOffset) != ';') {
            ++currentMethodDescritorOffset;
          }
          locals[nLocal++] =
              methodDescriptor.substring(
                  currentArgumentDescriptorStartOffset + 1, currentMethodDescritorOffset++);
          break;
        default:
          context.currentFrameLocalCount = nLocal;
          return;
      }
    }
  }

  /**
   * Reads a JVMS 'stack_map_frame' structure and stores the result in the given {@link Context}
   * object. This method can also be used to read a full_frame structure, excluding its frame_type
   * field (this is used to parse the legacy StackMap attributes).
   *
   * @param stackMapFrameOffset the start offset in {@link #b} of the stack_map_frame_value
   *     structure to be read, or the start offset of a full_frame structure (excluding its
   *     frame_type field).
   * @param compressed true to read a 'stack_map_frame' structure, false to read a 'full_frame'
   *     structure without its frame_type field.
   * @param expand if the stack map frame must be expanded. See {@link #EXPAND_FRAMES}.
   * @param context where the parsed stack map frame must be stored.
   * @return the end offset of the JVMS 'stack_map_frame' or 'full_frame' structure.
   */
  private int readStackMapFrame(
      int stackMapFrameOffset,
      final boolean compressed,
      final boolean expand,
      final Context context) {
    int currentOffset = stackMapFrameOffset;
    final char[] charBuffer = context.charBuffer;
    final Label[] labels = context.currentMethodLabels;
    int frameType;
    if (compressed) {
      // Read the frame_type field.
      frameType = b[currentOffset++] & 0xFF;
    } else {
      frameType = Frame.FULL_FRAME;
      context.currentFrameOffset = -1;
    }
    int offsetDelta;
    context.currentFrameLocalCountDelta = 0;
    if (frameType < Frame.SAME_LOCALS_1_STACK_ITEM_FRAME) {
      offsetDelta = frameType;
      context.currentFrameType = Opcodes.F_SAME;
      context.currentFrameStackCount = 0;
    } else if (frameType < Frame.RESERVED) {
      offsetDelta = frameType - Frame.SAME_LOCALS_1_STACK_ITEM_FRAME;
      currentOffset =
          readVerificationTypeInfo(
              currentOffset, context.currentFrameStackTypes, 0, charBuffer, labels);
      context.currentFrameType = Opcodes.F_SAME1;
      context.currentFrameStackCount = 1;
    } else {
      offsetDelta = readUnsignedShort(currentOffset);
      currentOffset += 2;
      if (frameType == Frame.SAME_LOCALS_1_STACK_ITEM_FRAME_EXTENDED) {
        currentOffset =
            readVerificationTypeInfo(
                currentOffset, context.currentFrameStackTypes, 0, charBuffer, labels);
        context.currentFrameType = Opcodes.F_SAME1;
        context.currentFrameStackCount = 1;
      } else if (frameType >= Frame.CHOP_FRAME && frameType < Frame.SAME_FRAME_EXTENDED) {
        context.currentFrameType = Opcodes.F_CHOP;
        context.currentFrameLocalCountDelta = Frame.SAME_FRAME_EXTENDED - frameType;
        context.currentFrameLocalCount -= context.currentFrameLocalCountDelta;
        context.currentFrameStackCount = 0;
      } else if (frameType == Frame.SAME_FRAME_EXTENDED) {
        context.currentFrameType = Opcodes.F_SAME;
        context.currentFrameStackCount = 0;
      } else if (frameType < Frame.FULL_FRAME) {
        int local = expand ? context.currentFrameLocalCount : 0;
        for (int k = frameType - Frame.SAME_FRAME_EXTENDED; k > 0; k--) {
          currentOffset =
              readVerificationTypeInfo(
                  currentOffset, context.currentFrameLocalTypes, local++, charBuffer, labels);
        }
        context.currentFrameType = Opcodes.F_APPEND;
        context.currentFrameLocalCountDelta = frameType - Frame.SAME_FRAME_EXTENDED;
        context.currentFrameLocalCount += context.currentFrameLocalCountDelta;
        context.currentFrameStackCount = 0;
      } else { // if (tag == FULL_FRAME) {
        final int numberOfLocals = readUnsignedShort(currentOffset);
        currentOffset += 2;
        context.currentFrameType = Opcodes.F_FULL;
        context.currentFrameLocalCountDelta = numberOfLocals;
        context.currentFrameLocalCount = numberOfLocals;
        for (int local = 0; local < numberOfLocals; ++local) {
          currentOffset =
              readVerificationTypeInfo(
                  currentOffset, context.currentFrameLocalTypes, local, charBuffer, labels);
        }
        final int numberOfStackItems = readUnsignedShort(currentOffset);
        currentOffset += 2;
        context.currentFrameStackCount = numberOfStackItems;
        for (int stack = 0; stack < numberOfStackItems; ++stack) {
          currentOffset =
              readVerificationTypeInfo(
                  currentOffset, context.currentFrameStackTypes, stack, charBuffer, labels);
        }
      }
    }
    context.currentFrameOffset += offsetDelta + 1;
    createLabel(context.currentFrameOffset, labels);
    return currentOffset;
  }

  /**
   * Reads a JVMS 'verification_type_info' structure and stores it at the given index in the given
   * array.
   *
   * @param verificationTypeInfoOffset the start offset of the 'verification_type_info' structure to
   *     read.
   * @param frame the array where the parsed type must be stored.
   * @param index the index in 'frame' where the parsed type must be stored.
   * @param charBuffer the buffer used to read strings in the constant pool.
   * @param labels the labels of the method currently being parsed, indexed by their offset. If the
   *     parsed type is an ITEM_Uninitialized, a new label for the corresponding NEW instruction is
   *     stored in this array if it does not already exist.
   * @return the end offset of the JVMS 'verification_type_info' structure.
   */
  private int readVerificationTypeInfo(
      final int verificationTypeInfoOffset,
      final Object[] frame,
      final int index,
      final char[] charBuffer,
      final Label[] labels) {
    int currentOffset = verificationTypeInfoOffset;
    int tag = b[currentOffset++] & 0xFF;
    switch (tag) {
      case Frame.ITEM_TOP:
        frame[index] = Opcodes.TOP;
        break;
      case Frame.ITEM_INTEGER:
        frame[index] = Opcodes.INTEGER;
        break;
      case Frame.ITEM_FLOAT:
        frame[index] = Opcodes.FLOAT;
        break;
      case Frame.ITEM_DOUBLE:
        frame[index] = Opcodes.DOUBLE;
        break;
      case Frame.ITEM_LONG:
        frame[index] = Opcodes.LONG;
        break;
      case Frame.ITEM_NULL:
        frame[index] = Opcodes.NULL;
        break;
      case Frame.ITEM_UNINITIALIZED_THIS:
        frame[index] = Opcodes.UNINITIALIZED_THIS;
        break;
      case Frame.ITEM_OBJECT:
        frame[index] = readClass(currentOffset, charBuffer);
        currentOffset += 2;
        break;
      case Frame.ITEM_UNINITIALIZED:
      default:
        frame[index] = createLabel(readUnsignedShort(currentOffset), labels);
        currentOffset += 2;
    }
    return currentOffset;
  }

  // ----------------------------------------------------------------------------------------------
  // Methods to parse attributes
  // ----------------------------------------------------------------------------------------------

  /** @return the offset in {@link #b} of the first ClassFile's 'attributes' array field entry. */
  final int getFirstAttributeOffset() {
    // Skip the access_flags, this_class, super_class, and interfaces_count fields (using 2 bytes
    // each), as well as the interfaces array field (2 bytes per interface).
    int currentOffset = header + 8 + readUnsignedShort(header + 6) * 2;

    // Read the fields_count field.
    int fieldsCount = readUnsignedShort(currentOffset);
    currentOffset += 2;
    // Skip the 'fields' array field.
    while (fieldsCount-- > 0) {
      // Invariant: currentOffset is the offset of a field_info structure.
      // Skip the access_flags, name_index and descriptor_index fields (2 bytes each), and read the
      // attributes_count field.
      int attributesCount = readUnsignedShort(currentOffset + 6);
      currentOffset += 8;
      // Skip the 'attributes' array field.
      while (attributesCount-- > 0) {
        // Invariant: currentOffset is the offset of an attribute_info structure.
        // Read the attribute_length field (2 bytes after the start of the attribute_info) and skip
        // this many bytes, plus 6 for the attribute_name_index and attribute_length fields
        // (yielding the total size of the attribute_info structure).
        currentOffset += 6 + readInt(currentOffset + 2);
      }
    }

    // Skip the methods_count and 'methods' fields, using the same method as above.
    int methodsCount = readUnsignedShort(currentOffset);
    currentOffset += 2;
    while (methodsCount-- > 0) {
      int attributesCount = readUnsignedShort(currentOffset + 6);
      currentOffset += 8;
      while (attributesCount-- > 0) {
        currentOffset += 6 + readInt(currentOffset + 2);
      }
    }

    // Skip the ClassFile's attributes_count field.
    return currentOffset + 2;
  }

  /**
   * Reads a non standard JVMS 'attribute' structure in {@link #b}.
   *
   * @param attributePrototypes prototypes of the attributes that must be parsed during the visit of
   *     the class. Any attribute whose type is not equal to the type of one the prototypes will not
   *     be parsed: its byte array value will be passed unchanged to the ClassWriter.
   * @param type the type of the attribute.
   * @param offset the start offset of the JVMS 'attribute' structure in {@link #b}. The 6 attribute
   *     header bytes (attribute_name_index and attribute_length) are not taken into account here.
   * @param length the length of the attribute's content (excluding the 6 attribute header bytes).
   * @param charBuffer the buffer to be used to read strings in the constant pool.
   * @param codeAttributeOffset the start offset of the enclosing Code attribute in {@link #b}, or
   *     -1 if the attribute to be read is not a code attribute. The 6 attribute header bytes
   *     (attribute_name_index and attribute_length) are not taken into account here.
   * @param labels the labels of the method's code, or <tt>null</tt> if the attribute to be read is
   *     not a code attribute.
   * @return the attribute that has been read.
   */
  private Attribute readAttribute(
      final Attribute[] attributePrototypes,
      final String type,
      final int offset,
      final int length,
      final char[] charBuffer,
      final int codeAttributeOffset,
      final Label[] labels) {
    for (int i = 0; i < attributePrototypes.length; ++i) {
      if (attributePrototypes[i].type.equals(type)) {
        return attributePrototypes[i].read(
            this, offset, length, charBuffer, codeAttributeOffset, labels);
      }
    }
    return new Attribute(type).read(this, offset, length, null, -1, null);
  }

  // -----------------------------------------------------------------------------------------------
  // Utility methods: low level parsing
  // -----------------------------------------------------------------------------------------------

  /**
   * Returns the number of entries in the class's constant pool table.
   *
   * @return the number of entries in the class's constant pool table.
   */
  public int getItemCount() {
    return cpInfoOffsets.length;
  }

  /**
   * Returns the start offset in {@link #b} of a JVMS 'cp_info' structure (i.e. a constant pool
   * entry), plus one. <i>This method is intended for {@link Attribute} sub classes, and is normally
   * not needed by class generators or adapters.</i>
   *
   * @param constantPoolEntryIndex the index a constant pool entry in the class's constant pool
   *     table.
   * @return the start offset in {@link #b} of the corresponding JVMS 'cp_info' structure, plus one.
   */
  public int getItem(final int constantPoolEntryIndex) {
    return cpInfoOffsets[constantPoolEntryIndex];
  }

  /**
   * Returns a conservative estimate of the maximum length of the strings contained in the class's
   * constant pool table.
   *
   * @return a conservative estimate of the maximum length of the strings contained in the class's
   *     constant pool table.
   */
  public int getMaxStringLength() {
    return maxStringLength;
  }

  /**
   * Reads a byte value in {@link #b}. <i>This method is intended for {@link Attribute} sub classes,
   * and is normally not needed by class generators or adapters.</i>
   *
   * @param offset the start offset of the value to be read in {@link #b}.
   * @return the read value.
   */
  public int readByte(final int offset) {
    return b[offset] & 0xFF;
  }

  /**
   * Reads an unsigned short value in {@link #b}. <i>This method is intended for {@link Attribute}
   * sub classes, and is normally not needed by class generators or adapters.</i>
   *
   * @param offset the start index of the value to be read in {@link #b}.
   * @return the read value.
   */
  public int readUnsignedShort(final int offset) {
    byte[] b = this.b;
    return ((b[offset] & 0xFF) << 8) | (b[offset + 1] & 0xFF);
  }

  /**
   * Reads a signed short value in {@link #b}. <i>This method is intended for {@link Attribute} sub
   * classes, and is normally not needed by class generators or adapters.</i>
   *
   * @param offset the start offset of the value to be read in {@link #b}.
   * @return the read value.
   */
  public short readShort(final int offset) {
    byte[] b = this.b;
    return (short) (((b[offset] & 0xFF) << 8) | (b[offset + 1] & 0xFF));
  }

  /**
   * Reads a signed int value in {@link #b}. <i>This method is intended for {@link Attribute} sub
   * classes, and is normally not needed by class generators or adapters.</i>
   *
   * @param offset the start offset of the value to be read in {@link #b}.
   * @return the read value.
   */
  public int readInt(final int offset) {
    byte[] b = this.b;
    return ((b[offset] & 0xFF) << 24)
        | ((b[offset + 1] & 0xFF) << 16)
        | ((b[offset + 2] & 0xFF) << 8)
        | (b[offset + 3] & 0xFF);
  }

  /**
   * Reads a signed long value in {@link #b}. <i>This method is intended for {@link Attribute} sub
   * classes, and is normally not needed by class generators or adapters.</i>
   *
   * @param offset the start offset of the value to be read in {@link #b}.
   * @return the read value.
   */
  public long readLong(final int offset) {
    long l1 = readInt(offset);
    long l0 = readInt(offset + 4) & 0xFFFFFFFFL;
    return (l1 << 32) | l0;
  }

  /**
   * Reads a CONSTANT_Utf8 constant pool entry in {@link #b}. <i>This method is intended for {@link
   * Attribute} sub classes, and is normally not needed by class generators or adapters.</i>
   *
   * @param offset the start offset of an unsigned short value in {@link #b}, whose value is the
   *     index of a CONSTANT_Utf8 entry in the class's constant pool table.
   * @param charBuffer the buffer to be used to read the string. This buffer must be sufficiently
   *     large. It is not automatically resized.
   * @return the String corresponding to the specified CONSTANT_Utf8 entry.
   */
  public String readUTF8(final int offset, final char[] charBuffer) {
    int constantPoolEntryIndex = readUnsignedShort(offset);
    if (offset == 0 || constantPoolEntryIndex == 0) {
      return null;
    }
    return readUTF(constantPoolEntryIndex, charBuffer);
  }

  /**
   * Reads a CONSTANT_Utf8 constant pool entry in {@link #b}.
   *
   * @param constantPoolEntryIndex the index of a CONSTANT_Utf8 entry in the class's constant pool
   *     table.
   * @param charBuffer the buffer to be used to read the string. This buffer must be sufficiently
   *     large. It is not automatically resized.
   * @return the String corresponding to the specified CONSTANT_Utf8 entry.
   */
  final String readUTF(int constantPoolEntryIndex, final char[] charBuffer) {
    String value = constantUtf8Values[constantPoolEntryIndex];
    if (value != null) {
      return value;
    }
    int cpInfoOffset = cpInfoOffsets[constantPoolEntryIndex];
    return constantUtf8Values[constantPoolEntryIndex] =
        readUTF(cpInfoOffset + 2, readUnsignedShort(cpInfoOffset), charBuffer);
  }

  /**
   * Reads an UTF8 string in {@link #b}.
   *
   * @param utfOffset the start offset of the UTF8 string to be read.
   * @param utfLength the length of the UTF8 string to be read.
   * @param charBuffer the buffer to be used to read the string. This buffer must be sufficiently
   *     large. It is not automatically resized.
   * @return the String corresponding to the specified UTF8 string.
   */
  private String readUTF(final int utfOffset, final int utfLength, final char[] charBuffer) {
    int currentOffset = utfOffset;
    int endOffset = currentOffset + utfLength;
    int strLength = 0;
    byte[] b = this.b;
    while (currentOffset < endOffset) {
      int currentByte = b[currentOffset++];
      if ((currentByte & 0x80) == 0) {
        charBuffer[strLength++] = (char) (currentByte & 0x7F);
      } else if ((currentByte & 0xE0) == 0xC0) {
        charBuffer[strLength++] =
            (char) (((currentByte & 0x1F) << 6) + (b[currentOffset++] & 0x3F));
      } else {
        charBuffer[strLength++] =
            (char)
                (((currentByte & 0xF) << 12)
                    + ((b[currentOffset++] & 0x3F) << 6)
                    + (b[currentOffset++] & 0x3F));
      }
    }
    return new String(charBuffer, 0, strLength);
  }

  /**
   * Reads a CONSTANT_Class, CONSTANT_String, CONSTANT_MethodType, CONSTANT_Module or
   * CONSTANT_Package constant pool entry in {@link #b}. <i>This method is intended for {@link
   * Attribute} sub classes, and is normally not needed by class generators or adapters.</i>
   *
   * @param offset the start offset of an unsigned short value in {@link #b}, whose value is the
   *     index of a CONSTANT_Class, CONSTANT_String, CONSTANT_MethodType, CONSTANT_Module or
   *     CONSTANT_Package entry in class's constant pool table.
   * @param charBuffer the buffer to be used to read the item. This buffer must be sufficiently
   *     large. It is not automatically resized.
   * @return the String corresponding to the specified constant pool entry.
   */
  private String readStringish(final int offset, final char[] charBuffer) {
    // Get the start offset of the cp_info structure (plus one), and read the CONSTANT_Utf8 entry
    // designated by the first two bytes of this cp_info.
    return readUTF8(cpInfoOffsets[readUnsignedShort(offset)], charBuffer);
  }

  /**
   * Reads a CONSTANT_Class constant pool entry in {@link #b}. <i>This method is intended for {@link
   * Attribute} sub classes, and is normally not needed by class generators or adapters.</i>
   *
   * @param offset the start offset of an unsigned short value in {@link #b}, whose value is the
   *     index of a CONSTANT_Class entry in class's constant pool table.
   * @param charBuffer the buffer to be used to read the item. This buffer must be sufficiently
   *     large. It is not automatically resized.
   * @return the String corresponding to the specified CONSTANT_Class entry.
   */
  public String readClass(final int offset, final char[] charBuffer) {
    return readStringish(offset, charBuffer);
  }

  /**
   * Reads a CONSTANT_Module constant pool entry in {@link #b}. <i>This method is intended for
   * {@link Attribute} sub classes, and is normally not needed by class generators or adapters.</i>
   *
   * @param offset the start offset of an unsigned short value in {@link #b}, whose value is the
   *     index of a CONSTANT_Module entry in class's constant pool table.
   * @param charBuffer the buffer to be used to read the item. This buffer must be sufficiently
   *     large. It is not automatically resized.
   * @return the String corresponding to the specified CONSTANT_Module entry.
   */
  public String readModule(final int offset, final char[] charBuffer) {
    return readStringish(offset, charBuffer);
  }

  /**
   * Reads a CONSTANT_Package constant pool entry in {@link #b}. <i>This method is intended for
   * {@link Attribute} sub classes, and is normally not needed by class generators or adapters.</i>
   *
   * @param offset the start offset of an unsigned short value in {@link #b}, whose value is the
   *     index of a CONSTANT_Package entry in class's constant pool table.
   * @param charBuffer the buffer to be used to read the item. This buffer must be sufficiently
   *     large. It is not automatically resized.
   * @return the String corresponding to the specified CONSTANT_Package entry.
   */
  public String readPackage(final int offset, final char[] charBuffer) {
    return readStringish(offset, charBuffer);
  }

  /**
   * Reads a numeric or string constant pool entry in {@link #b}. <i>This method is intended for
   * {@link Attribute} sub classes, and is normally not needed by class generators or adapters.</i>
   *
   * @param constantPoolEntryIndex the index of a CONSTANT_Integer, CONSTANT_Float, CONSTANT_Long,
   *     CONSTANT_Double, CONSTANT_Class, CONSTANT_String, CONSTANT_MethodType or
   *     CONSTANT_MethodHandle entry in the class's constant pool.
   * @param charBuffer the buffer to be used to read strings. This buffer must be sufficiently
   *     large. It is not automatically resized.
   * @return the {@link Integer}, {@link Float}, {@link Long}, {@link Double}, {@link String},
   *     {@link Type} or {@link Handle} corresponding to the specified constant pool entry.
   */
  public Object readConst(final int constantPoolEntryIndex, final char[] charBuffer) {
    int cpInfoOffset = cpInfoOffsets[constantPoolEntryIndex];
    switch (b[cpInfoOffset - 1]) {
      case Symbol.CONSTANT_INTEGER_TAG:
        return readInt(cpInfoOffset);
      case Symbol.CONSTANT_FLOAT_TAG:
        return Float.intBitsToFloat(readInt(cpInfoOffset));
      case Symbol.CONSTANT_LONG_TAG:
        return readLong(cpInfoOffset);
      case Symbol.CONSTANT_DOUBLE_TAG:
        return Double.longBitsToDouble(readLong(cpInfoOffset));
      case Symbol.CONSTANT_CLASS_TAG:
        return Type.getObjectType(readUTF8(cpInfoOffset, charBuffer));
      case Symbol.CONSTANT_STRING_TAG:
        return readUTF8(cpInfoOffset, charBuffer);
      case Symbol.CONSTANT_METHOD_TYPE_TAG:
        return Type.getMethodType(readUTF8(cpInfoOffset, charBuffer));
      case Symbol.CONSTANT_METHOD_HANDLE_TAG:
        int referenceKind = readByte(cpInfoOffset);
        int referenceCpInfoOffset = cpInfoOffsets[readUnsignedShort(cpInfoOffset + 1)];
        int nameAndTypeCpInfoOffset = cpInfoOffsets[readUnsignedShort(referenceCpInfoOffset + 2)];
        String owner = readClass(referenceCpInfoOffset, charBuffer);
        String name = readUTF8(nameAndTypeCpInfoOffset, charBuffer);
        String desc = readUTF8(nameAndTypeCpInfoOffset + 2, charBuffer);
        boolean itf = b[referenceCpInfoOffset - 1] == Symbol.CONSTANT_INTERFACE_METHODREF_TAG;
        return new Handle(referenceKind, owner, name, desc, itf);
      default:
        throw new AssertionError();
    }
  }
}
