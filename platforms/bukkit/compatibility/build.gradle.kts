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
    maven("https://mvn.lib.co.nz/public") // disguise
    maven("https://repo.opencollab.dev/main/") // geyser
    maven("https://maven.enginehub.org/repo/") // worldguard worldedit
    maven("https://repo.alessiodp.com/releases/") // parties
    maven("https://maven.devs.beer/") // ia
    maven("https://repo.pinodev.it/releases/") // zelchat
    maven("https://repo.hibiscusmc.com/releases") // hmccosmetics
}

dependencies {
    compileOnly(project(":api"))
    compileOnly(project(":backend"))
    compileOnly("net.kyori:adventure-api:${rootProject.properties["adventure_bundle_version"]}")
    compileOnly("dev.dejvokep:boosted-yaml:${rootProject.properties["boosted_yaml_version"]}")
    // WorldGuard
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.9")
    // Platform
    compileOnly("dev.folia:folia-api:${rootProject.properties["paper_version"]}-R0.1-SNAPSHOT")
    // Chat
    compileOnly(files("libs/VentureChat-3.7.1.jar"))
    compileOnly(files("libs/TrChat-2.0.11.jar"))
    compileOnly(files("libs/AdvancedChat-1.3.7.jar"))
    compileOnly(files("libs/CMIAPI-9.7.4.1.jar"))
    compileOnly(files("libs/ChatControl-11.3.1.jar"))
    compileOnly(files("libs/Typewriter.jar"))
    compileOnly("de.hexaoxi:carbonchat-api:3.0.0-beta.36")
    compileOnly("net.william278.huskchat:huskchat-bukkit:3.0.4")
    compileOnly("net.essentialsx:EssentialsX:2.20.1") {
        exclude(module = "spigot-api")
    }
    compileOnly("net.essentialsx:EssentialsXChat:2.20.1") {
        exclude(module = "spigot-api")
    }
    compileOnly("com.alessiodp.parties:parties-api:3.2.16")
    compileOnly("com.alessiodp.parties:parties-bukkit:3.2.16")
    compileOnly("it.pino.zelchat:zelchat-api:2.0.0-pre-13")
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
    compileOnly("com.github.sculmix:API-ECosmetics:1.0.1")
    compileOnly("com.hibiscusmc:HMCCosmetics:2.8.3")
    compileOnly("me.lojosho:HibiscusCommons:0.8.3")
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
    options.release.set(21)
    dependsOn(tasks.clean)
}