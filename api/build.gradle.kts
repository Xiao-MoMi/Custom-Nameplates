dependencies {
    compileOnly("dev.folia:folia-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.5")
    implementation(project(":common"))
    implementation("net.kyori:adventure-api:4.16.0")
}

tasks {
    shadowJar {
        relocate ("net.kyori", "net.momirealms.customnameplates.libraries")
    }
}