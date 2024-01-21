dependencies {
    implementation(project(":common"))
    // TAB
    compileOnly("me.neznamy:tab-api:4.0.2")
    // BungeeTabListPlus
    compileOnly("codecrafter47.bungeetablistplus:bungeetablistplus-api-bungee:3.6.4")
    // BungeeCord
    compileOnly("net.md-5:bungeecord-api:1.20-R0.2-SNAPSHOT")
    compileOnly("net.md-5:bungeecord-protocol:1.20-R0.2-SNAPSHOT")
    compileOnly("net.md-5:bungeecord-event:1.20-R0.2-SNAPSHOT")
    compileOnly("net.md-5:bungeecord-proxy:1.20-R0.2-SNAPSHOT")

    implementation("net.kyori:adventure-api:4.15.0")
    implementation("net.kyori:adventure-platform-bungeecord:4.3.2")
    implementation("net.kyori:adventure-text-minimessage:4.15.0")
    implementation("net.kyori:adventure-text-serializer-legacy:4.15.0")
}

tasks {
    shadowJar {
        relocate ("net.kyori", "net.momirealms.customnameplates.libraries")
    }
}