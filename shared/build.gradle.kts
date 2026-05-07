plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.android.library")
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
            // Export dependencies for iOS
            export("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
                api("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")

                // Ktor client
                implementation("io.ktor:ktor-client-core:2.3.8")
                implementation("io.ktor:ktor-client-content-negotiation:2.3.8")
                implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.8")
                implementation("io.ktor:ktor-client-logging:2.3.8")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
                implementation("io.ktor:ktor-client-android:2.3.8")
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                implementation("io.ktor:ktor-client-darwin:2.3.8")
            }
        }
    }
}

android {
    namespace = "com.astraloom.shared"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

// Xcode integration task
tasks.register("buildXcodeFramework") {
    val mode = System.getenv("CONFIGURATION") ?: "Debug"
    val sdkName = System.getenv("SDK_NAME") ?: "iphonesimulator"
    val frameworkName = "shared"

    val targetArch = when {
        sdkName.startsWith("iphoneos") -> "iosArm64"
        sdkName == "iphonesimulator" -> {
            val arch = System.getenv("ARCHS") ?: "arm64"
            if (arch.contains("arm64")) "iosSimulatorArm64" else "iosX64"
        }
        else -> "iosSimulatorArm64"
    }

    val frameworkPath = "build/bin/$targetArch/${mode.lowercase()}Framework/$frameworkName.framework"
    val targetDir = System.getenv("TARGET_BUILD_DIR") ?: "build/xcode-frameworks"
    val linkTask = "link${mode}Framework${targetArch.replaceFirstChar { it.uppercase() }}"

    dependsOn(linkTask)

    doLast {
        exec {
            commandLine("mkdir", "-p", targetDir)
        }
        exec {
            commandLine("rsync", "-av", frameworkPath, "$targetDir/")
        }
        println("✅ Framework copied to: $targetDir")
    }
}
