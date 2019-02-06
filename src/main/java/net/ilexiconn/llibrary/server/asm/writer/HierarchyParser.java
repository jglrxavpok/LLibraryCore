package net.ilexiconn.llibrary.server.asm.writer;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

public enum HierarchyParser {
    INSTANCE;

    private final LoadingCache<String, ClassHierarchy> hierarchyCache = CacheBuilder.newBuilder()
            .maximumSize(32)
            .build(new CacheLoader<String, ClassHierarchy>() {
                @Override
                public ClassHierarchy load(String type) {
                    LaunchClassLoader classLoader = Launch.classLoader;
                    return ClassHierarchy.build(type, classLoader, classLoader::getClassBytes);
                }
            });

    public ClassHierarchy get(String type) {
        return this.hierarchyCache.getUnchecked(type);
    }
}
