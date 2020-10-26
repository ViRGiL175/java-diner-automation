plugins {
    application
}

group = "ru.commandos"
version = "1.0.0"

repositories {
    mavenCentral()
}

application {
    mainClass.set("ru.commandos.Main")
}

// Fix for encoding problems
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

dependencies {
    testImplementation("junit", "junit", "4.12")
    implementation(fileTree(mapOf("dir" to "lib", "include" to listOf("*.jar"))))
    implementation("com.google.code.gson:gson:2.8.6")
}
