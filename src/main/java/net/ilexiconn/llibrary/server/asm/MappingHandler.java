package net.ilexiconn.llibrary.server.asm;

import net.ilexiconn.llibrary.server.core.plugin.LLibraryPlugin;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public enum MappingHandler {
    INSTANCE;

    private final Map<String, String> fields = new HashMap<>();
    private final Map<String, String> methods = new HashMap<>();

    public void parseMappings(InputStream stream) throws IOException {
        if (stream == null) {
            throw new IOException("Could not find LLibrary mappings file!");
        }

        this.fields.clear();
        this.methods.clear();

        try (DataInputStream input = new DataInputStream(new BufferedInputStream(stream))) {
            int classCount = input.readShort();
            for (int i = 0; i < classCount; i++) {
                String className = input.readUTF();
                int fieldCount = input.readShort();
                int methodCount = input.readShort();
                for (int f = 0; f < fieldCount; f++) {
                    String[] field = input.readUTF().split("=");
                    this.fields.put(className + "/" + field[0], field[1]);
                }
                for (int m = 0; m < methodCount; m++) {
                    String[] method = input.readUTF().split("=");
                    this.methods.put(className + "/" + method[0], method[1]);
                }
            }
        }
    }

    public String getClassMapping(String cls) {
        return cls.replace(".", "/");
    }

    public String getClassMapping(Object obj) {
        if (obj instanceof String) {
            return this.getClassMapping((String) obj);
        } else if (obj instanceof Class) {
            return ((Class) obj).getName();
        }
        return "";
    }

    public String getMethodMapping(Object obj, String method, String desc) {
        if (LLibraryPlugin.inDevelopment) {
            return method;
        }
        String cls = this.getClassMapping(obj);
        String key = cls + "/" + method + desc;
        String mapping = this.methods.get(key);
        if (mapping == null) {
            LLibraryPlugin.LOGGER.error("Failed to get method mapping for {}", key);
            return method;
        }
        return mapping;
    }

    public String getFieldMapping(Object obj, String field) {
        if (LLibraryPlugin.inDevelopment) {
            return field;
        }
        String cls = this.getClassMapping(obj);
        String key = cls + "/" + field;
        String mapping = this.fields.getOrDefault(key, field);
        if (mapping == null) {
            LLibraryPlugin.LOGGER.error("Failed to get field mapping for {}", key);
            return field;
        }
        return mapping;
    }
}
