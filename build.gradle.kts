import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
   id("com.github.johnrengelman.shadow") version "8.1.1"
   kotlin("jvm") version "1.6.21"
}

group = "de.jakkoble"
version = "2.3"

repositories {
   mavenCentral()
}

repositories {
   maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
}

dependencies {
   compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
   implementation("com.github.twitch4j:twitch4j:1.15.0")
   implementation("org.jetbrains.kotlin:kotlin-stdlib:1.7.10")
   implementation("com.github.twitch4j:twitch4j:1.12.0")
   implementation("io.github.openfeign:feign-okhttp:12.3")
   implementation("io.github.openfeign:feign-slf4j:12.3")
   implementation("io.github.openfeign:feign-jackson:12.3")
   implementation("io.github.openfeign:feign-hystrix:12.3")
}

java {
   toolchain.languageVersion.set(JavaLanguageVersion.of(22))
}

tasks.withType<KotlinCompile> {
   kotlinOptions.jvmTarget = "1.8"
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
   jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
   jvmTarget = "1.8"
}