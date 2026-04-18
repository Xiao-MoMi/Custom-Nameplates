rootProject.name = "CustomNameplates"
include(":api")
include(":backend")
include(":platforms:bukkit")
include(":platforms:bukkit:compatibility")
pluginManagement {
    plugins {
        kotlin("jvm") version "2.1.20"
    }
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}