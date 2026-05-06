plugins {
    alias(libs.plugins.android.application)
    // [Compose] Xoá 1 dòng dưới nếu dùng XML
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

// Tên app gốc — chỉ cần đổi ở đây
val baseAppName = "Template"

android {
    namespace = "com.one.tabb"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.one.tabb"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // gồm có 4 flavor: 
    // devFree | 1.0-dev
    // devPaid | 1.0-dev-paid
    // prodFree | 1.0
    // prodPaid | 1.0-paid
    flavorDimensions += listOf("env", "tier")
    productFlavors {
        create("dev") {
            dimension = "env"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
            resValue("string", "app_name", "Dev $baseAppName")
        }
        create("prod") {
            dimension = "env"
            resValue("string", "app_name", baseAppName)
        }
        create("free") {
            dimension = "tier"
        }
        create("paid") {
            dimension = "tier"
            versionNameSuffix = "-paid"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    
    buildFeatures {
        compose = true // [Compose] Xoá block buildFeatures này nếu dùng XML
        resValues = true
    }
}

// Đặt tên file APK output: AppName-v1.0(1)-devFree-debug.apk
androidComponents {
    onVariants { variant ->
        variant.outputs.forEach { output ->
            if (output is com.android.build.api.variant.impl.VariantOutputImpl) {
                val flavor = variant.flavorName ?: ""
                val buildType = variant.buildType ?: ""
                output.outputFileName.set(
                    "${baseAppName}-v${output.versionName.get()}" +
                    "(${output.versionCode.get()})-${flavor}-${buildType}.apk"
                )
            }
        }
    }
}

dependencies {
    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // [Compose] Xoá nhóm này nếu dùng XML
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    debugImplementation(libs.androidx.compose.ui.tooling)
    // [/Compose]

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.database)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.config)
}