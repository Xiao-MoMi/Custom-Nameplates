plugins {
    id("maven-publish")
}

repositories {
    maven("https://jitpack.io/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    // Adventure
    implementation("net.kyori:adventure-api:${rootProject.properties["adventure_bundle_version"]}")
    compileOnly("net.kyori:adventure-text-minimessage:${rootProject.properties["adventure_bundle_version"]}")
    compileOnly("net.kyori:adventure-text-serializer-gson:${rootProject.properties["adventure_bundle_version"]}")
    compileOnly("net.kyori:adventure-text-minimessage:${rootProject.properties["adventure_bundle_version"]}")
    compileOnly("net.kyori:adventure-text-serializer-gson:${rootProject.properties["adventure_bundle_version"]}")
    compileOnly("net.kyori:adventure-text-serializer-json-legacy-impl:${rootProject.properties["adventure_bundle_version"]}")
    // YAML
    implementation(files("libs/boosted-yaml-${rootProject.properties["boosted_yaml_version"]}.jar"))
    // Cache
    compileOnly("com.github.ben-manes.caffeine:caffeine:${rootProject.properties["caffeine_version"]}")
    // Netty
    compileOnly("io.netty:netty-all:4.1.117.Final")
    // GSON
    compileOnly("com.google.code.gson:gson:${rootProject.properties["gson_version"]}")
    // Fast util
    compileOnly("it.unimi.dsi:fastutil:${rootProject.properties["fastutil_version"]}")
    // Command
    compileOnly("org.incendo:cloud-core:${rootProject.properties["cloud_core_version"]}")
    compileOnly("org.incendo:cloud-minecraft-extras:${rootProject.properties["cloud_minecraft_extras_version"]}")
    // Logger
    compileOnly("org.slf4j:slf4j-api:${rootProject.properties["slf4j_version"]}")
    compileOnly("org.apache.logging.log4j:log4j-core:${rootProject.properties["log4j_version"]}")
    // Expression
    compileOnly("net.objecthunter:exp4j:${rootProject.properties["exp4j_version"]}")
    // code generator
    compileOnly("net.bytebuddy:byte-buddy:${rootProject.properties["byte_buddy_version"]}")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
    withSourcesJar()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(17)
    dependsOn(tasks.clean)
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        archiveFileName = "custom-nameplates-${rootProject.properties["project_version"]}.jar"
        relocate ("net.kyori", "net.momirealms.customnameplates.libraries")
        relocate("dev.dejvokep", "net.momirealms.customnameplates.libraries")
    }
}

publishing {
    repositories {
        maven {
            url = uri("https://repo.momirealms.net/releases")
            credentials(PasswordCredentials::class) {
                username = System.getenv("REPO_USERNAME")
                password = System.getenv("REPO_PASSWORD")
            }
        }
    }
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = "net.momirealms"
            artifactId = "custom-nameplates"
            version = rootProject.properties["project_version"].toString()
            artifact(tasks["sourcesJar"])
            from(components["shadow"])
            pom {
                name = "CustomNameplates API"
                url = "https://github.com/Xiao-MoMi/Custom-Nameplates"
                licenses {
                    license {
                        name = "GNU General Public License v3.0"
                        url = "https://www.gnu.org/licenses/gpl-3.0.html"
                        distribution = "repo"
                    }
                }
            }
        }
    }
}
