package net.ilexiconn.llibrary.server.core.plugin;

import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import net.ilexiconn.llibrary.server.asm.MappingHandler;
import net.ilexiconn.llibrary.server.core.api.LLibraryCoreAPI;
import net.minecraft.entity.Entity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

/*
@IFMLLoadingPlugin.Name("llibrary")
@IFMLLoadingPlugin.MCVersion("1.13.2")
@IFMLLoadingPlugin.SortingIndex(1001)
@IFMLLoadingPlugin.TransformerExclusions("net.ilexiconn.llibrary.server.asm")*/
public class LLibraryPlugin implements ILaunchPluginService {
    public static final Logger LOGGER = LogManager.getLogger("LLibrary Core");

    public static LLibraryCoreAPI api;

    public static boolean inDevelopment;

    private boolean loaded;

    @Override
    public String name() {
        return "llibrary";
    }

    @Override
    public void addResource(Path resource, String name) {
        if(!loaded) {
            loaded = true;
            // checks if we are in a deobfuscated environment
            LLibraryPlugin.inDevelopment = Entity.class.getCanonicalName().equals("net.minecraft.entity.Entity");
            try (InputStream input = this.getClass().getResourceAsStream("/llibrary.mappings")) {
                MappingHandler.INSTANCE.parseMappings(input);
            } catch (IOException e) {
                throw new RuntimeException("Failed to parse LLibrary mappings", e);
            }
        }
    }

    @Override
    public ClassNode processClass(ClassNode classNode, Type classType) {
        return classNode;
    }

    @Override
    public <T> T getExtension() {
        return null;
    }

    @Override
    public boolean handlesClass(Type classType, boolean isEmpty) {
        return false;
    }
}
