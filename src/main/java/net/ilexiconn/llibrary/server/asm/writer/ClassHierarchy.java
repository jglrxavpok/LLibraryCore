package net.ilexiconn.llibrary.server.asm.writer;

import net.ilexiconn.llibrary.server.core.plugin.LLibraryPlugin;
import net.minecraft.launchwrapper.Launch;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ClassHierarchy {
    private static Method findLoadedClass;

    static {
        try {
            findLoadedClass = ClassLoader.class.getDeclaredMethod("findLoadedClass", String.class);
            findLoadedClass.setAccessible(true);
        } catch (ReflectiveOperationException e) {
            LLibraryPlugin.LOGGER.error("Failed to get findLoadedClass method", e);
        }
    }

    private final HierarchyNode root;

    ClassHierarchy(HierarchyNode root) {
        this.root = root;
    }

    public static ClassHierarchy build(String type, ClassLoader classLoader, RawClassFetcher fetcher) {
        return new ClassHierarchy(fetchNode(type.replace('/', '.'), classLoader, fetcher));
    }

    private static HierarchyNode fetchNode(String type, ClassLoader classLoader, RawClassFetcher fetcher) {
        Class<?> loadedClass = getLoadedClass(type);
        if (loadedClass != null) {
            return readClassNode(loadedClass);
        }

        HierarchyNode rawClassNode = readRawClass(type, classLoader, fetcher);
        if (rawClassNode != null) {
            return rawClassNode;
        }

        // It's not loaded and we can't find raw bytes. Force load the class as a last option
        try {
            return readClassNode(classLoader.loadClass(type));
        } catch (ClassNotFoundException e) {
            LLibraryPlugin.LOGGER.error("Failed to force load class {}", type, e);
        }

        return new HierarchyNode(type, false);
    }

    private static HierarchyNode readClassNode(Class<?> type) {
        HierarchyNode node = new HierarchyNode(type.getName(), type.isInterface());

        Class<?> superclass = type.getSuperclass();
        if (superclass != null) {
            node.add(readClassNode(superclass));
        }

        Class<?>[] interfaces = type.getInterfaces();
        for (Class<?> interfaceType : interfaces) {
            node.add(readClassNode(interfaceType));
        }

        return node;
    }

    @Nullable
    private static HierarchyNode readRawClass(String type, ClassLoader classLoader, RawClassFetcher fetcher) {
        try {
            byte[] rawBytes = fetcher.fetch(type.replace('.', '/'));
            if (rawBytes != null) {
                ClassNode classNode = read(rawBytes);
                HierarchyNode node = new HierarchyNode(type, Modifier.isInterface(classNode.access));

                if (classNode.superName != null) {
                    node.add(fetchNode(classNode.superName.replace('/', '.'), classLoader, fetcher));
                }

                if (classNode.interfaces != null) {
                    for (String interfaceType : classNode.interfaces) {
                        node.add(fetchNode(interfaceType.replace('/', '.'), classLoader, fetcher));
                    }
                }
            }
        } catch (IOException e) {
            LLibraryPlugin.LOGGER.error("Failed to read bytes for class {}", type, e);
        }

        return null;
    }

    private static ClassNode read(byte[] bytes) {
        ClassNode node = new ClassNode();
        ClassReader reader = new ClassReader(bytes);
        reader.accept(node, 0);
        return node;
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

    public String findCommon(ClassHierarchy other) {
        if (other.root.instanceOf(this.root)) {
            return this.root.getType();
        } else if (this.root.instanceOf(other.root)) {
            return other.root.getType();
        } else if (this.root.isInterface() || other.root.isInterface()) {
            return "java.lang.Object";
        }

        HierarchyNode node = this.root;
        do {
            node = node.getSuper();
            if (node == null) {
                return "java.lang.Object";
            }
        } while (!other.root.instanceOf(node));

        return node.getType();
    }
}
