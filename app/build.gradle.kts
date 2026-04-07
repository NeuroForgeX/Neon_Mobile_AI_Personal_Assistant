import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.forge.bright"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    dynamicFeatures.add(":litert_npu_runtime_libraries:mediatek_runtime")
    dynamicFeatures.add(":litert_npu_runtime_libraries:qualcomm_runtime_v69")
    dynamicFeatures.add(":litert_npu_runtime_libraries:qualcomm_runtime_v73")
    dynamicFeatures.add(":litert_npu_runtime_libraries:qualcomm_runtime_v75")
    dynamicFeatures.add(":litert_npu_runtime_libraries:qualcomm_runtime_v79")
    dynamicFeatures.add(":litert_npu_runtime_libraries:qualcomm_runtime_v81")
    tasks.withType<JavaCompile>().configureEach {
        options.compilerArgs.addAll(listOf("-Xlint:none"))
    }

    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            freeCompilerArgs.addAll(
                listOf(
                    "-Xskip-primitive-version-checks",
                    "-Xskip-metadata-version-check"
                )
            )
        }
    }

    defaultConfig {
        applicationId = "com.forge.bright"
        minSdk = 31
        targetSdk = 36
        versionCode = 2
        versionName = "1.0-rc1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            // NPU only supports 64-bit ARM
            abiFilters += listOf("arm64-v8a")
        }
    }

    // Signing configurations
    signingConfigs {
        create("release") {
            // For now, use debug keystore for release builds
            // TODO: Configure proper release keystore for production
            storeFile = file(System.getProperty("user.home") + "/.android/debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
        }

        release {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
        }

        create("releaseCandidate") {
            initWith(getByName("release"))
            versionNameSuffix = "-rc"
            isDebuggable = false
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlin {
        jvmToolchain(21)
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/DEPENDENCIES"
        }
        // Mandatory for Qualcomm NPU runtime libraries
        jniLibs {
            useLegacyPackaging = true
        }
    }
    buildFeatures {
        compose = true
        // Required if using certain native NPU libraries
        prefab = true
    }
    dependenciesInfo {
        includeInApk = false
        includeInBundle = true
    }
    buildToolsVersion = "36.1.0"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.documentfile)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.khushpanchal.ketch)
    implementation(libs.material)

    // Compose BOM and dependencies
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    
    // Splash Screen API
    implementation(libs.androidx.core.splashscreen)

    // LiteRT Accelerator Libraries
    implementation(libs.ai.litert.gpu)

    // LiteRTLM (Language Model) Libraries
    implementation(libs.litertlm.android)

    // Google AI Edge LiteRT Libraries
    implementation(libs.google.litert)

    // 2. LiteRT Play Services Runtime (Standardizes hardware access)
    implementation("com.google.android.gms:play-services-tflite-java:16.4.0")
    
    // 3. Play Services GPU Delegate (Fallback for MediaTek Mali GPU)
    implementation("com.google.android.gms:play-services-tflite-gpu:16.2.0")

    // Strings for NPU runtime libraries
    implementation(project(":litert_npu_runtime_libraries:runtime_strings"))

    // Gson for JSON parsing
    implementation(libs.gson)

    // Room Database
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.inline)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

}
