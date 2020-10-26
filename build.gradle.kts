plugins {
    java
}

group = "ru.commandos"
version = "1.0.0"

repositories {
    mavenCentral()
}

// Fix for encoding problems
tasks.withType<JavaCompile> {
    options.encoding = "windows-1251"
}

dependencies {
    testImplementation("junit", "junit", "4.12")
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("com.google.code.gson:gson:2.8.6")
}
