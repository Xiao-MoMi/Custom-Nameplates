/*
 * This file is part of LuckPerms, licensed under the MIT License.
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

package net.momirealms.customnameplates.common.dependency;

import net.momirealms.customnameplates.common.dependency.classloader.IsolatedClassLoader;
import net.momirealms.customnameplates.common.dependency.relocation.Relocation;
import net.momirealms.customnameplates.common.dependency.relocation.RelocationHandler;
import net.momirealms.customnameplates.common.plugin.NameplatesPlugin;
import net.momirealms.customnameplates.common.plugin.classpath.ClassPathAppender;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

/**
 * Loads and manages runtime dependencies for the plugin.
 */
public class DependencyManagerImpl implements DependencyManager {

    /** A registry containing plugin specific behaviour for dependencies. */
    private final DependencyRegistry registry;
    /** The path where library jars are cached. */
    private final Path cacheDirectory;
    /** The classpath appender to preload dependencies into */
    private final ClassPathAppender classPathAppender;
    /** A map of dependencies which have already been loaded. */
    private final Map<Dependency, Path> loaded = Collections.synchronizedMap(new EnumMap<>(Dependency.class));
    /** A map of isolated classloaders which have been created. */
    private final Map<Set<Dependency>, IsolatedClassLoader> loaders = new HashMap<>();
    /** Cached relocation handler instance. */
    private final RelocationHandler relocationHandler;
    private final Executor loadingExecutor;
    private final NameplatesPlugin plugin;

    /**
     * Constructs a DependencyManagerImpl instance
     *
     * @param plugin plugin
     */
    public DependencyManagerImpl(NameplatesPlugin plugin) {
        this.plugin = plugin;
        this.registry = new DependencyRegistry();
        this.cacheDirectory = setupCacheDirectory(plugin);
        this.classPathAppender = plugin.getClassPathAppender();
        this.loadingExecutor = plugin.getScheduler().async();
        this.relocationHandler = new RelocationHandler(this);
    }

    @Override
    public ClassLoader obtainClassLoaderWith(Set<Dependency> dependencies) {
        Set<Dependency> set = new HashSet<>(dependencies);

        for (Dependency dependency : dependencies) {
            if (!this.loaded.containsKey(dependency)) {
                throw new IllegalStateException("Dependency " + dependency + " is not loaded.");
            }
        }

        synchronized (this.loaders) {
            IsolatedClassLoader classLoader = this.loaders.get(set);
            if (classLoader != null) {
                return classLoader;
            }

            URL[] urls = set.stream()
                    .map(this.loaded::get)
                    .map(file -> {
                        try {
                            return file.toUri().toURL();
                        } catch (MalformedURLException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toArray(URL[]::new);

            classLoader = new IsolatedClassLoader(urls);
            this.loaders.put(set, classLoader);
            return classLoader;
        }
    }

    @Override
    public void loadDependencies(Collection<Dependency> dependencies) {
        CountDownLatch latch = new CountDownLatch(dependencies.size());

        for (Dependency dependency : dependencies) {
            if (this.loaded.containsKey(dependency)) {
                latch.countDown();
                continue;
            }

            this.loadingExecutor.execute(() -> {
                try {
                    loadDependency(dependency);
                } catch (Throwable e) {
                    this.plugin.getPluginLogger().warn("Unable to load dependency " + dependency.name(), e);
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void loadDependency(Dependency dependency) throws Exception {
        if (this.loaded.containsKey(dependency)) {
            return;
        }

        Path file = remapDependency(dependency, downloadDependency(dependency));

        this.loaded.put(dependency, file);

        if (this.classPathAppender != null && this.registry.shouldAutoLoad(dependency)) {
            this.classPathAppender.addJarToClasspath(file);
        }
    }

    private Path downloadDependency(Dependency dependency) throws DependencyDownloadException {
        String fileName = dependency.getFileName(null);
        Path file = this.cacheDirectory.resolve(dependency.toLocalPath()).resolve(fileName);
        cleanOutdatedVersions(file.getParent());

        // if the file already exists, don't attempt to re-download it.
        if (Files.exists(file)) {
            return file;
        }

        DependencyDownloadException lastError = null;
        String forceRepo = dependency.getRepo();
        List<DependencyRepository> repository = DependencyRepository.getByID(forceRepo);
        if (!repository.isEmpty()) {
            int i = 0;
            while (i < repository.size()) {
                try {
                    plugin.getPluginLogger().info("Downloading dependency(" + fileName + ")[" + repository.get(i).getUrl() + dependency.getMavenRepoPath() + "]");
                    repository.get(i).download(dependency, file);
                    plugin.getPluginLogger().info("Successfully downloaded " + fileName);
                    return file;
                } catch (DependencyDownloadException e) {
                    lastError = e;
                    i++;
                }
            }
        }
        throw Objects.requireNonNull(lastError);
    }

    private void cleanOutdatedVersions(Path currentVersionDirectory) {
        Path artifactDirectory = currentVersionDirectory.getParent();
        if (!Files.isDirectory(artifactDirectory)) return;

        Set<Path> loadedVersionDirectories = new HashSet<>();
        synchronized (this.loaded) {
            this.loaded.values().forEach(path -> loadedVersionDirectories.add(path.getParent()));
        }
        try (DirectoryStream<Path> directories = Files.newDirectoryStream(artifactDirectory)) {
            for (Path directory : directories) {
                if (!Files.isDirectory(directory) || directory.equals(currentVersionDirectory)
                        || loadedVersionDirectories.contains(directory)) continue;
                try (Stream<Path> paths = Files.walk(directory)) {
                    for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                        Files.delete(path);
                    }
                    this.plugin.getPluginLogger().info("Cleaned up outdated dependency " + directory);
                } catch (IOException | RuntimeException e) {
                    this.plugin.getPluginLogger().warn("Failed to clean outdated dependency " + directory, e);
                }
            }
        } catch (IOException e) {
            this.plugin.getPluginLogger().warn("Failed to clean outdated dependencies in " + artifactDirectory, e);
        }
    }

    private Path remapDependency(Dependency dependency, Path normalFile) throws Exception {
        List<Relocation> rules = new ArrayList<>(dependency.getRelocations());
        if (rules.isEmpty()) {
            return normalFile;
        }

        Path remappedFile = normalFile.resolveSibling(dependency.getFileName(DependencyRegistry.isGsonRelocated() ? "remapped-legacy" : "remapped"));

        // if the remapped source exists already, just use that.
        if (Files.exists(remappedFile)) {
            return remappedFile;
        }

        plugin.getPluginLogger().info("Remapping " + dependency.getFileName(null));
        relocationHandler.remap(normalFile, remappedFile, rules);
        plugin.getPluginLogger().info("Successfully remapped " + dependency.getFileName(null));
        return remappedFile;
    }

    private static Path setupCacheDirectory(NameplatesPlugin plugin) {
        Path cacheDirectory = plugin.getDataDirectory().resolve("libs");
        try {
            if (Files.exists(cacheDirectory) && (Files.isDirectory(cacheDirectory) || Files.isSymbolicLink(cacheDirectory))) {
                cleanDirectoryJars(plugin, cacheDirectory);
                return cacheDirectory;
            }

            try {
                Files.createDirectories(cacheDirectory);
            } catch (FileAlreadyExistsException e) {
                // ignore
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to create libs directory", e);
        }

        return cacheDirectory;
    }

    private static void cleanDirectoryJars(NameplatesPlugin plugin, Path directory) throws IOException {
        try (DirectoryStream<Path> files = Files.newDirectoryStream(directory, "*.jar")) {
            for (Path file : files) {
                if (!Files.isRegularFile(file)) continue;
                try {
                    Files.delete(file);
                } catch (IOException e) {
                    plugin.getPluginLogger().warn("Failed to clean legacy dependency " + file, e);
                }
            }
        }
    }

    @Override
    public void close() {
        IOException firstEx = null;

        for (IsolatedClassLoader loader : this.loaders.values()) {
            try {
                loader.close();
            } catch (IOException ex) {
                if (firstEx == null) {
                    firstEx = ex;
                } else {
                    firstEx.addSuppressed(ex);
                }
            }
        }

        if (firstEx != null) {
            plugin.getPluginLogger().severe(firstEx.getMessage(), firstEx);
        }
    }
}
