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
    }
}

rootProject.name = "pool-scorekeeper"
include(":domain")

fun androidSdkPresent(): Boolean {
    val local = file("local.properties")
    if (local.exists()) {
        val properties = java.util.Properties()
        local.inputStream().use { properties.load(it) }
        val dir = properties.getProperty("sdk.dir")
        if (dir != null && file(dir).exists()) {
            return true
        }
    }
    val env = System.getenv("ANDROID_HOME") ?: System.getenv("ANDROID_SDK_ROOT")
    return env != null && file(env).exists()
}

if (androidSdkPresent()) {
    include(":app")
}
