import org.gradle.api.initialization.resolve.RepositoriesMode.FAIL_ON_PROJECT_REPOS

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://www.jitpack.io") }
    }
}

rootProject.name = "MyHappyBot"
include(":app")
include(":litert_npu_runtime_libraries:runtime_strings")
include(":litert_npu_runtime_libraries:mediatek_runtime")
//include(":litert_npu_runtime_libraries:qualcomm_runtime_v69")
//include(":litert_npu_runtime_libraries:qualcomm_runtime_v73")
//include(":litert_npu_runtime_libraries:qualcomm_runtime_v75")
//include(":litert_npu_runtime_libraries:qualcomm_runtime_v79")
//include(":litert_npu_runtime_libraries:qualcomm_runtime_v81")
 