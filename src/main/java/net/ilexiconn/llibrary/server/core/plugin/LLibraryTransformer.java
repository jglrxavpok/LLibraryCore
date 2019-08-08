package net.ilexiconn.llibrary.server.core.plugin;

import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.nio.file.Path;

public class LLibraryTransformer implements ILaunchPluginService {
    private static final String RUNTIME_PATCHER = "RuntimePatcher";

    @Override
    public ClassNode processClass(ClassNode classNode, Type classType) {
        LLibraryPlugin.LOGGER.debug("Found runtime patcher {}", classType.getClassName());
        for (MethodNode methodNode : classNode.methods) {
            InsnList insnList = methodNode.instructions;
            for (AbstractInsnNode node = insnList.getFirst(); node != null; node = node.getNext()) {
                if (node.getOpcode() == Opcodes.LDC) {
                    LdcInsnNode ldc = (LdcInsnNode) node;
                    if (ldc.cst instanceof Type) {
                        ldc.cst = ((Type) ldc.cst).getClassName();
                    }
                }
            }
        }

        classNode.visitAnnotation("Lnet/ilexiconn/llibrary/server/asm/Transformed;", true);
        return classNode;
    }

    @Override
    public String name() {
        return RUNTIME_PATCHER+"-Patcher, LLibrary Core";
    }

    @Override
    public void addResource(Path resource, String name) {

    }

    @Override
    public <T> T getExtension() {
        return null;
    }

    @Override
    public boolean handlesClass(Type classType, boolean isEmpty) {
        String name = classType.getClassName();
        return !name.startsWith("$") && name.contains(RUNTIME_PATCHER);
    }
}
