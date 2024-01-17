/*
 * This file is part of helper, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.momirealms.customnameplates.paper.helper;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.util.LogUtils;
import net.momirealms.customnameplates.paper.CustomNameplatesPluginImpl;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Resolves {@link MavenLibrary} annotations for a class, and loads the dependency
 * into the classloader.
 */
public final class LibraryLoader {

    @SuppressWarnings("Guava")
    private static final Supplier<URLClassLoaderAccess> URL_INJECTOR = Suppliers.memoize(() -> URLClassLoaderAccess.create((URLClassLoader) CustomNameplatesPluginImpl.getInstance().getClass().getClassLoader()));

    /**
     * Resolves all {@link MavenLibrary} annotations on the given object.
     *
     * @param object the object to load libraries for.
     */
    public static void loadAll(Object object) {
        loadAll(object.getClass());
    }

    /**
     * Resolves all {@link MavenLibrary} annotations on the given class.
     *
     * @param clazz the class to load libraries for.
     */
    public static void loadAll(Class<?> clazz) {
        MavenLibrary[] libs = clazz.getDeclaredAnnotationsByType(MavenLibrary.class);
        for (MavenLibrary lib : libs) {
            load(lib.groupId(), lib.artifactId(), lib.version(), lib.repo().url());
        }
    }

    public static void load(String groupId, String artifactId, String version, String repoUrl) {
        load(new Dependency(groupId, artifactId, version, repoUrl));
    }

    public static void loadDependencies(String... libs) {
        if (libs == null || libs.length % 2 != 0)
            return;
        for (int i = 0; i < libs.length; i+=2) {
            String[] split = libs[i].split(":");
            load(new Dependency(
                    split[0],
                    split[1],
                    split[2],
                    libs[i+1]
            ));
        }
    }

    public static void load(Dependency d) {
//        LogUtils.info(String.format("Loading dependency %s:%s:%s", d.groupId, d.artifactId, d.version));
        String name = d.artifactId() + "-" + d.version();
        File saveLocation = new File(getLibFolder(d), name + ".jar");
        if (!saveLocation.exists()) {
            try {
                LogUtils.info("Dependency '" + name + "' is not already in the libraries folder. Attempting to download...");
                URL url = d.getUrl();
                try (InputStream is = url.openStream()) {
                    Files.copy(is, saveLocation.toPath());
                    LogUtils.info("Dependency '" + name + "' successfully downloaded.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!saveLocation.exists())
            throw new RuntimeException("Unable to download dependency: " + d);
        try {
            URL_INJECTOR.get().addURL(saveLocation.toURI().toURL());
        } catch (Exception e) {
            throw new RuntimeException("Unable to load dependency: " + saveLocation, e);
        }
    }

    @SuppressWarnings("all")
    private static File getLibFolder(Dependency dependency) {
        File pluginDataFolder = CustomNameplatesPlugin.getInstance().getDataFolder();
        File serverDir = pluginDataFolder.getParentFile().getParentFile();

        File helperDir = new File(serverDir, "libraries");
        String[] split = dependency.groupId().split("\\.");
        File jarDir;
        StringJoiner stringJoiner = new StringJoiner(File.separator);
        for (String str : split) {
            stringJoiner.add(str);
        }
        jarDir = new File(helperDir, stringJoiner + File.separator + dependency.artifactId + File.separator + dependency.version);
        jarDir.mkdirs();
        return jarDir;
    }

    public record Dependency(String groupId, String artifactId, String version, String repoUrl) {
            public Dependency(String groupId, String artifactId, String version, String repoUrl) {
                this.groupId = Objects.requireNonNull(groupId, "groupId");
                this.artifactId = Objects.requireNonNull(artifactId, "artifactId");
                this.version = Objects.requireNonNull(version, "version");
                this.repoUrl = Objects.requireNonNull(repoUrl, "repoUrl");
            }

            public URL getUrl() throws MalformedURLException {
                String repo = this.repoUrl;
                if (!repo.endsWith("/")) {
                    repo += "/";
                }
                repo += "%s/%s/%s/%s-%s.jar";

                String url = String.format(repo, this.groupId.replace(".", "/"), this.artifactId, this.version, this.artifactId, this.version);
                return new URL(url);
            }

            @Override
            public boolean equals(Object o) {
                if (o == this) return true;
                if (!(o instanceof Dependency other)) return false;
                return this.groupId().equals(other.groupId()) &&
                        this.artifactId().equals(other.artifactId()) &&
                        this.version().equals(other.version()) &&
                        this.repoUrl().equals(other.repoUrl());
            }

            @Override
            public int hashCode() {
                final int PRIME = 59;
                int result = 1;
                result = result * PRIME + this.groupId().hashCode();
                result = result * PRIME + this.artifactId().hashCode();
                result = result * PRIME + this.version().hashCode();
                result = result * PRIME + this.repoUrl().hashCode();
                return result;
            }

            @Override
            public String toString() {
                return "LibraryLoader.Dependency(" +
                        "groupId=" + this.groupId() + ", " +
                        "artifactId=" + this.artifactId() + ", " +
                        "version=" + this.version() + ", " +
                        "repoUrl=" + this.repoUrl() + ")";
            }
        }
}
