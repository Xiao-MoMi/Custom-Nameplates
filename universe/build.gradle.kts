dependencies {
    implementation(project(":api"))
    implementation(project(":common"))
    implementation(project(":paper"))
    implementation(project(":velocity"))
    implementation(project(":bungeecord"))
}

tasks {
    shadowJar {
        relocate ("net.kyori", "net.momirealms.customnameplates.libraries")
        relocate ("org.bstats", "net.momirealms.customnameplates.libraries.bstats")
        relocate ("net.momirealms.biomeapi", "net.momirealms.customnameplates.libraries.biomeapi")
    }
}