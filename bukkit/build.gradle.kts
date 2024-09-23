plugins {
    id("io.github.goooler.shadow") version "8.1.8"
}

repositories {
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/") // papi
    maven("https://libraries.minecraft.net") // brigadier
    maven("https://jitpack.io/")
    maven("https://papermc.io/repo/repository/maven-public/") // paper
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") // spigot
}

dependencies {
    implementation(project(":api"))
    implementation(project(":common"))
    implementation(project(":backend"))

    compileOnly("dev.folia:folia-api:${rootProject.properties["paper_version"]}-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:${rootProject.properties["placeholder_api_version"]}")

    // YAML
    compileOnly("dev.dejvokep:boosted-yaml:${rootProject.properties["boosted_yaml_version"]}")

    // Adventure
    implementation("net.kyori:adventure-api:${rootProject.properties["adventure_bundle_version"]}")
    implementation("net.kyori:adventure-text-minimessage:${rootProject.properties["adventure_bundle_version"]}")
    implementation("net.kyori:adventure-platform-bukkit:${rootProject.properties["adventure_platform_version"]}")
    implementation("net.kyori:adventure-text-serializer-gson:${rootProject.properties["adventure_bundle_version"]}") {
        exclude("com.google.code.gson", "gson")
    }

    compileOnly("com.github.Xiao-MoMi:Sparrow-Heart:${rootProject.properties["sparrow_heart_version"]}")

    // Cloud
    compileOnly("org.incendo:cloud-core:${rootProject.properties["cloud_core_version"]}")
    compileOnly("org.incendo:cloud-minecraft-extras:${rootProject.properties["cloud_minecraft_extras_version"]}")
    compileOnly("org.incendo:cloud-paper:${rootProject.properties["cloud_paper_version"]}")
}

tasks {
    shadowJar {
        archiveFileName = "CustomNameplates-bukkit-${rootProject.properties["project_version"]}.jar"
        destinationDirectory.set(file("$rootDir/target"))
        relocate("net.kyori", "net.momirealms.customnameplates.libraries")
        relocate("org.incendo", "net.momirealms.customnameplates.libraries")
        relocate("dev.dejvokep", "net.momirealms.customnameplates.libraries")
        relocate ("org.apache.commons.pool2", "net.momirealms.customnameplates.libraries.commonspool2")
        relocate ("com.mysql", "net.momirealms.customnameplates.libraries.mysql")
        relocate ("org.mariadb", "net.momirealms.customnameplates.libraries.mariadb")
        relocate ("com.zaxxer.hikari", "net.momirealms.customnameplates.libraries.hikari")
        relocate ("com.mongodb", "net.momirealms.customnameplates.libraries.mongodb")
        relocate ("org.bson", "net.momirealms.customnameplates.libraries.bson")
        relocate ("org.bstats", "net.momirealms.customnameplates.libraries.bstats")
        relocate ("com.github.benmanes.caffeine", "net.momirealms.customnameplates.libraries.caffeine")
        relocate ("net.objecthunter.exp4j", "net.momirealms.customnameplates.libraries.exp4j")
        relocate ("redis.clients.jedis", "net.momirealms.customnameplates.libraries.jedis")
    }
}

artifacts {
    archives(tasks.shadowJar)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(17)
    dependsOn(tasks.clean)
}