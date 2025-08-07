plugins {
    id("io.github.goooler.shadow") version "8.1.8"
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/") // paper
    maven("https://repo.william278.net/releases/") // husk
    maven("https://jitpack.io/")
    maven("https://repo.oraxen.com/releases/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/") // papi
    maven("https://repo.essentialsx.net/releases/") // ess
    maven("https://repo.md-5.net/content/groups/public/") // disguise
    maven("https://repo.opencollab.dev/main/") // geyser
    maven("https://maven.enginehub.org/repo/") // worldguard worldedit
    maven("https://repo.alessiodp.com/releases/") // parties
    maven("https://maven.devs.beer/") // ia
}

dependencies {
    compileOnly(project(":api"))
    compileOnly(project(":backend"))
    compileOnly("dev.dejvokep:boosted-yaml:${rootProject.properties["boosted_yaml_version"]}")
    // WorldGuard
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.9")
    // Platform
    compileOnly("dev.folia:folia-api:${rootProject.properties["paper_version"]}-R0.1-SNAPSHOT")
    // Chat
    compileOnly(files("libs/VentureChat-3.7.1.jar"))
    compileOnly(files("libs/TrChat-2.0.11.jar"))
    compileOnly(files("libs/carbonchat-paper-3.0.0-beta.27.jar"))
    compileOnly(files("libs/AdvancedChat-1.3.7.jar"))
    compileOnly(files("libs/CMIAPI-9.7.4.1.jar"))
    compileOnly(files("libs/ChatControl-11.3.1.jar"))
    compileOnly(files("libs/Typewriter.jar"))
    compileOnly("net.william278.huskchat:huskchat-bukkit:3.0.4")
    compileOnly("net.essentialsx:EssentialsX:2.20.1")
    compileOnly("net.essentialsx:EssentialsXChat:2.20.1")
    compileOnly("com.alessiodp.parties:parties-api:3.2.16")
    compileOnly("com.alessiodp.parties:parties-bukkit:3.2.16")
//    compileOnly("com.github.Brikster:Chatty:v2.19.14")
    compileOnly(files("libs/Chatty-3.0.0-SNAPSHOT.jar"))
    // Emoji
    compileOnly("dev.lone:api-itemsadder:4.0.10")
    compileOnly("io.th0rgal:oraxen:1.182.0")
    // PAPI
    compileOnly("me.clip:placeholderapi:${rootProject.properties["placeholder_api_version"]}")
    // Disguise
    compileOnly("LibsDisguises:LibsDisguises:10.0.44")
    // Geyser
    compileOnly("org.geysermc.geyser:api:2.4.2-SNAPSHOT")
    // Floodgate
    compileOnly("org.geysermc.floodgate:api:2.2.3-SNAPSHOT")
    // Cosmetics
    compileOnly("com.github.FrancoBM12:API-MagicCosmetics:2.2.9")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(17)
    dependsOn(tasks.clean)
}