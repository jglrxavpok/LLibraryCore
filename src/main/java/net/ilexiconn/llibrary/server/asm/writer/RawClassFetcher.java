package net.ilexiconn.llibrary.server.asm.writer;

import java.io.IOException;

public interface RawClassFetcher {
    byte[] fetch(String name) throws IOException;
}
