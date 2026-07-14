import com.github.megatronking.stringfog.plugin.StringFogExtension
import com.github.megatronking.stringfog.plugin.StringFogMode
import com.github.megatronking.stringfog.plugin.kg.RandomKeyGenerator

plugins {
    alias(libs.plugins.android.application)
    // [Compose] Xoá 1 dòng dưới nếu dùng XML
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

// Tên app gốc — chỉ cần đổi ở đây, xoá app_name trong strings.xml
val baseAppName = "Template"
// mã dự án
val projectId = "PI01"
val protectedAabName = "${projectId.replace(" ", "-")}-reschiper.aab"

apply(plugin = "stringfog")
apply(plugin = "io.github.goldfish07.reschiper")

configure<StringFogExtension> {
    implementation = "com.github.megatronking.stringfog.xor.StringFogImpl"
    enable = true
    fogPackages = arrayOf("com.one.tabb")
    kg = RandomKeyGenerator()
    mode = StringFogMode.bytes
}

configure<io.github.goldfish07.reschiper.plugin.Extension> {
    enableObfuscation = true
    obfuscationMode = "default"
    obfuscatedBundleName = protectedAabName
    whiteList = setOf(
        "res/raw",
        "res/raw/*",
        "res/xml",
        "res/xml/*",
        "*.R.raw.*",
        "*.R.xml.*",
        "*.R.string.google_api_key",
        "*.R.string.google_app_id",
        "*.R.string.default_web_client_id",
        "*.R.string.gcm_defaultSenderId",
        "*.R.string.ga_trackingId",
        "*.R.string.firebase_database_url",
        "*.R.string.google_crash_reporting_api_key",
        "*.R.string.google_storage_bucket",
        "*.R.integer.google_play_services_version",
        "*.R.string.project_id",
        "*.R.string.com.google.firebase.crashlytics.mapping_file_id",
        "*.R.bool.com.crashlytics.*",
        "*.R.string.com.crashlytics.*",
        "*.R.layout.tt_*",
        "*.R.layout.notification_*",
        "*.R.anim.tt_*",
        "*.R.drawable.tt_*",
        "*.R.string.tt_*",
        "*.R.color.tt_*",
        "*.R.dimen.tt_*",
        "*.R.integer.tt_*",
        "*.R.style.tt_*",
        "*.R.attr.tt_*",
        "*.R.*.tt_*",
        "res/*/tt_*",
        "*.R.*.pangle_*",
        "res/*/pangle_*",
        "*.R.*.bytedance_*",
        "res/*/bytedance_*"
    )
    mergeDuplicateResources = false
    enableFileFiltering = false
    enableFilterStrings = false
}

val versionMajor = 1
val versionMinor = 0
val versionPatch = 0
val versionBuild = 0

// CI (GitHub Actions) sets these; release AAB must be signed for Play Store upload.
val releaseKeystorePath = System.getenv("KEYSTORE_PATH")
val hasReleaseKeystore =
    !releaseKeystorePath.isNullOrBlank() && file(releaseKeystorePath!!).exists()

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
        versionCode = (versionMajor * 1000000) + (versionMinor * 10000) + (versionPatch * 100) + versionBuild
        versionName = "$versionMajor.$versionMinor.$versionPatch"

    }


    signingConfigs {
        if (hasReleaseKeystore) {
            create("release") {
                storeFile = file(releaseKeystorePath!!)
                storePassword =
                    System.getenv("KEYSTORE_PASSWORD")
                        ?: error("KEYSTORE_PASSWORD is required when KEYSTORE_PATH is set")
                keyAlias =
                    System.getenv("KEY_ALIAS") ?: error("KEY_ALIAS is required when KEYSTORE_PATH is set")
                keyPassword =
                    System.getenv("KEY_PASSWORD") ?: System.getenv("KEYSTORE_PASSWORD")
                            ?: error("KEY_PASSWORD or KEYSTORE_PASSWORD is required when KEYSTORE_PATH is set")
            }
        }
    }

    buildTypes {
        release {
            signingConfig = if (hasReleaseKeystore) {
                signingConfigs.getByName("release")
            } else {
                signingConfigs.getByName("debug")
            }
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // gồm có 4 flavor: 
    // devFree | 1.0.0-dev
    // devPaid | 1.0.0-dev-paid
    // prodFree | 1.0.0
    // prodPaid | 1.0.0-paid
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
        buildConfig = true
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
                    "${projectId}-v${output.versionName.get()}" +
                    "(${output.versionCode.get()})-${flavor}-${buildType}.apk"
                )
            }
        }
    }
}

dependencies {
    implementation("com.github.megatronking.stringfog:xor:5.0.0")

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
