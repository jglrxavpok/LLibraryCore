package net.ilexiconn.llibrary.server.asm.writer;

import net.ilexiconn.llibrary.server.core.plugin.LLibraryPlugin;
import net.minecraft.launchwrapper.Launch;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * A {@link ClassWriter} implementation that replaces the classloader used in getCommonSuperClass, allowing the
 * COMPUTE_FRAMES flag to work properly.
 */
public class PatchClassWriter extends ClassWriter {
    private static Method findLoadedClass;

    static {
        try {
            findLoadedClass = ClassLoader.class.getDeclaredMethod("findLoadedClass", String.class);
            findLoadedClass.setAccessible(true);
        } catch (ReflectiveOperationException e) {
            LLibraryPlugin.LOGGER.error("Failed to get findLoadedClass method", e);
        }
    }

    public PatchClassWriter(int flags) {
        super(flags);
    }

    public PatchClassWriter(ClassReader classReader, int flags) {
        super(classReader, flags);
    }

    @Override
    protected String getCommonSuperClass(String type1, String type2) {
        try {
            ClassHierarchy hierarchy1 = this.buildHierarchy(type1);
            ClassHierarchy hierarchy2 = this.buildHierarchy(type2);

            return hierarchy1.findCommon(hierarchy2).replace('.', '/');
        } catch (Exception e) {
            LLibraryPlugin.LOGGER.error("Failed to find common super class between {} and {}", type1, type2, e);
            return "java/lang/Object";
        }
    }

    private ClassHierarchy buildHierarchy(String type) throws IOException {
        String classType = type.replace('/', '.');
        Class<?> c = getLoadedClass(classType);
        if (c != null) {
            return ClassHierarchy.build(c);
        }
        return ClassHierarchy.build(classType, Launch.classLoader::getClassBytes);
    }

    @Nullable
    private static Class<?> getLoadedClass(String name) {
        if (findLoadedClass == null) {
            return null;
        }
        try {
            return (Class<?>) findLoadedClass.invoke(Launch.classLoader, name);
        } catch (ReflectiveOperationException e) {
            LLibraryPlugin.LOGGER.error("Failed to find loaded class", e);
            return null;
        }
    }
}
