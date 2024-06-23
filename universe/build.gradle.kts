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