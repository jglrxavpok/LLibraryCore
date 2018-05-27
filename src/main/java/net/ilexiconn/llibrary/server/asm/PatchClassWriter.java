package net.ilexiconn.llibrary.server.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

/**
 * A {@link ClassWriter} implementation that replaces the classloader used in getCommonSuperClass, allowing the
 * COMPUTE_FRAMES flag to work properly.
 */
public class PatchClassWriter extends ClassWriter {
    public PatchClassWriter(int flags) {
        super(flags);
    }

    public PatchClassWriter(ClassReader classReader, int flags) {
        super(classReader, flags);
    }

    @Override
    protected String getCommonSuperClass(String type1, String type2) {
        return "java/lang/Object";
    }
}
