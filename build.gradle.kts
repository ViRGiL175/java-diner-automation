plugins {
    java
}

group = "ru.commandos"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(fileTree(mapOf("dir" to "lib", "include" to listOf("*.jar"))))
    implementation(files("/src/main/java/ru/virgil/java-diner-signal-source-2.3.0.jar"))
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}