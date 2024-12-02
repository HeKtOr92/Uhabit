plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.uhabit"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.uhabit"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }


}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("androidx.recyclerview:recyclerview:1.3.0")
    implementation("androidx.room:room-runtime:2.5.2")
    annotationProcessor("androidx.room:room-compiler:2.5.2")
    implementation("com.jakewharton.threetenabp:threetenabp:1.4.4")
    implementation("androidx.viewpager2:viewpager2:1.1.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")

    // Exclude any support libraries if pulled transitively
    configurations.all {
        exclude(group = "com.android.support")
    }
}
