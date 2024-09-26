package net.momirealms.customnameplates.common.util;

public class OSUtils {

    public static String getOSName() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return "windows";
        } else if (os.contains("mac")) {
            return "macos";
        } else if (os.contains("nux") || os.contains("nix")) {
            return "linux";
        } else if (os.contains("freebsd")) {
            return "freebsd";
        } else {
            return "unknown";
        }
    }
}
