package net.ilexiconn.llibrary.server.asm.writer;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import cpw.mods.modlauncher.TransformingClassLoader;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URL;

public enum HierarchyParser {
    INSTANCE;

    private final LoadingCache<String, ClassHierarchy> hierarchyCache = CacheBuilder.newBuilder()
            .maximumSize(32)
            .build(new CacheLoader<String, ClassHierarchy>() {
                @Override
                public ClassHierarchy load(String type) {
                    TransformingClassLoader classLoader = FMLLoader.getLaunchClassLoader();
                    return ClassHierarchy.build(type, classLoader, HierarchyParser::bytesFetcher);
                }
            });

    private static byte[] bytesFetcher(String type) {
        TransformingClassLoader classLoader = FMLLoader.getLaunchClassLoader();
        URL resource = classLoader.getResource(type+".class");
        try {
            return IOUtils.toByteArray(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ClassHierarchy get(String type) {
        return this.hierarchyCache.getUnchecked(type);
    }
}
