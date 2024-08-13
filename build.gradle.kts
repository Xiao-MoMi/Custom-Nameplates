plugins {
    id("java")
    id("application")
    id("maven-publish")
    id("io.github.goooler.shadow") version "8.1.7"
}

allprojects {

    version = "2.4.7"

    apply<JavaPlugin>()
    apply(plugin = "java")
    apply(plugin = "application")
    apply(plugin = "io.github.goooler.shadow")
    apply(plugin = "org.gradle.maven-publish")

    application {
        mainClass.set("")
    }

    repositories {
        mavenCentral()
        maven("https://maven.aliyun.com/repository/public/")
        maven("https://papermc.io/repo/repository/maven-public/")
        maven("https://oss.sonatype.org/content/groups/public/")
        maven("https://repo.dmulloy2.net/repository/public/")
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
        maven("https://repo.codemc.org/repository/maven-public/")
        maven("https://maven.enginehub.org/repo/")
        maven("https://jitpack.io/")
        maven("https://mvn.lumine.io/repository/maven-public/")
        maven("https://repo.rapture.pw/repository/maven-releases/")
        maven("https://nexus.phoenixdevt.fr/repository/maven-public/")
        maven("https://r.irepo.space/maven/")
        maven("https://repo.auxilor.io/repository/maven-public/")
        maven("https://betonquest.org/nexus/repository/betonquest/")
        maven("https://repo.william278.net/releases/")
        maven("https://repo.william278.net/velocity/")
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        maven("https://repo.minebench.de/")
        maven("https://repo.xenondevs.xyz/releases/")
        maven("https://repo.kryptonmc.org/releases")
        maven("https://repo.oraxen.com/releases/")
        maven("https://nexus.codecrafter47.de/content/repositories/public/")
        maven("https://repo.opencollab.dev/main/")
        maven("https://repo.md-5.net/content/groups/public/")
    }
}

subprojects {
    tasks.processResources {
        val props = mapOf("version" to version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("*plugin.yml") {
            expand(props)
        }
        filesMatching("bungee.yml") {
            expand(props)
        }
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(17)
    }

    tasks.shadowJar {
        destinationDirectory.set(file("$rootDir/target"))
        archiveClassifier.set("")
        archiveFileName.set("CustomNameplates-" + project.name + "-" + project.version + ".jar")
    }

    if ("api" == project.name) {
        publishing {
            publications {
                create<MavenPublication>("mavenJava") {
                    groupId = "net.momirealms"
                    artifactId = "CustomNameplates"
                    version = rootProject.version.toString()
                    artifact(tasks.shadowJar)
                }
            }
        }
    }
}