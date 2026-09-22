buildscript {
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.4.10")
    }
}

plugins {
    id("com.android.application") version "9.4.1" apply false
    id("net.bytebuddy.byte-buddy-gradle-plugin") version "1.18.14" apply false
}
