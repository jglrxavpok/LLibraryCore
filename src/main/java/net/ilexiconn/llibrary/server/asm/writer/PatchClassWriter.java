package net.ilexiconn.llibrary.server.asm.writer;

import net.ilexiconn.llibrary.server.core.plugin.LLibraryPlugin;
import net.minecraft.launchwrapper.Launch;
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
            RawClassFetcher fetcher = Launch.classLoader::getClassBytes;
            ClassHierarchy hierarchy1 = ClassHierarchy.build(type1, Launch.classLoader, fetcher);
            ClassHierarchy hierarchy2 = ClassHierarchy.build(type2, Launch.classLoader, fetcher);

            return hierarchy1.findCommon(hierarchy2).replace('.', '/');
        } catch (Exception e) {
            LLibraryPlugin.LOGGER.error("Failed to find common super class between {} and {}", type1, type2, e);
            return "java/lang/Object";
        }
    }
}
