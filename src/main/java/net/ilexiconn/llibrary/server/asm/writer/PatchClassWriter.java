package net.ilexiconn.llibrary.server.asm.writer;

import net.ilexiconn.llibrary.server.core.plugin.LLibraryPlugin;
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
        try {
            ClassHierarchy hierarchy1 = HierarchyParser.INSTANCE.get(type1);
            ClassHierarchy hierarchy2 = HierarchyParser.INSTANCE.get(type2);

            return hierarchy1.findCommon(hierarchy2).replace('.', '/');
        } catch (Throwable t) {
            LLibraryPlugin.LOGGER.error("Failed to find common super class between {} and {}", type1, type2, t);
            return "java/lang/Object";
        }
    }
}
