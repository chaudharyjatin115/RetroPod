pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { setUrl("https://jitpack.io") }
        maven("https://jogamp.org/deployment/maven")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://jitpack.io") }
        maven("https://jogamp.org/deployment/maven")
        maven(url = "https://raw.githubusercontent.com/bravepipeproject/maven-repo/master/repository")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

val coreDir = File(rootDir, "core")
val serviceDir = File(rootDir, "core/service")
val mediaDir = File(rootDir, "core/media")

rootProject.name = "RetroPod"
include(
    ":androidApp",
    ":composeApp",
    ":common",
    ":data",
    ":domain",
    ":ktorExt",
    ":kotlinYtmusicScraper",
    ":media3",
    ":media3-ui",
)

// core modules
project(":common").projectDir = File(coreDir, "common")
project(":data").projectDir = File(coreDir, "data")
project(":domain").projectDir = File(coreDir, "domain")

// service modules
project(":ktorExt").projectDir = File(serviceDir, "ktorExt")
project(":kotlinYtmusicScraper").projectDir = File(serviceDir, "kotlinYtmusicScraper")

// media modules
project(":media3").projectDir = File(mediaDir, "media3")
project(":media3-ui").projectDir = File(mediaDir, "media3-ui")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")