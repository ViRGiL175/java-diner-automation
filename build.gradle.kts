plugins {
    java
}

group = "ru.commandos"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("junit", "junit", "4.12")
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
}
