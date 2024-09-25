package net.momirealms.customnameplates.bukkit;

import net.momirealms.customnameplates.api.ConfigManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class BukkitConfigManager extends ConfigManager {

    private final BukkitCustomNameplates plugin;

    public BukkitConfigManager(BukkitCustomNameplates plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void saveResource(@NotNull String resourcePath) {
        if (resourcePath.isEmpty()) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }

        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = getResource(resourcePath);
        if (in == null) {
            return;
        }

        File outFile = new File(BukkitCustomNameplates.getInstance().getBootstrap().getDataFolder(), resourcePath);
        int lastIndex = resourcePath.lastIndexOf('/');
        File outDir = new File(BukkitCustomNameplates.getInstance().getBootstrap().getDataFolder(), resourcePath.substring(0, Math.max(lastIndex, 0)));

        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        try {
            if (!outFile.exists()) {
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Nullable
    public static InputStream getResource(@NotNull String filename) {
        try {
            URL url = BukkitCustomNameplates.getInstance().getBootstrap().getClass().getClassLoader().getResource(filename);
            if (url == null) {
                return null;
            }
            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException ex) {
            return null;
        }
    }
}
