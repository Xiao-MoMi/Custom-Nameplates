dependencies {
    implementation(project(":common"))
    compileOnly("io.netty:netty-codec-http:4.1.105.Final")
    // velocity
    compileOnly("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")
    compileOnly("com.velocitypowered:velocity-proxy:3.3.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")
    // velocitab
    compileOnly("net.william278:velocitab:1.6.1")
}