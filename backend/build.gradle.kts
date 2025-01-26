repositories {
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    compileOnly(project(":api"))
    // YAML
    compileOnly("dev.dejvokep:boosted-yaml:${rootProject.properties["boosted_yaml_version"]}")
    // Adventure
    compileOnly("net.kyori:adventure-api:${rootProject.properties["adventure_bundle_version"]}")
    compileOnly("net.kyori:adventure-text-minimessage:${rootProject.properties["adventure_bundle_version"]}")
    compileOnly("net.kyori:adventure-text-serializer-gson:${rootProject.properties["adventure_bundle_version"]}")
    // Gson
    compileOnly("com.google.code.gson:gson:${rootProject.properties["gson_version"]}")
    // Database
    compileOnly("org.xerial:sqlite-jdbc:${rootProject.properties["sqlite_driver_version"]}")
    compileOnly("com.h2database:h2:${rootProject.properties["h2_driver_version"]}")
    compileOnly("org.mongodb:mongodb-driver-sync:${rootProject.properties["mongodb_driver_version"]}")
    compileOnly("com.zaxxer:HikariCP:${rootProject.properties["hikari_version"]}")
    compileOnly("redis.clients:jedis:${rootProject.properties["jedis_version"]}")
    // Cache
    compileOnly("com.github.ben-manes.caffeine:caffeine:${rootProject.properties["caffeine_version"]}")
    // COMMONS IO
    compileOnly("commons-io:commons-io:${rootProject.properties["commons_io_version"]}")
    // TTF
    compileOnly("org.lwjgl:lwjgl-freetype:${rootProject.properties["lwjgl_version"]}")
    compileOnly("org.lwjgl:lwjgl:${rootProject.properties["lwjgl_version"]}")
    // Fast util
    compileOnly("it.unimi.dsi:fastutil:${rootProject.properties["fastutil_version"]}")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(17)
    dependsOn(tasks.clean)
}
