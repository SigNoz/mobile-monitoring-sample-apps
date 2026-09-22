plugins {
    id("com.android.application")
    id("net.bytebuddy.byte-buddy-gradle-plugin")
}

android {
    namespace = "com.example.androidkotlindemo"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.example.androidkotlindemo"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // OpenTelemetry Android agent: traces, logs and metrics over OTLP/HTTP.
    implementation(platform("io.opentelemetry.android:opentelemetry-android-bom:1.7.0-alpha"))
    implementation("io.opentelemetry.android:android-agent")

    // Rewrites android.util.Log calls into OpenTelemetry log records at build time.
    implementation("io.opentelemetry.android.instrumentation:android-log-library")
    byteBuddy("io.opentelemetry.android.instrumentation:android-log-agent:1.7.0-alpha")

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
