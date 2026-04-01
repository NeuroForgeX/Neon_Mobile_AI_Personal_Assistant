plugins {
    id("com.android.library")
}

android {
    namespace = "com.forge.bright.litert_npu_runtime_libraries.runtime_strings"
    compileSdk = 36

    defaultConfig {
        minSdk = 30
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
        
        create("releaseCandidate") {
            initWith(getByName("release"))
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_24
        targetCompatibility = JavaVersion.VERSION_24
    }

    kotlin {
        jvmToolchain(24)
    }
}
