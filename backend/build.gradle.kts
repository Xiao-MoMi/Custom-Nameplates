plugins {
    id("io.github.goooler.shadow") version "8.1.8"
}

repositories {
}

dependencies {
    implementation(project(":common"))
    implementation(project(":api"))
    // YAML
    compileOnly("dev.dejvokep:boosted-yaml:${rootProject.properties["boosted_yaml_version"]}")
    // Adventure
    implementation("net.kyori:adventure-api:${rootProject.properties["adventure_bundle_version"]}")
    compileOnly("net.kyori:adventure-text-minimessage:${rootProject.properties["adventure_bundle_version"]}")
    compileOnly("net.kyori:adventure-text-serializer-gson:${rootProject.properties["adventure_bundle_version"]}")
}

tasks {
    shadowJar {
        relocate ("net.kyori", "net.momirealms.customnameplates.libraries")
    }
}