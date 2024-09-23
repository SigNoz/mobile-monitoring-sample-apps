# SigNoz Samples: Mobile App Monitoring 

This is a monorepo consisting of all the sample mobile applications in different languages & frameworks which are already instrumented to work out of the box with SigNoz, for locally testing the applications you can clone the repository using 

```bash
  git clone --sparse https://github.com/SigNoz/mobile-monitoring-sample-apps.git
```

Move to the cloned folder

```bash
cd mobile-monitoring-sample-apps
```

For testing Android app built with Kotlin

```bash
git sparse-checkout set android-kotlin
```

then there will be a single folder for Kotlin sample app, you can change directory to it, using ```cd android-kotlin```
and run by following it's own instructions.

Similarly for testing Android app built with Java

```bash
git sparse-checkout set android-java
```

then there will be a single folder for Java sample app, you can change directory to it, using ```cd android-java```
and run by following it's own instructions.

Similarly for testing Android/iOS app built with Flutter

```bash
git sparse-checkout set android-ios-flutter
```

then there will be a single folder for Flutter sample app, you can change directory to it, using ```cd android-ios-flutter```
and run by following it's own instructions.

Similarly for testing iOS app built with Swift UI

```bash
git sparse-checkout set ios-swift
```

then there will be a single folder for Swift sample app, you can change directory to it, using ```cd ios-swift```
and run by following it's own instructions.
