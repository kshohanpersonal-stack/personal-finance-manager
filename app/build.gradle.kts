plugins {
    alias(libs.plugins.android-application)
    alias(libs.plugins.kotlin-android)
    alias(libs.plugins.kotlin-compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.shohankhan.ledgerly"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.shohankhan.ledgerly"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        resourceConfigurations += setOf("en")
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Reproducible OSS-style signing: the release APK is signed with the
            // public debug keystore so anyone can build & install an identical artifact.
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(libs.androidx-core-ktx)
    implementation(libs.androidx-core-splashscreen)
    implementation(libs.androidx-activity-compose)
    implementation(libs.androidx-lifecycle-runtime-ktx)
    implementation(libs.androidx-lifecycle-viewmodel-compose)
    implementation(libs.androidx-lifecycle-process)
    implementation(libs.androidx-navigation-compose)
    implementation(libs.androidx-biometric)
    implementation(libs.androidx-work-runtime-ktx)
    implementation(libs.kotlinx-coroutines-android)

    implementation(platform(libs.androidx-compose-bom))
    implementation(libs.androidx-compose-ui)
    implementation(libs.androidx-compose-ui-graphics)
    implementation(libs.androidx-compose-ui-tooling-preview)
    implementation(libs.androidx-compose-material3)
    implementation(libs.androidx-compose-material-icons-extended)

    implementation(libs.androidx-room-runtime)
    implementation(libs.androidx-room-ktx)
    ksp(libs.androidx-room-compiler)

    implementation(libs.androidx-datastore-preferences)

    debugImplementation(libs.androidx-compose-ui-tooling)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx-coroutines-test)
    testImplementation(libs.json)
}
