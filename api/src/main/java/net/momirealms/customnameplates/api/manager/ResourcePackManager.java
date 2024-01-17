package net.momirealms.customnameplates.api.manager;

import java.io.File;

public interface ResourcePackManager {

    /**
     * Generate the resource pack
     */
    void generateResourcePack();

    void deleteDirectory(File file);

    String native2ascii(char c);
}
