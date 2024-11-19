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

import net.momirealms.customnameplates.common.dependency.relocation.Relocation;
import net.momirealms.customnameplates.common.plugin.CustomNameplatesProperties;
import net.momirealms.customnameplates.common.util.Architecture;
import net.momirealms.customnameplates.common.util.Platform;
import org.jetbrains.annotations.Nullable;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * The dependencies used by CustomNameplates.
 */
public enum Dependency {

    ASM(
            "org.ow2.asm",
            "asm",
            "maven",
            "asm"
    ),
    ASM_COMMONS(
            "org.ow2.asm",
            "asm-commons",
            "maven",
            "asm-commons"
    ),
    JAR_RELOCATOR(
            "me.lucko",
            "jar-relocator",
            "maven",
            "jar-relocator"
    ),
    H2_DRIVER(
            "com.h2database",
            "h2",
            "maven",
            "h2-driver"
    ),
    SQLITE_DRIVER(
            "org.xerial",
            "sqlite-jdbc",
            "maven",
            "sqlite-driver"
    ),
    CLOUD_CORE(
            "org{}incendo",
            "cloud-core",
            "maven",
            "cloud-core",
            Relocation.of("cloud", "org{}incendo{}cloud"),
            Relocation.of("geantyref", "io{}leangen{}geantyref")
    ),
    CLOUD_BRIGADIER(
            "org{}incendo",
            "cloud-brigadier",
            "maven",
            "cloud-brigadier",
            Relocation.of("cloud", "org{}incendo{}cloud"),
            Relocation.of("geantyref", "io{}leangen{}geantyref")
    ),
    CLOUD_SERVICES(
            "org{}incendo",
            "cloud-services",
            "maven",
            "cloud-services",
            Relocation.of("cloud", "org{}incendo{}cloud"),
            Relocation.of("geantyref", "io{}leangen{}geantyref")
    ),
    CLOUD_BUKKIT(
            "org{}incendo",
            "cloud-bukkit",
            "maven",
            "cloud-bukkit",
            Relocation.of("cloud", "org{}incendo{}cloud"),
            Relocation.of("geantyref", "io{}leangen{}geantyref")
    ),
    CLOUD_PAPER(
            "org{}incendo",
            "cloud-paper",
            "maven",
            "cloud-paper",
            Relocation.of("cloud", "org{}incendo{}cloud"),
            Relocation.of("geantyref", "io{}leangen{}geantyref")
    ),
    CLOUD_SPONGE(
            "org{}incendo",
            "cloud-sponge7",
            "maven",
            "cloud-sponge7",
            Relocation.of("cloud", "org{}incendo{}cloud"),
            Relocation.of("geantyref", "io{}leangen{}geantyref")
    ),
    CLOUD_MINECRAFT_EXTRAS(
            "org{}incendo",
            "cloud-minecraft-extras",
            "maven",
            "cloud-minecraft-extras",
            Relocation.of("cloud", "org{}incendo{}cloud"),
            Relocation.of("adventure", "net{}kyori{}adventure"),
            Relocation.of("option", "net{}kyori{}option"),
            Relocation.of("examination", "net{}kyori{}examination"),
            Relocation.of("geantyref", "io{}leangen{}geantyref")
    ),
    GEANTY_REF(
            "io{}leangen{}geantyref",
            "geantyref",
            "maven",
            "geantyref",
            Relocation.of("geantyref", "io{}leangen{}geantyref")
    ),
    BOOSTED_YAML(
            "dev{}dejvokep",
            "boosted-yaml",
            "maven",
            "boosted-yaml",
            Relocation.of("boostedyaml", "dev{}dejvokep{}boostedyaml")
    ),
    BYTEBUDDY(
            "net{}bytebuddy",
            "byte-buddy",
            "maven",
            "byte-buddy",
            Relocation.of("bytebuddy", "net{}bytebuddy")
    ),
    MARIADB_DRIVER(
            "org{}mariadb{}jdbc",
            "mariadb-java-client",
            "maven",
            "mariadb-java-client",
            Relocation.of("mariadb", "org{}mariadb")
    ),
    MYSQL_DRIVER(
            "com{}mysql",
            "mysql-connector-j",
            "maven",
            "mysql-connector-j",
            Relocation.of("mysql", "com{}mysql")
    ),
    HIKARI_CP(
            "com{}zaxxer",
            "HikariCP",
            "maven",
            "hikari-cp",
            Relocation.of("hikari", "com{}zaxxer{}hikari")
    ),
    MONGODB_DRIVER_CORE(
            "org{}mongodb",
            "mongodb-driver-core",
            "maven",
            "mongodb-driver-core",
            Relocation.of("mongodb", "com{}mongodb"),
            Relocation.of("bson", "org{}bson")
    ),
    MONGODB_DRIVER_SYNC(
            "org{}mongodb",
            "mongodb-driver-sync",
            "maven",
            "mongodb-driver-sync",
            Relocation.of("mongodb", "com{}mongodb"),
            Relocation.of("bson", "org{}bson")
    ) {
        @Override
        public String getVersion() {
            return Dependency.MONGODB_DRIVER_CORE.getVersion();
        }
    },
    MONGODB_DRIVER_BSON(
            "org{}mongodb",
            "bson",
            "maven",
            "mongodb-bson",
            Relocation.of("mongodb", "com{}mongodb"),
            Relocation.of("bson", "org{}bson")
    ) {
        @Override
        public String getVersion() {
            return Dependency.MONGODB_DRIVER_CORE.getVersion();
        }
    },
    COMMONS_POOL_2(
            "org{}apache{}commons",
            "commons-pool2",
            "maven",
            "commons-pool",
            Relocation.of("commonspool2", "org{}apache{}commons{}pool2")
    ),
    BSTATS_BASE(
            "org{}bstats",
            "bstats-base",
            "maven",
            "bstats-base",
            Relocation.of("bstats", "org{}bstats")
    ),
    BSTATS_BUKKIT(
            "org{}bstats",
            "bstats-bukkit",
            "maven",
            "bstats-bukkit",
            Relocation.of("bstats", "org{}bstats")
    ) {
        @Override
        public String getVersion() {
            return Dependency.BSTATS_BASE.getVersion();
        }
    },
    BSTATS_SPONGE(
            "org{}bstats",
            "bstats-sponge",
            "maven",
            "bstats-sponge",
            Relocation.of("bstats", "org{}bstats")
    ) {
        @Override
        public String getVersion() {
            return Dependency.BSTATS_BASE.getVersion();
        }
    },
    GSON(
            "com.google.code.gson",
            "gson",
            "maven",
            "gson"
    ),
    CAFFEINE(
            "com{}github{}ben-manes{}caffeine",
            "caffeine",
            "maven",
            "caffeine",
            Relocation.of("caffeine", "com{}github{}benmanes{}caffeine")
    ),
    JEDIS(
            "redis{}clients",
            "jedis",
            "maven",
            "jedis",
            Relocation.of("jedis", "redis{}clients{}jedis"),
            Relocation.of("commonspool2", "org{}apache{}commons{}pool2")
    ),
    EXP4J(
            "net{}objecthunter",
            "exp4j",
            "maven",
            "exp4j",
            Relocation.of("exp4j", "net{}objecthunter{}exp4j")
    ),
    SLF4J_SIMPLE(
            "org.slf4j",
            "slf4j-simple",
            "maven",
            "slf4j_simple"
    ) {
        @Override
        public String getVersion() {
            return Dependency.SLF4J_API.getVersion();
        }
    },
    SLF4J_API(
            "org.slf4j",
            "slf4j-api",
            "maven",
            "slf4j"
    ),
    BYTE_BUDDY(
            "net{}bytebuddy",
            "byte-buddy",
            "maven",
            "byte-buddy",
            Relocation.of("bytebuddy", "net{}bytebuddy")
    ),
    COMMONS_IO(
            "commons-io",
            "commons-io",
            "maven",
            "commons-io",
            Relocation.of("commons", "org{}apache{}commons")
    ),
    LWJGL(
            "org{}lwjgl",
            "lwjgl",
            "maven",
            "lwjgl"
    ),
    LWJGL_NATIVES(
            "org{}lwjgl",
            "lwjgl",
            "maven",
            "lwjgl-natives-" + getNativesPath(),
            "-natives-" + getNativesPath()
    ) {
        @Override
        public String getVersion() {
            return Dependency.LWJGL.getVersion();
        }
    },
    LWJGL_FREETYPE(
            "org{}lwjgl",
            "lwjgl-freetype",
            "maven",
            "lwjgl-freetype"
    ) {
        @Override
        public String getVersion() {
            return Dependency.LWJGL.getVersion();
        }
    },
    LWJGL_FREETYPE_NATIVES(
            "org{}lwjgl",
            "lwjgl-freetype",
            "maven",
            "lwjgl-freetype-natives-" + getNativesPath(),
            "-natives-" + getNativesPath()
    ) {
        @Override
        public String getVersion() {
            return Dependency.LWJGL.getVersion();
        }
    };

    private final List<Relocation> relocations;
    private final String repo;
    private final String groupId;
    private final String artifactId;
    private final String customArtifactID;
    private String artifactIdSuffix;

    private static final String MAVEN_FORMAT = "%s/%s/%s/%s-%s.jar";

    Dependency(String groupId, String artifactId, String repo, String customArtifactID) {
        this(groupId, artifactId, repo, customArtifactID, new Relocation[0]);
    }

    Dependency(String groupId, String artifactId, String repo, String customArtifactID, Relocation... relocations) {
        this.artifactId = artifactId;
        this.artifactIdSuffix = "";
        this.groupId = groupId;
        this.relocations = new ArrayList<>(Arrays.stream(relocations).toList());
        this.repo = repo;
        this.customArtifactID = customArtifactID;
    }

    Dependency(String groupId, String artifactId, String repo, String customArtifactID, String artifactIdSuffix, Relocation... relocations) {
        this.artifactId = artifactId;
        this.artifactIdSuffix = artifactIdSuffix;
        this.groupId = groupId;
        this.relocations = new ArrayList<>(Arrays.stream(relocations).toList());
        this.repo = repo;
        this.customArtifactID = customArtifactID;
    }

    public void setArtifactIdSuffix(String artifactIdSuffix) {
        this.artifactIdSuffix = artifactIdSuffix;
    }

    public String getVersion() {
        return CustomNameplatesProperties.getValue(customArtifactID);
    }

    private static String rewriteEscaping(String s) {
        return s.replace("{}", ".");
    }

    public String getFileName(String classifier) {
        String name = customArtifactID.toLowerCase(Locale.ROOT).replace('_', '-');
        String extra = classifier == null || classifier.isEmpty()
                ? ""
                : "-" + classifier;
        return name + "-" + this.getVersion() + extra + ".jar";
    }

    String getMavenRepoPath() {
        return String.format(MAVEN_FORMAT,
                rewriteEscaping(groupId).replace(".", "/"),
                rewriteEscaping(artifactId),
                getVersion(),
                rewriteEscaping(artifactId),
                getVersion() + artifactIdSuffix
        );
    }

    public List<Relocation> getRelocations() {
        return this.relocations;
    }

    /**
     * Creates a {@link MessageDigest} suitable for computing the checksums
     * of dependencies.
     *
     * @return the digest
     */
    public static MessageDigest createDigest() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    public String getRepo() {
        return repo;
    }

    private static String getNativesPath() {
        String base = Platform.get().getNativePath();
        if (Architecture.get() != Architecture.X64) {
            base += "-" + Architecture.get().getNativePath();
        }
        return base;
    }
}
