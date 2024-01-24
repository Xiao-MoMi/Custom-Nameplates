# Custom-Nameplates

![CodeFactor Grade](https://img.shields.io/codefactor/grade/github/Xiao-MoMi/Custom-Nameplates)
![bStats Servers](https://img.shields.io/bstats/servers/16649)
![bStats Players](https://img.shields.io/bstats/players/16649)
![GitHub](https://img.shields.io/github/license/Xiao-MoMi/Custom-Nameplates)
[![](https://jitpack.io/v/Xiao-MoMi/Custom-Nameplates.svg)](https://jitpack.io/#Xiao-MoMi/Custom-Nameplates)
<a href="https://mo-mi.gitbook.io/xiaomomi-plugins/plugin-wiki/" alt="GitBook">
<img src="https://img.shields.io/badge/docs-gitbook-brightgreen" alt="Gitbook"/>
</a>

CustomNameplates is a Paper plugin that provides unlimited customization for nametags, bossbars, actionbars and chat bubbles.

## How to build

### Windows

#### Command Line
Install JDK 17 and set the JDK installation path to JAVA_HOME as an environment variable.\
Start powershell and change directory to the project folder.\
Execute ".\gradlew build" and get the jar at /target/CustomNameplates-universe-version.jar.

#### IDE
Import the project and execute gradle build action.

##### About Proxy
If you are using a proxy, configurate the proxy in gradle.properties. Otherwise comment the lines in gradle.properties.

## Support the developer

Polymart: https://polymart.org/resource/customnameplates.2543 \
Afdian: https://afdian.net/@xiaomomi

## Use CustomNameplates API

### Maven

```
<repositories>
  <repository>
    <id>jitpack</id>
    <url>https://jitpack.io/</url>
  </repository>
</repositories>
```
```
<dependencies>
  <dependency>
    <groupId>com.github.Xiao-MoMi</groupId>
    <artifactId>Custom-Nameplates</artifactId>
    <version>{LATEST}</version>
    <scope>provided</scope>
  </dependency>
</dependencies>
```
### Gradle (Groovy)

```
repositories {
    maven { url 'https://jitpack.io' }
}
```
```
dependencies {
    compileOnly 'com.github.Xiao-MoMi:Custom-Nameplates:{LATEST}'
}
```
### Gradle (Kotlin)

```
repositories {
    maven("https://jitpack.io/")
}
```
```
dependencies {
    compileOnly("com.github.Xiao-MoMi:Custom-Nameplates:{LATEST}")
}
```