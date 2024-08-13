dependencies {
    // server
    compileOnly("dev.folia:folia-api:1.20.1-R0.1-SNAPSHOT")

    // command
    compileOnly("dev.jorel:commandapi-bukkit-core:9.5.3")

    // packet
    compileOnly("com.comphenix.protocol:ProtocolLib:5.2.0-SNAPSHOT")

    // papi
    compileOnly("me.clip:placeholderapi:2.11.6")

    // config
    compileOnly("dev.dejvokep:boosted-yaml:1.3.6")

    // bStats
    compileOnly("org.bstats:bstats-bukkit:3.0.2")

    // team
    compileOnly("me.neznamy:tab-api:4.0.2")
    compileOnly(files("libs/CMI-API-9.6.5.0.jar"))

    // Gson
    compileOnly("com.google.code.gson:gson:2.10.1")

    // database
    compileOnly("org.xerial:sqlite-jdbc:3.45.3.0")
    compileOnly("com.h2database:h2:2.2.224")
    compileOnly("org.mongodb:mongodb-driver-sync:5.0.1")
    compileOnly("com.zaxxer:HikariCP:5.0.1")
    compileOnly("redis.clients:jedis:5.1.3")

    // others
    compileOnly("com.github.LoneDev6:api-itemsadder:3.5.0c-r5")
    compileOnly("io.th0rgal:oraxen:1.165.0")
    compileOnly("com.github.FrancoBM12:API-MagicCosmetics:2.2.5")
    compileOnly("commons-io:commons-io:2.15.1")
    compileOnly("org.geysermc.geyser:api:2.2.0-SNAPSHOT")
    compileOnly("LibsDisguises:LibsDisguises:10.0.42")

    // chat channels
    compileOnly(files("libs/VentureChat-3.7.1.jar"))
    compileOnly(files("libs/TrChat-2.0.11.jar"))
    compileOnly(files("libs/carbonchat-paper-3.0.0-beta.26.jar"))
    compileOnly(files("libs/AdvancedChat-1.3.7.jar"))
    compileOnly("net.william278:huskchat:2.7.1")

    // api module
    implementation(project(":api"))
    implementation(project(":common"))
    implementation("com.github.Xiao-MoMi:Sparrow-Heart:0.35")

    // adventure
    implementation("net.kyori:adventure-api:4.17.0")
    implementation("net.kyori:adventure-platform-bukkit:4.3.3")
}

tasks {
    shadowJar {
        relocate ("net.kyori", "net.momirealms.customnameplates.libraries")
        relocate ("org.bstats", "net.momirealms.customnameplates.libraries.bstats")
        relocate ("net.momirealms.sparrow.heart", "net.momirealms.customnameplates.libraries.sparrow")
        relocate ("org.apache.commons.pool2", "net.momirealms.customnameplates.libraries.commonspool2")
        relocate ("com.mysql", "net.momirealms.customnameplates.libraries.mysql")
        relocate ("org.mariadb", "net.momirealms.customnameplates.libraries.mariadb")
        relocate ("com.zaxxer.hikari", "net.momirealms.customnameplates.libraries.hikari")
        relocate ("redis.clients.jedis", "net.momirealms.customnameplates.libraries.jedis")
        relocate ("com.mongodb", "net.momirealms.customnameplates.libraries.mongodb")
        relocate ("org.bson", "net.momirealms.customnameplates.libraries.bson")
        relocate ("dev.jorel.commandapi", "net.momirealms.customnameplates.libraries.commandapi")
        relocate ("dev.dejvokep.boostedyaml", "net.momirealms.customnameplates.libraries.boostedyaml")
    }
}
