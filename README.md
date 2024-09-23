# SigNoz Mobile App Monitoring Monorepo

This repository contains multiple sample applications that demonstrate how to use SigNoz for mobile app monitoring. The applications are available for different platforms and languages.

## Submodules

This monorepo includes the following sample applications as submodules:

1. **Flutter (Android & iOS)**: [android-ios-flutter-signoz-sample-app](https://github.com/UnCool-0x/android-ios-flutter-signoz-sample-app)
2. **iOS (Swift)**: [ios-swift-signoz-sample-app](https://github.com/UnCool-0x/ios-swift-signoz-sample-app)
3. **Android (Kotlin)**: [android-kotlin-signoz-sample-app](https://github.com/UnCool-0x/android-kotlin-signoz-sample-app)
4. **Android (Java)**: [android-java-signoz-sample-app](https://github.com/UnCool-0x/android-java-signoz-sample-app)

## Cloning the Repository

To clone the entire monorepo with all submodules, use:
  ```bash
    git clone --recursive https://github.com/UnCool-0x/signoz-mobile-app-monitoring-monorepo
  ```

Alternatively, if you've already cloned the repository without submodules, you can initialize and update them using:
  ```bash
    git submodule update --init --recursive
```

## Cloning Individual Repositories

If you are only interested in one of the sample applications, you can clone the individual repositories directly:

- **Flutter (Android & iOS)**:
    ```bash
      git clone https://github.com/UnCool-0x/android-ios-flutter-signoz-sample-app
    ```
    
- **iOS (Swift)**:
  ```bash
      git clone https://github.com/UnCool-0x/ios-swift-signoz-sample-app
  ```
  
- **Android (Kotlin)**:
  ```bash
      git clone https://github.com/UnCool-0x/android-kotlin-signoz-sample-app

  ```
- **Android (Java)**:
  ```bash
      git clone https://github.com/UnCool-0x/android-java-signoz-sample-app
  ```

## Updating Submodules

If there are updates to any of the submodules, you can update them using the following command:

```bash
    git submodule update --remote
```
