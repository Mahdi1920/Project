pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        // ⭐ ADD THIS ⭐
        maven { url = uri("https://plugins.gradle.org/m2/")  }
        maven { url = uri("https://repo.osgeo.org/repository/release/") }
//        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://maven.maptiler.com/repository/maven-public/") }
        maven { url = uri( "https://maven.maplibre.org/releases") }
    }
}

rootProject.name = "Livraison Project"
include(":app")