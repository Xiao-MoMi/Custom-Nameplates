# Custom-Nameplates

![CodeFactor Grade](https://img.shields.io/codefactor/grade/github/Xiao-MoMi/Custom-Nameplates)
<a href="https://mo-mi.gitbook.io/xiaomomi-plugins/plugin-wiki/customnameplates" alt="GitBook">
<img src="https://img.shields.io/badge/docs-gitbook-brightgreen" alt="Gitbook"/>
</a>
[![Scc Count Badge](https://sloc.xyz/github/Xiao-MoMi/Custom-Nameplates/?category=codes)](https://github.com/Xiao-MoMi/Custom-Nameplates/)
![Code Size](https://img.shields.io/github/languages/code-size/Xiao-MoMi/Custom-Nameplates)
![bStats Servers](https://img.shields.io/bstats/servers/16649)
![bStats Players](https://img.shields.io/bstats/players/16649)
![GitHub](https://img.shields.io/github/license/Xiao-MoMi/Custom-Nameplates)

## How to Build

#### Command Line
Install JDK 17 & 21. \
Start terminal and change directory to the project folder.\
Execute ".\gradlew build" and get the artifact under /target folder

#### IDE
Import the project and execute gradle build action. \
Get the artifact under /target folder

## How to Contribute

#### Translations
Clone this project and create a new language file in the /backend/src/main/resources/translations directory. \
Once your changes are ready, open a pull request for review. We appreciate your works!

## Support the Developer

Polymart: https://polymart.org/resource/customnameplates.2543/ \
BuiltByBit: https://builtbybit.com/resources/customnameplates.36359/ \
Afdian: https://afdian.com/@xiaomomi/

## CustomNameplates API

```kotlin
repositories {
    maven("https://repo.momirealms.net/releases/")
}
```
```kotlin
dependencies {
    compileOnly("net.momirealms:custom-nameplates:3.0.19")
}
```