plugins {
    id("io.github.goooler.shadow") version "8.1.8"
}

repositories {
}

dependencies {
    compileOnly(project(":common"))
    compileOnly(project(":api"))
    // YAML
    compileOnly("dev.dejvokep:boosted-yaml:${rootProject.properties["boosted_yaml_version"]}")
    // Adventure
    compileOnly("net.kyori:adventure-api:${rootProject.properties["adventure_bundle_version"]}")
    compileOnly("net.kyori:adventure-text-minimessage:${rootProject.properties["adventure_bundle_version"]}")
    compileOnly("net.kyori:adventure-text-serializer-gson:${rootProject.properties["adventure_bundle_version"]}")
    // Gson
    compileOnly("com.google.code.gson:gson:${rootProject.properties["gson_version"]}")
    // Database
    compileOnly("org.xerial:sqlite-jdbc:${rootProject.properties["sqlite_driver_version"]}")
    compileOnly("com.h2database:h2:${rootProject.properties["h2_driver_version"]}")
    compileOnly("org.mongodb:mongodb-driver-sync:${rootProject.properties["mongodb_driver_version"]}")
    compileOnly("com.zaxxer:HikariCP:${rootProject.properties["hikari_version"]}")
    compileOnly("redis.clients:jedis:${rootProject.properties["jedis_version"]}")
}

tasks {
    shadowJar {
        relocate ("net.kyori", "net.momirealms.customnameplates.libraries")
    }
}