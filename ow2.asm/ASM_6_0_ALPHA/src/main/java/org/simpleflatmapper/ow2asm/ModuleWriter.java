/***
 * ASM: a very small and fast Java bytecode manipulation framework
 * Copyright (c) 2000-2011 INRIA, France Telecom
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.simpleflatmapper.ow2asm;

/**
 * @author Remi Forax
 */
final class ModuleWriter extends ModuleVisitor {
    /**
     * The class writer to which this Module attribute must be added.
     */
    private final ClassWriter cw;
    
    /**
     * size in byte of the corresponding Module attribute.
     */
    private int size;
    
    /**
     * number of requires items
     */
    private int requireCount;
    
    /**
     * The requires items in bytecode form. This byte vector only contains
     * the items themselves, the number of items is store in requireCount
     */
    private ByteVector requires;
    
    /**
     * number of exports items
     */
    private int exportCount;
    
    /**
     * The exports items in bytecode form. This byte vector only contains
     * the items themselves, the number of items is store in exportCount
     */
    private ByteVector exports;
    
    /**
     * number of uses items
     */
    private int useCount;
    
    /**
     * The uses items in bytecode form. This byte vector only contains
     * the items themselves, the number of items is store in useCount
     */
    private ByteVector uses;
    
    /**
     * number of provides items
     */
    private int provideCount;
    
    /**
     * The uses provides in bytecode form. This byte vector only contains
     * the items themselves, the number of items is store in provideCount
     */
    private ByteVector provides;
    
    ModuleWriter(final ClassWriter cw) {
        super(Opcodes.ASM6);
        this.cw = cw;
        this.size = 8;
    }
    
    @Override
    public void visitRequire(String module, int access) {
        if (requires == null) {
            requires = new ByteVector();
        }
        //FIXME fix bad ACC_PUBLIC value (0x0020)
        if ((access & Opcodes.ACC_PUBLIC) != 0) {
            access = access & ~ Opcodes.ACC_PUBLIC | 0x0020;
        }
        requires.putShort(cw.newUTF8(module)).putShort(access);
        requireCount++;
        size += 4;
    }
    
    @Override
    public void visitExport(String packaze, String... modules) {
        if (exports == null) {
            exports = new ByteVector();
        }
        exports.putShort(cw.newUTF8(packaze));
        if (modules == null) {
            exports.putShort(0);
            size += 4;
        } else {
            exports.putShort(modules.length);
            for(String to: modules) {
                exports.putShort(cw.newUTF8(to));
            }    
            size += 4 + 2 * modules.length; 
        }
        exportCount++;
    }
    
    @Override
    public void visitUse(String service) {
        if (uses == null) {
            uses = new ByteVector();
        }
        uses.putShort(cw.newClass(service));
        useCount++;
        size += 2;
    }
    
    @Override
    public void visitProvide(String service, String impl) {
        if (provides == null) {
            provides = new ByteVector();
        }
        provides.putShort(cw.newClass(service)).putShort(cw.newClass(impl));
        provideCount++;
        size += 4;
    }
    
    @Override
    public void visitEnd() {
        // empty
    }

    int getSize() {
        return size;
    }

    void put(ByteVector out) {
        out.putInt(size);
        out.putShort(requireCount);
        if (requires != null) {
            out.putByteArray(requires.data, 0, requires.length);
        }
        out.putShort(exportCount);
        if (exports != null) {
            out.putByteArray(exports.data, 0, exports.length);
        }
        out.putShort(useCount);
        if (uses != null) {
            out.putByteArray(uses.data, 0, uses.length);
        }
        out.putShort(provideCount);
        if (provides != null) {
            out.putByteArray(provides.data, 0, provides.length);
        }
    }
}
