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
 * A visitor to visit a Java module. The methods of this class must be called in
 * the following order: ( <tt>visitRequire</tt> | <tt>visitExport</tt> |
 * <tt>visitUse</tt> | <tt>visitProvide</tt> )* <tt>visitEnd</tt>.
 * 
 * @author Remi Forax
 */
public abstract class ModuleVisitor {
    /**
     * The ASM API version implemented by this visitor. The value of this field
     * must be {@link Opcodes#ASM6}.
     */
    protected final int api;
    
    /**
     * The module visitor to which this visitor must delegate method calls. May
     * be null.
     */
    protected ModuleVisitor mv;
    
    
    public ModuleVisitor(final int api) {
        this(api, null);
    }

    /**
     * Constructs a new {@link MethodVisitor}.
     * 
     * @param api
     *            the ASM API version implemented by this visitor. Must be {@link Opcodes#ASM6}.
     * @param mv
     *            the method visitor to which this visitor must delegate method
     *            calls. May be null.
     */
    public ModuleVisitor(final int api, final ModuleVisitor mv) {
        if (api != Opcodes.ASM6) {
            throw new IllegalArgumentException();
        }
        this.api = api;
        this.mv = mv;
    }
    
    /**
     * Visits a dependence of the current module.
     * 
     * @param module the module name of the dependence
     * @param access the access flag of the dependence among
     *        ACC_PUBLIC, ACC_SYNTHETIC and ACC_MANDATED.
     */
    public void visitRequire(String module, int access) {
        if (mv != null) {
            mv.visitRequire(module, access);
        }
    }
    
    /**
     * Visit an exported package of the current module.
     * 
     * @param packaze the name of the exported package.
     * @param modules names of the modules that can access to
     *        the public classes of the exported package or
     *        <tt>null</tt>.
     */
    public void visitExport(String packaze, String... modules) {
        if (mv != null) {
            mv.visitExport(packaze, modules);
        }
    }
    
    /**
     * Visit a service used by the current module.
     * The name must be the name of an interface or an
     * abstract class.
     * 
     * @param service the internal name of the service.
     */
    public void visitUse(String service) {
        if (mv != null) {
            mv.visitUse(service);
        }
    }
    
    /**
     * Visit an implementation of a service.
     * 
     * @param service the internal name of the service
     * @param impl the internal name of the implementation
     *        of the service
     */
    public void visitProvide(String service, String impl) {
        if (mv != null) {
            mv.visitProvide(service, impl);
        }
    }
    
    public void visitEnd() {
        if (mv != null) {
            mv.visitEnd();
        }
    }
}
