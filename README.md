## Android上一个识别率极高的二维码扫描库，兼容support版本，主要是针对老旧项目更新麻烦的问题

### JitPack:
Step 1. Add the JitPack repository in your root build.gradle at the end of repositories:

```gradle
allprojects {
    repositories {
        ...
        mavenCentral()
        maven { url "https://jitpack.io" }
    }
}
```

Step 2. Add the dependency

```gradle
dependencies {
    implementation 'com.github.MrDon94:QRScan:v1.0.1'
}
```
