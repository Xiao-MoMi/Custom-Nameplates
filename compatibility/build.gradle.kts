plugins {
    id("io.github.goooler.shadow") version "8.1.8"
}

repositories {
}

dependencies {
    compileOnly(project(":api"))
    compileOnly(project(":common"))
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(17)
    dependsOn(tasks.clean)
}