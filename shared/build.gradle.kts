import com.android.build.gradle.internal.utils.createPublishingInfoForApp
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    id("maven-publish")
}

group = "com.ic.tech"
version = "1.0.0"

kotlin {
    androidTarget {
        publishLibraryVariants("release", "debug")
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "SmartCardReader"
            isStatic = true

            export(libs.mvvm.core)
            export(libs.mvvm.flow)
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.mvvm.core)
            implementation(libs.mvvm.flow)
        }

        androidMain.dependencies {
            api(libs.mvvm.core)
            api(libs.mvvm.flow)
            api(libs.mvvm.flow.compose)

            api(libs.arrow.core)
            api(libs.arrow.fx.coroutines)

            api(libs.jmrtd)
            api(libs.prov)
        }

        iosMain.dependencies {
            api(libs.mvvm.core)
            api(libs.mvvm.flow)
        }
    }

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
}

android {
    namespace = "org.ic.tech.main.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/Vanhoai/KMP_ReadSC")
            credentials {
                username =
                    project.findProject("gpr.user") as String? ?: System.getenv("GITHUB_USERNAME")
                password =
                    project.findProject("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}