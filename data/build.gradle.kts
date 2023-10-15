plugins {
    id("com.android.library")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
    id("kotlinx-serialization")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "wing.tree.bionda.data"
    compileSdk = 34

    defaultConfig {
        minSdk = 28

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.7"
    }

    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }
}

dependencies {
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    api("androidx.core:core-ktx:1.12.0")
    api("com.google.android.material:material:1.10.0")
    api("com.jakewharton.timber:timber:5.0.1")
    api("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.5")

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.compose.runtime:runtime:1.5.3")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.room:room-ktx:2.5.2")
    implementation("androidx.room:room-runtime:2.5.2")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.dagger:hilt-android:2.47")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.tickaroo.tikxml:annotation:0.8.13")
    //noinspection GradleDependency
    implementation("com.tickaroo.tikxml:core:0.8.13")
    //noinspection GradleDependency
    implementation("com.tickaroo.tikxml:retrofit-converter:0.8.13")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.22")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")

    kapt("com.google.dagger:hilt-android-compiler:2.47")
    //noinspection GradleDependency
    kapt("com.tickaroo.tikxml:processor:0.8.13")

    ksp("androidx.room:room-compiler:2.5.2")
    ksp("androidx.room:room-ktx:2.5.2")

    testImplementation("junit:junit:4.13.2")
}
