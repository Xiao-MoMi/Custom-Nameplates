/*
 *  Copyright (C) <2024> <XiaoMoMi>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
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

    /**
     * Constructs a Dependency with the given group ID, artifact ID, repository, and custom artifact ID.
     *
     * @param groupId         the group ID of the dependency
     * @param artifactId      the artifact ID of the dependency
     * @param repo            the repository for the dependency
     * @param customArtifactID the custom artifact ID for the dependency
     */
    Dependency(String groupId, String artifactId, String repo, String customArtifactID) {
        this(groupId, artifactId, repo, customArtifactID, new Relocation[0]);
    }

    /**
     * Constructs a Dependency with the given group ID, artifact ID, repository, custom artifact ID, and relocations.
     *
     * @param groupId         the group ID of the dependency
     * @param artifactId      the artifact ID of the dependency
     * @param repo            the repository for the dependency
     * @param customArtifactID the custom artifact ID for the dependency
     * @param relocations     any relocations associated with the dependency
     */
    Dependency(String groupId, String artifactId, String repo, String customArtifactID, Relocation... relocations) {
        this.artifactId = artifactId;
        this.artifactIdSuffix = "";
        this.groupId = groupId;
        this.relocations = new ArrayList<>(Arrays.stream(relocations).toList());
        this.repo = repo;
        this.customArtifactID = customArtifactID;
    }

    /**
     * Constructs a Dependency with the given group ID, artifact ID, repository, custom artifact ID, and relocations.
     *
     * @param groupId         the group ID of the dependency
     * @param artifactId      the artifact ID of the dependency
     * @param repo            the repository for the dependency
     * @param customArtifactID the custom artifact ID for the dependency
     * @param artifactIdSuffix the custom artifact suffix
     * @param relocations     any relocations associated with the dependency
     */
    Dependency(String groupId, String artifactId, String repo, String customArtifactID, String artifactIdSuffix, Relocation... relocations) {
        this.artifactId = artifactId;
        this.artifactIdSuffix = artifactIdSuffix;
        this.groupId = groupId;
        this.relocations = new ArrayList<>(Arrays.stream(relocations).toList());
        this.repo = repo;
        this.customArtifactID = customArtifactID;
    }

    /**
     * Sets the artifact suffix
     *
     * @param artifactIdSuffix the artifact suffix
     */
    public void setArtifactIdSuffix(String artifactIdSuffix) {
        this.artifactIdSuffix = artifactIdSuffix;
    }

    /**
     * Returns the version of the dependency based on the custom artifact ID.
     *
     * @return the version of the dependency
     */
    public String getVersion() {
        return CustomNameplatesProperties.getValue(customArtifactID);
    }

    private static String rewriteEscaping(String s) {
        return s.replace("{}", ".");
    }

    /**
     * Returns the filename for the dependency's JAR file, optionally with a classifier.
     *
     * @param classifier the classifier for the JAR (e.g., "sources" or "javadoc")
     * @return the filename for the JAR
     */
    public String getFileName(String classifier) {
        String name = customArtifactID.toLowerCase(Locale.ROOT).replace('_', '-');
        String extra = classifier == null || classifier.isEmpty()
                ? ""
                : "-" + classifier;
        return name + "-" + this.getVersion() + extra + ".jar";
    }

    /**
     * Returns the Maven repository path for the dependency.
     *
     * @return the Maven repository path
     */
    String getMavenRepoPath() {
        return String.format(MAVEN_FORMAT,
                rewriteEscaping(groupId).replace(".", "/"),
                rewriteEscaping(artifactId),
                getVersion(),
                rewriteEscaping(artifactId),
                getVersion() + artifactIdSuffix
        );
    }

    /**
     * Returns the list of relocations for the dependency.
     *
     * @return a list of {@link Relocation} objects
     */
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

    /**
     * Gets the repo
     *
     * @return the repo
     */
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
