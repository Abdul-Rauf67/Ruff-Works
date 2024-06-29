plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    id("kotlin-android")
    id("kotlin-kapt")

}

android {
    namespace = "com.example.agrilinkup"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.agrilinkup"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        dataBinding = true
        viewBinding = true
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

kapt {
    correctErrorTypes = true
}
dependencies {

    //for firebase
    implementation(platform("com.google.firebase:firebase-bom:33.1.1"))
    implementation("com.google.firebase:firebase-core:21.1.1")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.android.gms:play-services-tasks:18.2.0")

    // Country Codes
    implementation("com.hbb20:ccp:2.7.3")

    // image picker
    //implementation ("com.github.Dhaval2404:ImagePicker:v2.1")
    implementation("com.github.dhaval2404:imagepicker:2.1")
    implementation("androidx.activity:activity-ktx:1.9.0")
    implementation("androidx.compose.foundation:foundation-android:1.6.8")
    implementation("androidx.compose.foundation:foundation-layout-android:1.6.8")

    //navigation components
    val nav_version = "2.7.7"
    implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
    implementation("androidx.navigation:navigation-ui-ktx:$nav_version")


    //lifeCycle
    val lifecycle_version = "2.8.2"
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-common-java8:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")

    //hilt
    // implementation("dagger.hilt:hilt-lifecycle-viewmodel")
    implementation("com.google.dagger:hilt-android:2.51.1")
    ksp("com.google.dagger:hilt-compiler:2.51.1")

    //Responsiveness
    implementation("com.intuit.ssp:ssp-android:1.1.0")
    implementation("com.intuit.sdp:sdp-android:1.1.0")

    //Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")

    //Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    //Country code picker
    implementation("com.hbb20:ccp:2.7.3")

    //Circle image view
    implementation("de.hdodenhof:circleimageview:3.1.0")

    implementation("com.airbnb.android:lottie:6.4.0")

    //Gson
    implementation("com.google.code.gson:gson:2.10.1")

    //Shimmer effect
    implementation("com.facebook.shimmer:shimmer:0.5.0")

    //Chat voice player
    implementation("com.github.JagarYousef:ChatVoicePlayer:1.1.0")

    //Chat audio record view with animations
    implementation("com.github.3llomi:RecordView:3.1.3")

    //Dots Indicator for Viewpager + Recyclerview
    implementation("me.relex:circleindicator:2.1.6")

    //Carousel recycler view
    implementation("com.github.sparrow007:carouselrecyclerview:1.2.6")


    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity-ktx:1.9.0")
    implementation("androidx.fragment:fragment-ktx:1.8.1")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation("androidx.activity:activity-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.2")
    implementation("com.google.android.gms:play-services-cast-framework:21.5.0")
    testImplementation("junit:junit:4.13.2")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
