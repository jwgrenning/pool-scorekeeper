plugins {
    id("com.android.application")
}

android {
    namespace = "net.grenning.pool_scorekeeper"
    compileSdk = 35

    defaultConfig {
        applicationId = "net.grenning.pool_scorekeeper"
        minSdk = 24
        targetSdk = 35
        versionCode = 5
        versionName = "2.3"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(project(":domain"))
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
}
