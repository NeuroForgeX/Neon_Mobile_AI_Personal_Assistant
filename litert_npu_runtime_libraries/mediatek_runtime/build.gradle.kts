plugins { id("com.android.dynamic-feature") }

android {
    namespace = "com.google.ai.edge.litert.mediatek_runtime"
    compileSdk = 35

    defaultConfig { minSdk = 31 }

    buildTypes {
        debug {}
        release {}
        create("releaseCandidate") {}
    }

    sourceSets {
        getByName("main") {
            // let gradle pack the shared library into apk
            jniLibs.srcDirs("src/main/jni")
        }
    }
}

dependencies { implementation(project(":app")) }
